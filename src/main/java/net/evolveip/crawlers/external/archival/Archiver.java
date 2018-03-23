package net.evolveip.crawlers.external.archival;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.evolveip.crawlers.external.ConnectionManager;
import net.evolveip.crawlers.external.Record;
import net.evolveip.crawlers.external.Table;
import net.evolveip.crawlers.external.archival.NoveltyValidator.RecordMutationPredicate;
import net.evolveip.crawlers.external.archival.statementwriting.StatementWriter;
import net.evolveip.crawlers.external.archival.statementwriting.StatementWriterFactory;

/**
 * Archiver is one of our two main classes in the Retrieve Archive model for
 * crawling. All tables are archived by this archiver. We use our
 * noveltyValidator to determine if a record matches another record. We then
 * either update existing records whose data has changed, insert new records,
 * and delete no longer existing records.
 *
 * @author brobert
 *
 */
public class Archiver {

	private static final Logger logger = LoggerFactory.getLogger(Archiver.class);

	/**
	 * We use the novelty validator in every step of the archival process to
	 * determine if a record exists compared to another set of records.
	 */
	private NoveltyValidator noveltyValidator;

	/**
	 * Existing table in the database and new data we retrieved.
	 */
	private Table tableToArchive, existingTableInDb;

	private boolean setStrNullToDBNull;



	public Archiver(boolean setStrNullToDBNull) {
		this.setStrNullToDBNull = setStrNullToDBNull;
	}



	/**
	 * Archiving involves updating existing records with new values, inserting
	 * entirely new records. And deleting records that no longer exist.
	 *
	 * @param table
	 */
	public void archive(Table table) {
		logger.info("Beginning archive of " + table.getSchematic().tableName());
		Validate.notNull(table);
		tableToArchive = table;
		existingTableInDb = new ExistingTableFetcher(tableToArchive.getSchematic()).getExistingTable();
		noveltyValidator = new NoveltyValidator(existingTableInDb, tableToArchive);

		StatementWriterFactory factory = StatementWriterFactory.getFactory(ConnectionManager.instance().getVendorType(), setStrNullToDBNull);
		logger.info("Deleting no longer existing records...");
		int deletedCount = procesesRecordMutations(noveltyValidator.deletePredicate, factory.getDeleteStatementWriter(), existingTableInDb, tableToArchive);
		logger.info("Updating records that have changed...");
		int updatedCount = procesesRecordMutations(noveltyValidator.updatePredicate, factory.getUpdateStatementWriter(), tableToArchive, existingTableInDb);
		logger.info("Inserting new records...");
		int insertedCount = procesesRecordMutations(noveltyValidator.insertPredicate, factory.getInsertStatementWriter(), tableToArchive, existingTableInDb);

		logger.info("------------Arhive Details for " + tableToArchive.getSchematic().tableName() + "----------");
		logger.info("Records Deleted: {}", deletedCount);
		logger.info("Records Updated: {}", updatedCount);
		logger.info("Records Inserted: {}", insertedCount);
		logger.info("--------------------------------------");
		existingTableInDb = null;
		noveltyValidator = null;
		tableToArchive = null;
	}



	/**
	 * This method process all record mutations. We pass in the mutation
	 * predicate, the sql statement writer, the table we are processing, and the
	 * table we compare to. The last two paramaters are neccesary because for
	 * inserting and updating we process the NEW table and compare those records
	 * to the OLD table. For deletion however we process the existing records
	 * and compare them to the new ones to see if we should delete.
	 *
	 * @param mutatePredicate
	 * @param writer
	 * @return
	 */
	private int procesesRecordMutations(RecordMutationPredicate mutatePredicate, StatementWriter writer, Table tableToProcess, Table tableToCompare) {
		int batchSize = 1000;
		int recordsProcessed = 0;
		try (Connection conn = ConnectionManager.instance().getConnection()) {
			conn.setAutoCommit(true);
			Statement stmt = conn.createStatement();
			int currentBatchSize = 0, batchNumber = 1;
			for (Record record : tableToProcess.getRecords()) {

				if (mutatePredicate.shouldMutate(record)) {
					String updateStr = writer.writeSqlStatement(record, tableToCompare.getSchematic());
					stmt.addBatch(updateStr);

					if (currentBatchSize == batchSize) {
						logger.info("Submitting batch " + batchNumber + " for the " + tableToArchive.getSchematic().tableName() + " table, with "
								+ currentBatchSize
								+ " statements...");
						stmt.executeBatch();
						logger.info("Finished executing batch " + batchNumber);
						ConnectionManager.closeCloseables(stmt);
						stmt = conn.createStatement();
						currentBatchSize = 0;
						batchNumber++;
					}
					recordsProcessed++;
					currentBatchSize++;
				}
			}
			if (currentBatchSize != 0) {
				stmt.executeBatch();
				logger.info("Executed batch " + batchNumber + " with " + currentBatchSize + " statements.");
				ConnectionManager.closeCloseables(stmt);
			}
		} catch (BatchUpdateException be) {
			be.printStackTrace();
			System.err.println(be.getErrorCode() + " " + be.getMessage());

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return recordsProcessed;
	}

}
