# database-crawler
**Owner(s):** Carlos Devoto

A Java library for introspecting the structure of a PostgreSQL database and generating an in-memory graph where each node represents a database table and each edge represents a foreign key association between two tables. This graph can be used, for instance, to dynamically generate a nuke function for a given entity based on the current data schema as opposed to hardcoding the nuke function and risking that it will become obsolete as new tables are added and removed from the schema.

## Usage
To use this library in a different Java project, add the following ``implementation`` directive to the ``dependencies`` section of your ``build.gradle`` file:
```groovy
implementation "com.resolute:database-crawler:${rbiDepVersion}"
```

### Sample Code

```java
    DatabaseCrawler dao = DatabaseCrawler.create(dataSource);
    Graph schemaGraph = dao.getSchemaGraph();
    Graph subgraph = schemaGraph.getSubgraphReachableFrom("customer_tbl");
    List<Node> sorted = subgraph.topologicalSort();
```
