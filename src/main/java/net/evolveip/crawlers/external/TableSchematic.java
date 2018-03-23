package net.evolveip.crawlers.external;

import java.util.Set;

/**
 * TableSchematic represents a blue print for how we plan on building a table.
 * We must specify the columns in the table, its name as well as an archive
 * schematic which is used to determine how this given table should be archived.
 *
 * @author brobert
 *
 */
public interface TableSchematic {

	/**
	 * The exact name of the table in the database.
	 */
	public String tableName();



	/**
	 * The ordered names of the columns exactly as in the database.
	 */
	public Set<String> nonPrimaryKeyColumns();



	public Set<String> uniquePrimarykeyColumns();

}
