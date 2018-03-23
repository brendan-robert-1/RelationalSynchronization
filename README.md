Relational Synchronization is useful for assisting in simple ETL (Exchange Transform, and Load) operations on data sources.
Typically this is used when we have a data source that we have to "mirror" in some database. For example, if we need to create a condensed and local database
for reporting we can use some of the tools of Relational Synchornization to expedite and streamline this process. 

This tool can completly hide all decisions about updating, inserting, and deleting records in the target database. In addition it writes all of the sql
behind the scenes using vendor specific syntax, and performs the DML in efficient batches.

The project starts with a Crawler object which is obtainable with a Crawler.newBuilder() call where we initialize some essential information regarding
the target database + credentials. More interstingly we can call any number of .bind(TableSchematic s, TableRetriever r) before build(). These bindings represent
entire tables in the database. We are declaring this is the table defined by the schematic, and here is the client provided retreiver that will retrieve the data and build a Table
at runtime. If we had 3 tables, a User, a Product, and a Subscription we would see 3 bind calls that may look like so

	.bind(userSchematic, userRetriever)
	.bind(productSchematic, new ProductTableRetriever())
	.bind(subscriptionSchematic, new SubscriptionTableRetriever())

which tells the Crawler that when we run crawl() we want to retrieve and synchronize these tables in this order.

Using this tool is generally quite time efficient, using internal data structures that provide very fast search and comparison function on Record and Table objects. 
It is important to note that the space efficiency is O(n) where n is the number of records, techncially 2 * n because we load the existing table and the "new" table into memory concurrently.
