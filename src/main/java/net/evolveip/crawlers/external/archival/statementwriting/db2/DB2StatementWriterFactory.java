/**
 *
 */
package net.evolveip.crawlers.external.archival.statementwriting.db2;

import net.evolveip.crawlers.external.archival.statementwriting.StatementWriter;
import net.evolveip.crawlers.external.archival.statementwriting.StatementWriterFactory;

/**
 * @author brobert
 *
 */
public class DB2StatementWriterFactory extends StatementWriterFactory {

	public DB2StatementWriterFactory(boolean setStrNullToDBNull) {

	}



	@Override
	public StatementWriter getUpdateStatementWriter() {
		return null;
	}



	@Override
	public StatementWriter getInsertStatementWriter() {
		return null;
	}



	@Override
	public StatementWriter getDeleteStatementWriter() {
		return null;
	}

}
