# dataset-cloner
**Owner(s):** Carlos Devoto

A lightweight library that can be used to create one or more copies of a given entity within a PostgreSQL database.  This library leverages that ``database-crawler`` library in order to dynamically discover the structure of the database at runtime.  This structure is represented as a schema graph in which the vertices represent tables, and the edges represent foreign key associations.  A subgraph of this schema is computed that includes only the vertices and edges reachable from the vertex representing the entity to be cloned. The clone operation can then proceed generically based on the metadata represented within this subgraph. 

## Problem Statement

The purpose of this project is to create a tool that will allow us to clone a subset of a database in order to create one or more copies of a given entity.  For instance, we currently have a requirement to test how every aspect of the Resolute application would behave if we had a customer with 120 buildings.  In order to achieve this goal, the simplest thing to do would be to pick a customer in our Staging environment and clone a particular building within that customer until the desired total building count of 120 is reached.  This sounds simple enough until you consider that the definition of a particular building within our database spans thousands of records stored across upwards of one hundred tables.  This problem is further exacerbated by the fact that, even if you could write a program to identify which records within these one hundred tables are associated with a given building and then create clones of these records, the code you would write to do this would become obsolete in as little as one sprint.  This is because our database schema is constantly evolving; new tables are added, old tables are deleted, and new foreign key associations are formed.  In order to deal with these changes, you would have to make continuous updates to your building cloner application which is really impractical.  

To eliminate this need for continuous maintenance, we must create a tool that discovers the current database schema at runtime, uses this information to figure out what records within what tables must be cloned, and performs the appropriate cloning operations using generic algorithms.  The node-nuke-generator already does something very similar in order to regenerate all of our enitity nuke functions every time there is a schema change. Copying all records in a subgraph via generic algorithms is much more complicated than deleting them, however, so this should prove to be a very challenging project!

## Quickstart Guide

Suppose we want to create an application that will allow us to create multiple clones of all data associated with a record in the ``test1_tbl`` table, assuming a PostgreSQL database schema represented by the follow DDL script:

```sql
CREATE TABLE test1_tbl (
	id SERIAL PRIMARY KEY,
	name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> '')
);

CREATE TABLE test2_tbl (
	id SERIAL PRIMARY KEY,
	name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> '')
);

CREATE TABLE test3_tbl (
	test1_id INTEGER REFERENCES test1_tbl ON DELETE RESTRICT,
	test2_id INTEGER REFERENCES test2_tbl ON DELETE RESTRICT,
	name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> ''),
	PRIMARY KEY (test1_id, test2_id)
);

-- This table is used to test whether unary associations are properly handled!
CREATE TABLE test4_tbl (
	id SERIAL PRIMARY KEY,
	test1_id INTEGER REFERENCES test1_tbl ON DELETE RESTRICT,
	parent_id INTEGER REFERENCES test4_tbl ON DELETE RESTRICT,
	name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> '')
);

-- This table is used to test whether closure tables are properly handled!
CREATE TABLE test4_closure_tbl (
  parent_id INTEGER REFERENCES test4_tbl ON DELETE RESTRICT,
  child_id INTEGER REFERENCES test4_tbl ON DELETE RESTRICT,
  depth INTEGER,
  PRIMARY KEY (parent_id, child_id)
);
```
 

The first thing we need to do is create a class that extends the ``DatasetCloner`` superclass as shown below:

```java
package com.resolute.dataset.mycloner;

import static java.util.Objects.requireNonNull;

import com.resolute.database.crawler.model.Graph;
import com.resolute.dataset.cloner.engine.DatasetCloner;
import com.resolute.dataset.cloner.utils.Key;

public class Test1Cloner extends DatasetCloner {

  public static Builder builder(Environment env) {
    return new Builder(env);
  }

  private Test1Cloner(Builder builder) {
    super(builder, (schemaGraph, sourceSetsBuilder) -> {
      sourceSetsBuilder
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withRootSelectStatement("test1_tbl",
                  Key.of("id", builder.recordId)));

    });
  }

  public static class Builder extends DatasetCloner.Builder<Test1Cloner, Builder> {

    private Integer recordId;

    private Builder(Environment env) {
      super(env);
    }

    public Builder withRecordId(int recordId) {
      this.recordId = recordId;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected Test1Cloner newInstance() {
      requireNonNull(recordId, "recordId cannot be null");
      return new Test5Cloner(this);
    }
  }

}
```
Some important things to note about the preceding code block:

  * The ``Test1Cloner`` class must extend the ``DatasetCloner`` class.
  * The ``Test1Cloner`` class should implement the typical Builder pattern. To construct an instance of ``Test1Cloner``, the client code invokes a static ``builder(SchemaGraph)`` method that returns a new instance of the static ``Builder`` inner class.  Using method call chaining, the client then invokes a series of setter methods of the ``Builder`` instance in order to configure the cloner, and then invokes the ``build()`` method to create the actual instance of ``Test1Cloner``.
  * The static inner ``Builder`` class must extend ``DatasetCloner.Builder<Test1Cloner, Builder>``. Note the two generic arguments included when referencing the superclass.  The first argument is the name of the cloner class you are creating (i.e. ``Test1Cloner``). The second argument is the name of the builder class you are creating (i.e. ``Builder``).  The ``DatasetCloner.Builder`` encapsulates the configuration of attributes that are common to all cloners. When we extend ``DatasetCloner.Builder``, we inherit all of that configuration logic so that it doesn't have to be duplicated.
  * The ``Builder`` subclass we create can include its own attributes that are not shared with other cloner objects, together with their associated setter methods.  In this case, our Builder includes a ``recordId`` attribute and a ``withRecordId(int)`` method.
  * All ``Builder`` subclasses must override the ``getThis()`` method and the ``newInstance()`` method.  The ``getThis()`` should be implemented exactly as shown.  The ``newInstance()`` should ensure that any required attributes particular to your own class have been set (e.g. ``requireNonNull(recordId, "recordId cannot be null");``), and should return a new instance of your cloner class by invoking a private constructor that accepts the ``Builder`` instance as an argument.
  * The ``Test1Cloner(Builder)`` constructor should be private and must invoke the ``super(Builder, Initializer)`` superclass constructor. The first argument to the superclass constructor is the Builder object.  The second argument should be a lambda that accepts an instance of the ``Graph`` class representing the PostgreSQL database schema as its first argument, and an instance of the ``SourceSetsBuilder`` class as its second argument. 
  * The ``Initializer`` lambda contains the following two lines:
  
```java
      Graph test1Graph =
          schemaGraph.getSubgraphReachableFrom("test1_tbl");
          
      sourceSetsBuilder
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withRootSelectStatement("test1_tbl",
                  Key.of("id", builder.recordId)));
```

  * The first line extracts a subgraph rooted at the ``test1_tbl`` node from the schema graph.
  * The second line configures the source set for the clone operation, passing in the subgraph from the first line, and providing a root select statement to determine which record within the root ``test1_tbl`` table should be cloned. In this case we want to clone the record with an ``id`` value corresponding to ``builder.recordId``.
  
After we have created the cloner class, we need to create a class that extends the ``Application`` superclass as shown below:

```java
package com.resolute.dataset.cloner.mycloner;

import org.testcontainers.shaded.com.google.common.primitives.Ints;

import com.resolute.dataset.cloner.Application;
import com.resolute.dataset.cloner.Environment;


public class Test1CloneApplication extends Application {

  public static void main(String[] args) throws Exception {
    new Test1CloneApplication().run(args);
  }

  @Override
  public void run(Environment env) {

    // The following two lines show how to read a custom property,
    // try converting it into an int, and throw an exception if
    // the conversion fails.
    Integer recordId = Ints.tryParse(env.getProperties().getProperty("recordId"));
    checkArgument(recordId != null, "expected a positive integer value for recordId");

    Test1Cloner cloner = Test1Cloner.builder(env)
        .withRecordId(recordId)
        .build();
    try {
      cloner.execute();
    } catch (Throwable t) {
      t.printStackTrace();
      cloner.rollback();
    }

  }

}
```
 
Some code about the preceding code block:

  * The ``Test1CloneApplication`` class must extend the ``Application`` class.
  * The ``Test1CloneApplication`` class must include a ``main(String[])`` method that creates a new instance of ``Test1Application`` and invokes its ``run(String[])`` method.
  * The ``Test1CloneApplication`` class must override the ``run(Environment)`` method of its superclass with an implementation that creates and executes an instance of the ``Test1Cloner`` class.
  * The ``Test1Application`` class will look for a configuration file on the file system with an absolute path matching the string supplied as the first command-line argument when the class is executed as a Java application.  If no command-line arguments are specified, the class will look for a configuration file named ``dataset-cloner.conf`` in the current working directory.
  * The configuration file should include the following properties. All of the listed properties are mandatory except the ``logFile`` property, which defaults to ``dataset-cloner.log`` if omitted, the ``numCopies`` property which defaults to ``1`` if omitted, and the ``debug`` property, which defaults to ``false`` if omitted.
  * If the ``debug`` property is set to ``true``, all of the generated SQL code will be printed to the standard output stream. 
  
```
host=localhost
port=5432
database=resolute_cloud_dev
user=postgresadmin
password=xyzpdq
logFile=test1-cloner.log
numCopies=1
debug=false
pureCopyMode=false
outputFile=test1-cloner.sql
```
  * The configuration file may optionally also include some custom properties that are particular to your application, such as the ``recordId`` property shown below. These custom properties are made available to your ``run(Environment env)`` method by invoking  ``env.getProperties()``.   
   
```
host=localhost
port=5432
database=resolute_cloud_dev
user=postgresadmin
password=xyzpdq
logFile=test1-cloner.log
numCopies=1
debug=false
pureCopyMode=false
outputFile=test1-cloner.sql
recordId=36
```
Once you have created a ``DatasetCloner`` subclass, an ``Application`` subclass, and a configuration file, you can trigger the clone operation by invoking the ``main`` method of the ``Application`` subclass.

What follows is a brief explanation of each property in the configuration file:

  * **host (required)**:  The DNS name or IP address of the database server.  If you are running the database server locally, or if you have established an SSH tunnel to it, the value of this attribute should be set to 'localhost'.
  * **port (required)**:  The port that the database server is listening on.  If you have established an SSH tunnel to it, the value of this attribute should be set to whatever local port you have bound the remote port to.
  * **database (required)**:  The name of the database that we want to connect to.
  * **user (required)**:  The name of the database user that we will connect as.  Note that this should be a user with full read/write accessto the database in question.
  * **password (required)**:  The password of the database user that we will connect as.
  * **logFile (optional; defaults to 'dataset-cloner.log')**:  The absolute or relative path to the log file which will be generated when the application runs.  This log file will contain the primary keys for all inserted records, and can be used to perform a full rollback in the event that the application crashes or hangs.
  * **numCopies (optional; defaults to '1')**:  The number of copies to be made of the entity specified by the 'entityType' and 'entityId' attributes.
  * **debug (optional; defaults to 'false')**:  If set to 'true', all of the SQL that gets executed as the application is run will be written to the system output stream.  Since there is a lot of SQL code generated, you should only change this setting if you are attempting to troubleshoot a problem.
  * **pureCopyMode (optional; defaults to 'false')**: This property caters to the use case where we want to copy data from one database to another.  In this scenario, we do not want to mutate the data in the source set at all.  Instead, we want to make a pure copy. If this property is set to 'true', the clone application will not insert any new data into the source database, but will instead generate a SQL containing all of the insert statements needed to make a pure copy of the source set. You can open up a PostgreSQL client application like DBeaver and use it to execute the generated SQL script.  The location of the generated SQL script is determined by the value of the 'outputFile' property.
  * **outputFile (optional; defaults to 'dataset-cloner.sql')**: The absolute or relative path to the SQL script file which will be generated is the 'pureCopyMode' property is set to true. 

## Rollbacks

All subclasses of ``DatasetCloner`` expose a ``rollback()`` method that can be used to rollback all database inserts made during the current run of the ``Application`` subclass if an exception is thrown, but only for the recent copy that was made.  If the application crashes or gets hung up for some reason, or if you want to rollback the changes for all copies, you will not be able to use the ``rollback()`` method to undo your changes.  To properly deal with this scenario, the ``DatasetCloner`` generates a log file which records the temp table prefix and the primary keys of all inserted records for all copies.  The location of this log file can be configured by setting the value of the ``logFile`` property within your configuration file. By default, the log file will be placed in the current working directory, and will be named ``dataset-cloner.log``. This generated log file can be used to rollback all changes, by creating a second subclass of the ``Application`` class as shown below:

```java
package com.resolute.dataset.cloner.mycloner;

import com.resolute.database.crawler.model.Graph;
import com.resolute.dataset.cloner.Application;
import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.engine.RollbackOperation;

public class Test1RollbackApplication extends Application {

  public static void main(String[] args) throws Exception {
    new Test1RollbackApplication().run(args);
  }

  @Override
  public void run(Environment env) {

    RollbackOperation.forGraph(env.getSchemaGraph())
        .withDataSource(env.getDataSource())
        .withDebug(env.getDebug())
        .executeFromLogFile(env.getLogger().getFile());

  }
}
```
## SQL Script Execution
When you run a clone application with the ``pureCopyMode`` property set to ``true``, the application will generate a SQL script to whatever destination is specified by the value of the ``outputFile`` property (recall that this value defaults to ``dataset-cloner.sql`` if it is not specified).  The SQL script generated in this manner may be quite large. As such, if you attempt to load the entire script into memory before executing it, you are likely to get an out-of-memory if there is not enough memory assigned to your SQL client application, or a max-stack-depth error from PostgreSQL if you do manage to execute the entire script as a single transaction.  In order to avoid these errors, you need a streaming script parser that buffers SQL statements until a certain size threshold is reached, at which point all buffered statements will be executed, and the buffer will be reset so it is ready to accumulate the next batch of SQL statements.  This approach ensures that the script will be executed properly no matter how large it is.  

The ``dataset-cloner`` library provides a class named ``ScriptOperation`` that allows for this sort of buffered script execution.  The easiest way to use this class is to create an application class similar to the one shown below.

```
package com.resolute.dataset.cloner.testutils;

import com.resolute.dataset.cloner.Application;
import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.engine.script.ScriptOperation;

public class Test5ScriptApplication extends Application {

  public static void main(String[] args) throws Exception {
    new Test5ScriptApplication().run(args);
  }

  @Override
  public void run(Environment env) {

    ScriptOperation.execute(env);

  }
}
```
Before you run this class as a Java application, you should make sure you edit the ``dataset-cloner.conf`` file to reflect the database you want the script to be executed against. Whatever script is referenced by the ``outputFile`` property will be the one executed.  If the ``debug`` property is set to ``true`` all of the SQL code will be mirrored to the system output stream as it is executed.  

The ``ScriptOperation`` class exists to support the use case where you want to copy a dataset from one database to another. The following steps illustrate how you might do this:

  * Execute your clone application (e.g. ``Test1CloneApplication``) with a ``dataset-cloner.conf`` configured to point to the source database, and with the ``pureCopyMode`` property set to true.
  * Edit the ``dataset-cloner.conf`` to point to the target database, and then execute your script application (e.g. ``Test1ScriptApplication``).  Note that, if you attempt to execute your script application while you are still pointing to the source database, the script will fail entirely due to duplicate-key errors.



## Advanced DatasetCloner Configuration
### Problem 1: You don't want to clone the data in certain tables that are reachable from the root table

The ``dataset-cloner`` framework provides two basic ways to deal with this situation, and they can both be combined together to achieve precisely the results you want.

The first approach is to pass in a set of edges that should be ignored when you invoke the ``schemaGraph.getSubgraphReachableFrom`` method as shown below:

```
      Set<IgnoredEdge> ignored = ImmutableSet.of(
          new IgnoredEdge("node_tbl", "portfolio_tbl",
              new ForeignKey(ImmutableList.of(new ForeignKeyField("id", "id")))),
          new IgnoredEdge("node_tbl", "site_tbl",
              new ForeignKey(ImmutableList.of(new ForeignKeyField("id", "id")))),
          new IgnoredEdge("node_tbl", "standard_perspective_customer_node_tbl",
              new ForeignKey(ImmutableList.of(new ForeignKeyField("id", "node_id")))));

      Graph subGraph =
          schemaGraph.getSubgraphReachableFrom("node_tbl", ignored);

```
The second approach is to compute a second subgraph rooted at a different table and then subtract it from the first subgraph using the ``graph.difference(Graph)`` method.  In the example shown below, we decide that when we are cloning a record in the ``node_tbl``, we do not want to clone the data in any of the tables that are reachable from the ``widget_tbl`` table.

```java
      Graph nodeGraph =
          schemaGraph.getSubgraphReachableFrom("node_tbl");
          
      Graph widgetGraph =
          schemaGraph.getSubgraphReachableFrom("widget_tbl");
          
      Graph nodeGraphMinusWidgetGraph =
          nodeGraph.difference(widgetGraph, false); // Passing in false for the second arg prevents some verbose output from being generated! 
```
### Problem 2: You want to clone the data in certain tables that are not reachable from the root table

The ``dataset-cloner`` framework supports this by allowing you to configure multiple source sets using the ``SourceSetsBuilder`` object in your initializer.  For instance, suppose we want to clone the data in the ``raw_point_tbl`` whenever we clone a building. Unfortunately, the ``raw_point_tbl`` is not reachable from the ``node_tbl`` by following the set of directed foreign key associations leading from referenced tables to the tables containing reference fields. The set of ``raw_point_tbl`` records that you want to clone can be computed as the set of all ``raw_point_tbl`` referenced by any of ``mappable_point_tbl`` records that are being cloned.  This can be accomplished by configuring to source sets in your initializer; one for the subgraph rooted at the ``node_tbl``, and one for the subgraph rooted at the ``raw_point_tbl``. The set of all records to be cloned for a given table such as the ``mappable_point_tbl`` table can be determined by retrieving the list of all records stored in a special temp table that gets created for each table in the clone set.  This temp table contains the primary keys of all records in the main table that need to be cloned  Thus, the root select statement for the source set rooted at the ``raw_point_tbl`` table can be computed as follows:

```sql
SELECT t2.raw_point_id AS id 
  FROM <temp_mappable_point_tbl> t1
  JOIN mappable_point_tbl t2 ON t1.id = t2.id
  WHERE t2.raw_point_id IS NOT NULL
```
You can therefore configuring these two source sets within your DatasetCloner initializer as follows:

```
      Graph nodeGraph =
          schemaGraph.getSubgraphReachableFrom("node_tbl");

      Graph rawPointGraph =
          schemaGraph.getSubgraphReachableFrom("raw_point_tbl");
          
      sourceSetsBuilder
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withGraph(nodeGraph)
              .withRootSelectStatement("node_tbl",
                  Key.of("id", builder.buildingId))
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withGraph(rawPointGraph)
              .withRootSelectStatement("raw_point_tbl",
                  tableNamePrefix -> "SELECT t2.raw_point_id AS id "
                      + "FROM "
                      + TempTables.getTempTableName(tableNamePrefix,
                          "mappable_point_tbl")
                      + " t1 "
                      + "JOIN mappable_point_tbl t2 ON t1.id = t2.id "
                      + "WHERE t2.raw_point_id IS NOT NULL");

```
### Problem 3: Your subgraph may include subclass table records without the corresponding superclass table records

A commonly used pattern to prevent data duplication in relational databases is to design table structures that simulate inheritance hierarchies.  Suppose, for instance, that we want to model two categories of employees: hourly workers and salaried workers. Both categories of employees will share certain attributes and associations in common, such as the ``first_name`` and ``last_name`` attributes, or the ``manager`` association.  Each category of employee is likely to have some attributes and associations that the other category lacks however.  One way to model this in a relational database is as follows:
  * Create an ``employee`` table to encapsulate all of the attributes and associations shared in common by both categories of employees.  This table should include a discriminator column that stored the category of the employee in question (HOURLY or SALARIED)
  * Create an ``hourly_worker`` table to encapsulate the attributes and associations particular to hourly workers.  This table should have a primary key that is also a foreign key reference to a single record in the ``employee`` table.  An hourly worker can then be represented within the database through a single record in each of these two tables. 
  * Create a ``salaried_worker`` table to encapsulate the attributes and associations particular to salaried workers.  This table should have a primary key that is also a foreign key reference to a single record in the ``employee`` table. A salaried worker can then be represented within the database through a single record in each of these two tables.
  
When you use the ``dataset-cloner`` framework, you may encounter cases in which the subgraph you are attempting to clone will include a subclass table such as ``hourly_worker`` or ``salaried_worker`` without including the corresponding superclass table (i.e. ``employee``).  Failure to address this problem will usually cause the clone operation to fail, or worse, the clone operation will succeed, but you end up with a bunch of partial entities.

The ``DatasetCloner`` class exposes a ``getOrphanedSuperclassNodes()`` method that can be used to detect this problem. The method returns a ``Map`` where the key for each entry is a ``Node`` object representing the superclass table, and the value is a list of ``Node`` objects representing all of the subclass tables associated with the superclass table.  Once you know which superclass tables are causing the problem, you can solve this problem using the strategy described for solving Problem #2 above. 
 
### Problem 4: You want to control the mutation of fields that are part of a unique index

In order to prevent the violation of uniqueness constraints when cloning records for a given table, the ``dataset-cloner`` framework follows a standard algorithm:
  * If the unique index includes a foreign key reference to another cloned record, then we don't need to do anything special since the value of the foreign key field or fields will guarantee uniqueness.  If the field has a default value, we use it; otherwise, we just copy the value as is.
  * Otherwise, if the unique index field has a default value, we use it.
  * Otherwise, if the unique index field is a text field, we append a standard suffix to it as follows:
  
```
value --> value_<temp_table_prefix>_<copy_number>
```
Thus, if the original value is "Building 1", it will be mutated to something like "Building 1_34556845_12" before insertion in order to prevent the violation of uniqueness constraints.  If you are not happy with the standard mutation algorithm for a given field within a given table, you can supply your own algorithm when you configure the source sets in your initializer by invoking the ``withFieldLevelMutator`` method, as shown in the following example:

```java
      sourceSetsBuilder
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withGraph(rawPointGraph)
              .withRootSelectStatement("raw_point_tbl",
                  Key.of("id", builder.id))
               .withFieldLevelMutator("raw_point_tbl", "metric_id", (tableNamePrefix, copyNumber, value) -> {
                  return "__.Bldg" + tableNamePrefix + copyNumber + ".__" + value;
                  }));
```
     
### Problem 5: You want to force the mutation of certain fields that depend on the values of other fields

Suppose you have a table like ``node_tbl`` that has a column like ``display_name`` whose value is derived from the value of another column by default, like the ``name`` column. Since the ``name`` field is part of a unique index, the ``dataset-cloner`` cloner will automatically ensure that it's assigned a unique value using the strategy described in Problem #4 above. Unfortunately, the same algorithm will not be applied to the ``display_name`` column, since it is not part of a unique index.  Unless we do something this will probably result in multiple cloned node records with the exact same display name as the original node, which will be confusing to users. In order to prevent this, we can supply a custom mutator that will allow us to set the value of the ``display_name`` attribute based on the mutated value of the ``name`` attribute, by invoking ``withTupleLevelMutator`` as shown below:

```java
      sourceSetsBuilder
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withGraph(nodeGraph)
              .withRootSelectStatement("node_tbl",
                  Key.of("id", builder.buildingId))
              .withTupleLevelMutator("node_tbl", c -> c.setValue("display_name", c.getValue("name"))));

```

## Known Limitations
  * The algorithms used by the ``dataset-cloner`` framework rely on the discovery of the database schema based on foreign key associations.  If the schema is missing foreign key associations to certain tables, the data in those tables is unlikely to get cloned.  You can mitigate this by adding the appropriate foreign keys to the database schema.
  * If a table definition includes a unique index that (a) does not contain a foreign key reference to another cloned record, and (b) does not provide a generated default value for at least one of it fields, and (c) does not include at least one text field, it is very likely that the clone operation for that table will fail due to a uniqueness constraint violation.  You can mitigate this by either changing the table structure or providing custom mutators at the field- or tuple-level.
  * The ``ScriptOperation`` class uses a very simple algorithm to determine when the end of a SQL statement has been reached. Namely, it looks for a line ending with a semi-colon.  If the script includes the declaration of any PostgreSQL functions, this simple parser is likely to fail, since such function declarations almost always include semi-colons within the function body.  The ``ScriptOperation`` is ideally suited for execution of the SQL scripts generated by running the clone application with ``pureCopyMode`` set to ``true``. It should not be used as a general purpose client to execute more complicated scripts.
