package net.evolveip.crawlers.external.retrieval;

import net.evolveip.crawlers.external.Table;
import net.evolveip.crawlers.external.TableSchematic;

/**
 * Retrieves a Table object from an unknown data source.
 *
 * @author brobert
 *
 */
public interface TableRetriever {

	/**
	 * We need to get our respective {@link Table} from its source (API, DB etc)
	 * each retriever needs to know where to go and how to pack up the data into
	 * an archivable Table object. Our higher level model handles the data one
	 * SQL table at a time.
	 *
	 * @return
	 */
	public abstract Table retrieveTable(TableSchematic schematic);

}
