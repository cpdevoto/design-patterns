# resolute-dataset-cloner-app
**Owner(s):** Carlos Devoto

This application can be used to make one or more deep copies of a specific entity within the Resolute database, primarily for testing purposes.  Using this application, for instance, you can make 10 deep copies of the 'Macomb' building within the 'McLaren' customer.  Currently, the following types of entities can be cloned:

  * Building (Fully Supported)
  * Customer (Fully Supported)
  * Distributor (Fully Supported)
  
This application uses the ``dataset-cloner`` framework to dynamically discover the current structure of the Resolute database at runtime. It will make a deep copy of records in all of the tables that are reachable from the entity to be cloned based on foreign key associations. 

## Usage

In order to use this application, the first thing you should do is establish a connection to the database containing the data to be cloned.  One way to do this is by establishing an SSH tunnel using a command similar to that shown below:

```
ssh -NL 0.0.0.0:5424:postgres1-db.dc.res0.local:5432 bigdata-master0.dc.res0.local
```

Next, you should make a copy of the ``dataset-cloner.conf.example`` file, naming it ``dataset-cloner.conf`` and editing it as shown below:

```
host=localhost
port=5424
database=resolute_cloud_dev
user=postgresadmin
password=databasepassword
logFile=dataset-cloner.log
numCopies=1
debug=false
entityType=BUILDING
entityId=42
```

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
  * **entityType (required)**:  The type of the entity to be cloned.  Currently, this must be set to 'BUILDING', 'CUSTOMER', or 'DISTRIBUTOR'.  Other entities may be supported in the future as the need arises.
  * **entityId (required)**:  The id of the entity to be cloned.
  
Once you have edited the configuration file to reflect the clone operation that you wish to perform, you can run the application. When the application is built a shadow JAR is published to Artifactory which can be retrieved from the following URL, which you should copy and paste into the address bar of your browser:

**ht<span></span>tps://maddogtechnology.jfrog.io/artifactory/resolute-develop/com/resolute/resolute-dataset-cloner-app/[RELEASE]/resolute-dataset-cloner-app-[RELEASE]-all.jar**

Note that, when you copy this link into your browser, you will be prompted to provide your Artifactory username and password.  Once you have downloaded this file and placed it in the same directory where your configuration file is located, you can run the application by executing the following command in a Terminal Window (replace '3.1.3' with the actual release number of the JAR file you downloaded):

```
java -jar resolute-dataset-cloner-app-3.1.3-all.jar
```

If the application crashes, hangs, or throws an exception, you should run the following command in order to do a full rollback:

```
java -jar resolute-dataset-cloner-app-3.1.3-all.jar --rollback
```
The resulting rollback operation will use whatever log file is specified in the configuration file in order to determine which records to delete.

If you want to execute the SQL script that was generated when you ran ``java -jar resolute-dataset-cloner-app-3.1.3-all.jar`` with ``pureCopyMode`` set to ``true``, you should edit your ``dataset-cloner.conf`` file so that it points to whatever database you want the script executed against, and then run the following command:

```
java -jar resolute-dataset-cloner-app-3.1.3-all.jar --script
```
    
## Known Limitations

  * Currently, the rollback operation that gets executed whenever an exception is thrown by the clone operation only rolls back data associated with the last copy that was created.  To do a full rollback of all copies you will need to run ``java -jar resolute-dataset-cloner-app-3.1.3-all.jar --rollback``. 

  
  
  
