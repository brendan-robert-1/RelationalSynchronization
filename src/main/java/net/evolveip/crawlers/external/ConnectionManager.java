/**
 *
 */
package net.evolveip.crawlers.external;

import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author brobert
 *
 */
public class ConnectionManager {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
	private String jdbcUrl, user, pw;

	private DBVendor dbType;

	private static ConnectionManager instance;



	public static void init(String jdbcUrl, String user, String pw, DBVendor dbType) {
		instance = new ConnectionManager(jdbcUrl, user, pw, dbType);
	}



	public static ConnectionManager instance() {
		if (instance == null) {
			throw new RuntimeException("Connection Manager not initialized, please initalize.");
		} else {
			return instance;
		}
	}



	/**
	 * @param jdbcUrl
	 * @param user
	 * @param pw
	 * @param dbType
	 */
	private ConnectionManager(String jdbcUrl, String user, String pw, DBVendor dbType) {
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.pw = pw;
		this.dbType = dbType;
	}



	public Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName(getClassForName());
			conn = DriverManager.getConnection(jdbcUrl, user, pw);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (conn == null) {
			logger.warn("Returning a null connection, could not get a connection.");
		}
		return conn;
	}



	/**
	 * @return
	 */
	private String getClassForName() {
		switch (dbType) {
			case DB2:
				return "com.ibm.db2.jcc.DB2Driver";
			case MYSQL:
				return "com.mysql.jdbc.Driver";
			case POSTGRESQL:
				return "org.postgresql.Driver";
			case SQLSERVER:
				return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			default:
				throw new IllegalArgumentException("Could not get a class for name for " + dbType);

		}
	}



	public static void closeCloseables(AutoCloseable... ac) {
		for (AutoCloseable a : ac) {
			try {
				a.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



	public DBVendor getVendorType() {
		return dbType;
	}

}
