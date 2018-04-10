Relational Synchronization is useful for assisting in simple ETL (Extract Transform, and Load) operations on data sources.
Typically this is used when we have a data source that we have to "mirror" in some database. For example, if we need to create a condensed and local database
for reporting we can use some of the tools of Relational Synchornization to expedite and streamline this process. 

This tool can completly hide all decisions about updating, inserting, and deleting records in the target database. In addition it writes all of the sql
behind the scenes using vendor specific syntax, and performs the DML in efficient batches.

The project starts with a Crawler object which is obtainable with a
```java
Crawler.newBuilder() 
```
call where we initialize some essential information regarding
the target database + credentials. More interstingly we can call any number of 
```java
.bind(TableSchematic s, TableRetriever r)
```
 before build(). These bindings represent
entire tables in the database. We are declaring this is the table defined by the schematic, and here is the client provided retreiver that will retrieve the data and build a Table
at runtime. If we had 3 tables, a User, a Product, and a Subscription we would see a Crawler build like so, note the 3 bind calls 
```java	
Crawler crawler = new Crawler.newBuilder()
	.dbVendor(DBVendor.MySQL)
	.jdbcUrl(jdbcURL)
	.pw(pw)
	.user(user)
	.bind(userSchematic, userRetriever)
	.bind(productSchematic, new ProductTableRetriever())
	.bind(subscriptionSchematic, new SubscriptionTableRetriever())
	.build()
```
which tells the `Crawler` that when we run `crawl()` we want to retrieve and synchronize these tables in this order.

Using this tool is generally quite time efficient, using internal data structures that provide very fast search and comparison function on `Record `and `Table` objects. 
It is important to note that the space efficiency is O(n) where n is the number of records, techncially 2 * n because we load the existing table and the "new" table into memory concurrently.

`TableRetriever` is the second major interface that client will be using. As the name states, this is any object that "retrieves" a `Table` object. This means that when it comes time to process the next `TableSchematic` -> `TableRetriever` binding,
we first retrieve the `Table` and then archive it internally to the db provided. The interface is very generic, allowing clients to structure exactly how their Tables are retrieved, however we do provide a few utilities that assist
this process. Specifically with regards to REST API based data sources. At the end of the day a `Table` object must be created.

Table's in the context of this tool are a structure that store a list of Record's and a metadata object called `TableSchematic`. A simple example for a generic TableRetriever's `retrieveTable()` method might looks like so:
```java
@Override 
public Table retrieveTable(TableSchematic schematic){
	//We get some kind of data like this from "somewhere" REST API, external DB, file on remote server etc
	List<String> userNames = users;

	Table table = new Table(schematic);

	for(String userName : userNames){
		Record.Builder builder = Record.newBuilder(schematic);
		builder.addColumnData("USER_COLUMN", userName);
		table.addRecord(builder.build());
	}

	return table;
}
```
You can see that this example has a Table with only one column, we would usually have a `List<>` of some object where each field represents a column and a call to `builder.addColumnData()` for each field,
but this is up to the client to implement. As you can see it is quite simple to build a table, and most use cases follow this example pretty closely.  
