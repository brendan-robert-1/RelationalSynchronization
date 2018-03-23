/**
 *
 */
package net.evolveip.crawlers.external.archival.statementwriting;

import org.apache.commons.lang.Validate;

import net.evolveip.crawlers.external.DBVendor;
import net.evolveip.crawlers.external.archival.statementwriting.db2.DB2StatementWriterFactory;
import net.evolveip.crawlers.external.archival.statementwriting.mysql.MySqlStatementWriterFactory;
import net.evolveip.crawlers.external.archival.statementwriting.postgresql.PostgresqlStatementWriterFactory;
import net.evolveip.crawlers.external.archival.statementwriting.sqlserver.SqlServerStatementWriterFactory;

/**
 * @author brobert
 *
 */
public abstract class StatementWriterFactory {

	public abstract StatementWriter getUpdateStatementWriter();



	public abstract StatementWriter getInsertStatementWriter();



	public abstract StatementWriter getDeleteStatementWriter();



	/**
	 * @param vendorType2
	 * @return
	 */
	public static StatementWriterFactory getFactory(DBVendor vendorType, boolean setStrNullToDBNull) {
		Validate.notNull(vendorType);
		StatementWriterFactory factory = null;
		switch (vendorType) {
			case DB2:
				factory = new DB2StatementWriterFactory(setStrNullToDBNull);
				break;
			case MYSQL:
				factory = new MySqlStatementWriterFactory(setStrNullToDBNull);
				break;
			case POSTGRESQL:
				factory = new PostgresqlStatementWriterFactory(setStrNullToDBNull);
				break;
			case SQLSERVER:
				factory = new SqlServerStatementWriterFactory(setStrNullToDBNull);
				break;
			default:
				throw new IllegalArgumentException("No factory could be provided for " + vendorType);
		}
		return factory;
	}

}
