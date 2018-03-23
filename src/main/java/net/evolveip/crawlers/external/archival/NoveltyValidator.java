package net.evolveip.crawlers.external.archival;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.evolveip.crawlers.external.Record;
import net.evolveip.crawlers.external.Table;

/**
 * We use novelty validator in all 3 steps of the archival process. When we are
 * updating existing records we need to check if a given new record is novel. If
 * it is we update, if it not we insert. Then we use the existing record no
 * longer exists method when deleting to determine if, for each existing record,
 * if it exists in the current pull set of records.
 *
 * Key thing to note is that the reason this is neccesary is because the
 * defenition of "is novel" is different in every context based on business
 * logic. We typically classify a subset of columns in a resultset to test
 * equality. For example we may consider a record novel if there doesnt exist in
 * the current archived set, a record with matching account id, product type,
 * and entity identifier for example, but the exact fields differ for each
 * implementation of a crawler.
 *
 * @author brobert
 *
 */
public class NoveltyValidator {
	private static final Logger logger = LoggerFactory.getLogger(NoveltyValidator.class);
	/**
	 * Function that determines if, for a given record in the new table, we
	 * should update its sister record in the existing table.
	 */
	public RecordMutationPredicate updatePredicate = (r) -> shouldUpdate(r);

	/**
	 * Function that determines if, for a given record in the new table, we
	 * should insert it into the database.
	 */
	public RecordMutationPredicate insertPredicate = (r) -> shouldInsert(r);

	/**
	 * Function that determines if a given record in the exising table no longer
	 * exists in the new table and should be deleted.
	 */
	public RecordMutationPredicate deletePredicate = (r) -> shouldDelete(r);

	private Table existingTable, newTable;



	public NoveltyValidator(Table existingTable, Table newTable) {
		this.existingTable = existingTable;
		this.newTable = newTable;

	}



	private boolean shouldUpdate(Record newRecord) {
		boolean shouldUpdate = false;
		String primaryKey = newRecord.getPrimaryKey();
		Record oldRecord = existingTable.getRecordFromPrimaryKey(primaryKey);
		if (oldRecord != null && dataHasChanged(oldRecord, newRecord)) {
			shouldUpdate = true;
		}
		return shouldUpdate;
	}



	private boolean shouldDelete(Record existingRecord) {
		boolean shouldDelete = false;
		String primaryKey = existingRecord.getPrimaryKey();
		Record newRecord = newTable.getRecordFromPrimaryKey(primaryKey);
		if (newRecord == null) {
			logger.debug("Should delete existingRecord: " + existingRecord.getPrimaryKey() + " becuase we couldnt find an entry in new table with this Id.");
			shouldDelete = true;
		}
		return shouldDelete;
	}



	private boolean shouldInsert(Record newRecord) {
		boolean shouldInsert = false;
		String primrayKey = newRecord.getPrimaryKey();
		Record oldRecord = existingTable.getRecordFromPrimaryKey(primrayKey);
		if (oldRecord == null) {
			shouldInsert = true;
		}
		return shouldInsert;
	}





	/**
	 * This functional interface represents the predicate that decides if a
	 * given record should be mutated (update, insert, or delete)
	 *
	 * @author brobert
	 *
	 */
	@FunctionalInterface
	interface RecordMutationPredicate {
		public boolean shouldMutate(Record record);
	}



	/**
	 * @param existingRecord
	 * @param newRecord
	 * @return
	 */
	private boolean dataHasChanged(Record existingRecord, Record newRecord) {
		boolean dataHasChanged = false;
		for (Map.Entry<String, String> entry : existingRecord.getNonPKFields().entrySet()) {
			String oldValue = entry.getValue();
			String newValue = newRecord.getNonPKFields().get(entry.getKey());
			if (oldValue.equals(newValue) == false) {
				logger.debug("Data has changed from: " + oldValue + " to " + newValue + " for " + entry.getKey());
				dataHasChanged = true;
				break;
			}
		}
		return dataHasChanged;
	}
}
