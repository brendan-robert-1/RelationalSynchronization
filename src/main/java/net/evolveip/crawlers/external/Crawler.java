package net.evolveip.crawlers.external;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.evolveip.crawlers.external.archival.Archiver;
import net.evolveip.crawlers.external.retrieval.TableRetriever;

/**
 * Represents our crawler. He takes requests in the form of vendors and collects
 * all table data for the requested vendor and archives.
 *
 * @author brobert
 *
 */
public class Crawler {

	private static final Logger logger = LoggerFactory.getLogger(Archiver.class);

	private String nameForLogs = "Crawler"; //A name for logging purposes.

	private String jdbcUrl; //the fully qualified jdbc url that we will be archiving to.

	private String user; //The username to the database that we will be archiving to.

	private String pw; //The password to the database that we will be archiving to

	private DBVendor dbVendor; //This enum represents a Database Vendor such as MYSQL DB2 etc. This is used for actually getting the connection

	private boolean setStrNullToDBNull = true;

	private Map<TableSchematic, TableRetriever> bindedTables = new LinkedHashMap<>();



	private Crawler() {

	}



	/**
	 * Represents the crawling processes. For each of the {@link TableSchematic}
	 * and {@link TableRetriever} combos that are binded to the crawler we
	 * retrieve the {@link Table} and archive it.
	 */
	public void crawl() {
		ConnectionManager.init(jdbcUrl, user, pw, dbVendor);
		Archiver archiver = new Archiver(setStrNullToDBNull);
		logger.info("Beginning {}", nameForLogs);
		for (TableSchematic tableSchematic : bindedTables.keySet()) {
			try {
				TableRetriever retriever = bindedTables.get(tableSchematic);
				Table tableToArchive = retriever.retrieveTable(tableSchematic);
				archiver.archive(tableToArchive);
				tableToArchive = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.info("{} has finished.", nameForLogs);
	}



	/**
	 * Only way to get a Crawler instance
	 *
	 * @return A new Builder instance.
	 */
	public static Builder newBuilder() {
		return new Builder();
	}



	public boolean isSetStrNullToDBNull() {
		return setStrNullToDBNull;
	}





	public static class Builder {

		private Crawler crawler;



		private Builder() {
			crawler = new Crawler();
		}



		

		public Builder nameForLogs(String nameForLogs) {
			crawler.nameForLogs = nameForLogs;
			return this;
		}



	
		public Builder jdbcUrl(String jdbcUrl) {
			Validate.notEmpty(jdbcUrl);
			crawler.jdbcUrl = jdbcUrl;
			return this;
		}


		public Builder user(String user) {
			Validate.notEmpty(user);
			crawler.user = user;
			return this;
		}



		public Builder pw(String pw) {
			Validate.notEmpty(pw);
			crawler.pw = pw;
			return this;
		}



		
		public Builder dbVendor(DBVendor dbVendor) {
			crawler.dbVendor = dbVendor;
			return this;
		}



		public Builder nullStringToDBNull(boolean setStrNullToDBNull) {
			crawler.setStrNullToDBNull = setStrNullToDBNull;
			return this;
		}



		/**
		 * This is the main method that we use to bind a table schematic to a
		 * retriever that will be run when we crawl. They are run the order that
		 * they are binded to the crawler
		 *
		 * @param schematic
		 * @param retriever
		 * @return
		 */
		public Builder bind(TableSchematic schematic, TableRetriever retriever) {
			Validate.notNull(schematic);
			Validate.notNull(retriever);
			crawler.bindedTables.put(schematic, retriever);
			return this;
		}



		public Crawler build() {
			Validate.notEmpty(crawler.user);
			Validate.notEmpty(crawler.jdbcUrl);
			Validate.notEmpty(crawler.pw);
			Validate.notNull(crawler.dbVendor);
			return crawler;
		}
	}

}
