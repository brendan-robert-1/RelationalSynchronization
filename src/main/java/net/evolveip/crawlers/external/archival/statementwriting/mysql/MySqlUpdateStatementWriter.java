/**
 *
 */
package net.evolveip.crawlers.external.archival.statementwriting.mysql;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.evolveip.crawlers.external.Record;
import net.evolveip.crawlers.external.TableSchematic;

/**
 * @author brobert
 *
 */
public class MySqlUpdateStatementWriter extends MySqlStatementWriter {
	private static final Logger logger = LoggerFactory.getLogger(MySqlUpdateStatementWriter.class);
	private boolean setStrNullToDBNull;



	public MySqlUpdateStatementWriter(boolean setStrNullToDBNull) {
		this.setStrNullToDBNull = setStrNullToDBNull;
	}



	@Override

	public String writeSqlStatement(Record record, TableSchematic metaData) {
		StringBuilder sb = new StringBuilder();
		sb.append("update " + metaData.tableName() + " set ");

		int count = 0;
		Map<String, String> nonPkFields = record.getNonPKFields();
		for (Map.Entry<String, String> entry : nonPkFields.entrySet()) {
			String escaped = escapeTicks(entry.getValue());
			String sqlFormattedVal = null;
			if (setStrNullToDBNull && escaped.equals("null")) {
				sqlFormattedVal = escaped;
			} else {
				sqlFormattedVal = "'" + escaped + "'";
			}
			sb.append(entry.getKey() + " = " + sqlFormattedVal);
			if (++count != nonPkFields.size()) {
				sb.append(", ");
			}
		}

		sb.append(" where ");

		Map<String, String> pkFields = record.getAllPrimaryKeys();
		count = 0;
		for (Map.Entry<String, String> entry : pkFields.entrySet()) {
			String escaped = escapeTicks(entry.getValue());
			sb.append(entry.getKey() + " = '" + escaped + "'");
			if (++count != pkFields.size() && pkFields.size() > 1) {
				sb.append(" and ");
			}
		}
		return sb.toString();
	}
}
