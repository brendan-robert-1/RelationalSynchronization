/**
 *
 */
package net.evolveip.crawlers.external.archival.statementwriting.sqlserver;

import net.evolveip.crawlers.external.archival.statementwriting.StatementWriter;
import net.evolveip.crawlers.external.archival.statementwriting.StatementWriterFactory;

/**
 * @author brobert
 *
 */
public class SqlServerStatementWriterFactory extends StatementWriterFactory {

	/**
	 * @param setStrNullToDBNull
	 */
	public SqlServerStatementWriterFactory(boolean setStrNullToDBNull) {}



	/* (non-Javadoc)
	 * @see net.evolveip.crawlers.external.archival.statementwriting.StatementWriterFactory#getUpdateStatementWriter()
	 */
	@Override
	public StatementWriter getUpdateStatementWriter() {
		return null;
	}



	/* (non-Javadoc)
	 * @see net.evolveip.crawlers.external.archival.statementwriting.StatementWriterFactory#getInsertStatementWriter()
	 */
	@Override
	public StatementWriter getInsertStatementWriter() {
		return null;
	}



	/* (non-Javadoc)
	 * @see net.evolveip.crawlers.external.archival.statementwriting.StatementWriterFactory#getDeleteStatementWriter()
	 */
	@Override
	public StatementWriter getDeleteStatementWriter() {
		return null;
	}

}
