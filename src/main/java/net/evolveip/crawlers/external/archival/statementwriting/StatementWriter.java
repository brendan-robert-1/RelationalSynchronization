/**
 *
 */
package net.evolveip.crawlers.external.archival.statementwriting;

import net.evolveip.crawlers.external.Record;
import net.evolveip.crawlers.external.TableSchematic;

/**
 * @author brobert
 *
 */
public interface StatementWriter {

	public abstract String writeSqlStatement(Record record, TableSchematic metaData);

}
