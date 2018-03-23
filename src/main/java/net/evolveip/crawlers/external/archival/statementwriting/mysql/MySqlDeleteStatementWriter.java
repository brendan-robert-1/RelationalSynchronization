/**
 *
 */
package net.evolveip.crawlers.external.archival.statementwriting.mysql;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.evolveip.crawlers.external.Record;
import net.evolveip.crawlers.external.TableSchematic;

/**
 * @author brobert
 *
 */
public class MySqlDeleteStatementWriter extends MySqlStatementWriter {
	private static final Logger logger = LoggerFactory.getLogger(MySqlDeleteStatementWriter.class);
	private boolean setStrNullToDBNull;



	public MySqlDeleteStatementWriter(boolean setStrNullToDBNull) {
		this.setStrNullToDBNull = setStrNullToDBNull;
	}



	@Override
	public String writeSqlStatement(Record record, TableSchematic metaData) {
		StringBuilder sb = new StringBuilder();
		sb.append("delete from " + metaData.tableName() + " where ");

		int count = 0;
		Set<Map.Entry<String, String>> entrySet = record.getAllPrimaryKeys().entrySet();
		for (Map.Entry<String, String> entry : entrySet) {
			String escaped = escapeTicks(entry.getValue());
			sb.append(entry.getKey() + " = '" + escaped + "' ");
			if (++count != entrySet.size() && entrySet.size() > 1) {
				sb.append(" and ");
			}

		}
		logger.debug(sb.toString());
		return sb.toString();
	}

}
