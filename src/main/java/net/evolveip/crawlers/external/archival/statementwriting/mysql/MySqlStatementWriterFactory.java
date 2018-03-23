/**
 *
 */
package net.evolveip.crawlers.external.archival.statementwriting.mysql;

import net.evolveip.crawlers.external.archival.statementwriting.StatementWriter;
import net.evolveip.crawlers.external.archival.statementwriting.StatementWriterFactory;

/**
 * @author brobert
 *
 */
public class MySqlStatementWriterFactory extends StatementWriterFactory {
	private boolean setStrNullToDBNull;



	public MySqlStatementWriterFactory(boolean setStrNullToDBNull) {
		this.setStrNullToDBNull = setStrNullToDBNull;
	}



	@Override
	public StatementWriter getUpdateStatementWriter() {
		return new MySqlUpdateStatementWriter(setStrNullToDBNull);
	}



	@Override
	public StatementWriter getInsertStatementWriter() {
		return new MySqlInsertStatementWriter(setStrNullToDBNull);
	}



	@Override
	public StatementWriter getDeleteStatementWriter() {
		return new MySqlDeleteStatementWriter(setStrNullToDBNull);
	}

}
