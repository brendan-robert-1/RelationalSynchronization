/**
 *
 */
package net.evolveip.crawlers.external;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a relational table which includes a list of records. In
 * order for a table to be instantiated succesfully its list of records must all
 * match up to the column names in the table meta data and in the right order.
 * In addition the records all must obviously have the same number of elements
 * as specified in the tables meta data.
 *
 * @author brobert
 *
 */
public class Table {

	private static final Logger logger = LoggerFactory.getLogger(Table.class);

	private TableSchematic schematic;

	private Map<String, Record> records = new TreeMap<>();



	public Table(TableSchematic schematic) {
		Validate.notNull(schematic);
		this.schematic = schematic;
	}



	public void addRecord(Record record) {
		//TODO record validation
		String primaryKey = record.getPrimaryKey();
		records.put(primaryKey, record);
	}



	public Collection<Record> getRecords() {
		return records.values();
	}



	public TableSchematic getSchematic() {
		return schematic;
	}



	/**
	 * @param primaryKey
	 * @return
	 */
	public Record getRecordFromPrimaryKey(String primaryKey) {
		return records.get(primaryKey);
	}



	@Override
	protected void finalize() throws Throwable {
		logger.debug(schematic.tableName() + " succesfully garbage collected.");
	}
}
