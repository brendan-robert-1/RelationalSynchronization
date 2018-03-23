/**
 *
 */
package net.evolveip.crawlers.external;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.Validate;

/**
 * w Record in this case represents any data type that can be expressed as a
 * record in a relational database. All data types are normalized to String.
 *
 * @author brobert
 *
 */
public class Record {

	private Map<String, String> fields = new TreeMap<>();

	private Map<String, String> primaryKeys = new TreeMap<>();



	/**
	 * @return
	 */
	public String getPrimaryKey() {
		String concatNaturalOrderedPrimaryKey = "";
		for (String pkValue : primaryKeys.values()) {
			concatNaturalOrderedPrimaryKey += pkValue;
		}
		return concatNaturalOrderedPrimaryKey;
	}



	public Map<String, String> getAllFields() {
		Map<String, String> map = new HashMap<>();
		map.putAll(fields);
		map.putAll(primaryKeys);
		return map;
	}



	public Map<String, String> getNonPKFields() {
		return fields;
	}



	public Map<String, String> getAllPrimaryKeys() {
		return primaryKeys;
	}





	public static class Builder {
		TableSchematic schematic;
		private Record record;



		private Builder(TableSchematic schematic) {
			this.schematic = schematic;
			record = new Record();
		}



		public Builder addColumnData(String columnName, String data) {
			Validate.notEmpty(columnName);
			if (data == null) {
				data = "null";
			}
			if (schematic.uniquePrimarykeyColumns().contains(columnName)) {
				record.primaryKeys.put(columnName, data);
			} else if (schematic.nonPrimaryKeyColumns().contains(columnName)) {
				record.fields.put(columnName, data);
			} else {
				throw new RecordColumnNameException("Could not find the column [" + columnName + "] in the schematic provided");
			}
			return this;
		}



		public Record build() {
			return record;
		}

	}



	public static Builder newBuilder(TableSchematic schematic) {
		return new Builder(schematic);
	}



	private Record() {}

}
