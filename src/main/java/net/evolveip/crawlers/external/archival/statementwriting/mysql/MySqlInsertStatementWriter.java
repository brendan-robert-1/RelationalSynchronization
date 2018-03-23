/**
 *
 */
package net.evolveip.crawlers.external.archival.statementwriting.mysql;

import java.util.LinkedHashSet;
import java.util.Map;

import com.google.common.collect.Sets;

import net.evolveip.crawlers.external.Record;
import net.evolveip.crawlers.external.TableSchematic;

/**
 * @author brobert
 *
 */
public class MySqlInsertStatementWriter extends MySqlStatementWriter {
	private boolean setStrNullToDBNull;



	public MySqlInsertStatementWriter(boolean setStrNullToDBNull) {
		this.setStrNullToDBNull = setStrNullToDBNull;
	}



	/* (non-Javadoc)
	 * @see net.evolveip.crawlers.external.archival.StatementWriter#writeSqlStatement(net.evolveip.crawlers.external.Record, net.evolveip.crawlers.external.TableSchematic)
	 */
	@Override
	public String writeSqlStatement(Record record, TableSchematic metaData) {
		StringBuilder sb = new StringBuilder();
		sb.append("Insert into " + metaData.tableName() + " (");

		int counter = 1;
		LinkedHashSet<String> allColumnNames = new LinkedHashSet<>(Sets.union(metaData.nonPrimaryKeyColumns(), metaData.uniquePrimarykeyColumns()));
		for (String columnName : allColumnNames) {
			sb.append(columnName);
			if (counter++ != allColumnNames.size()) {
				sb.append(", ");
			}
		}

		sb.append(") Values (");
		Map<String, String> fields = record.getAllFields();
		counter = 0;
		for (String columnName : allColumnNames) {
			String escapedVal = escapeTicks(fields.get(columnName));
			String sqlFormattedVal = null;
			if (setStrNullToDBNull && escapedVal.equals("null")) {
				sqlFormattedVal = escapedVal;
			} else {
				sqlFormattedVal = "'" + escapedVal + "'";
			}
			sb.append(sqlFormattedVal);
			if (++counter != fields.keySet().size()) {
				sb.append(", ");
			}
		}
		sb.append(")");
		return sb.toString();

	}

}
