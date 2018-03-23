/**
 *
 */
package net.evolveip.crawlers.external.archival.statementwriting.mysql;

import net.evolveip.crawlers.external.archival.statementwriting.StatementWriter;

/**
 * @author brobert
 *
 */
public abstract class MySqlStatementWriter implements StatementWriter {

	public String escapeTicks(String unescapedStr) {
		return unescapedStr == null ? null : unescapedStr.replaceAll("['\"\\\\]", "\\\\$0");
	}

}
