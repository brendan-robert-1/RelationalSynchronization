package net.evolveip.crawlers.external.archival;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashSet;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import net.evolveip.crawlers.external.ConnectionManager;
import net.evolveip.crawlers.external.Record;
import net.evolveip.crawlers.external.Table;
import net.evolveip.crawlers.external.TableSchematic;

/**
 * Existing Record fetcher binds with the Table and Record model to query a
 * database to build a Table in memory. This table will be used to determine if
 * we need to insert, update, or delete all given records. We assume string
 * value for everything and assume the table schematic binds 1 to 1 with the
 * table and column names of the database. extract data.
 *
 * @author brobert
 *
 */
public class ExistingTableFetcher {
	private static final Logger logger = LoggerFactory.getLogger(ExistingTableFetcher.class);
	private Table existingTable;



	public ExistingTableFetcher(TableSchematic metaData) {
		Validate.notNull(metaData);
		existingTable = fetchExistingTable(metaData);
	}



	/**
	 * Here we are getting the existing data in Table form. We use the table
	 * name and columns in the schematic to build our select.
	 *
	 * @param tableName
	 * @param columnNames
	 * @return
	 */
	private Table fetchExistingTable(TableSchematic schematic) {
		Table existingTable = new Table(schematic);
		LinkedHashSet<String> orderedColumnNames = new LinkedHashSet<>(Sets.union(schematic.nonPrimaryKeyColumns(), schematic.uniquePrimarykeyColumns()));
		String query = getQuery(orderedColumnNames, schematic.tableName());
		try (
				Connection validConn = ConnectionManager.instance().getConnection();
				PreparedStatement ps = validConn.prepareStatement(query);
				ResultSet resultSet = ps.executeQuery();) {
			while (resultSet.next()) {
				Record.Builder builder = Record.newBuilder(schematic);
				for (String column : orderedColumnNames) {
					builder.addColumnData(column, resultSet.getString(column));
				}
				existingTable.addRecord(builder.build());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return existingTable;
	}



	/**
	 * Constructs a uniform select statement based on the table schematic.
	 *
	 * @param tableName
	 * @param columnNames
	 * @return
	 */
	private String getQuery(LinkedHashSet<String> orderedColumnNames, String tableName) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");

		int count = 1;
		for (String columnName : orderedColumnNames) {
			sb.append(columnName);
			if (count++ != orderedColumnNames.size()) {
				sb.append(", ");
			}
		}

		sb.append(" from " + tableName);
		logger.debug(sb.toString());
		return sb.toString();
	}



	public Table getExistingTable() {
		return existingTable;
	}



	public void setExistingDetails(Table existingTable) {
		this.existingTable = existingTable;
	}

}
