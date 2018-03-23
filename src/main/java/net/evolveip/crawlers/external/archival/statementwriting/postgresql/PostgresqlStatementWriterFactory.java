/**
 *
 */
package net.evolveip.crawlers.external.archival.statementwriting.postgresql;

import net.evolveip.crawlers.external.archival.statementwriting.StatementWriter;
import net.evolveip.crawlers.external.archival.statementwriting.StatementWriterFactory;

/**
 * @author brobert
 *
 */
public class PostgresqlStatementWriterFactory extends StatementWriterFactory {

	/**
	 * @param setStrNullToDBNull
	 */
	public PostgresqlStatementWriterFactory(boolean setStrNullToDBNull) {}



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
