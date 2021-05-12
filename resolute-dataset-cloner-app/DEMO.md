OVERVIEW:

In this demo, I will show how to clone a distributor from a source database to a target database. In this case, we will use the Development database as the source database,
and a locally running database as the target.  

INSTRUCTIONS:

1. Create an SSH tunnel to the Development database (i.e. the source database) by executing the following command in a Terminal Window: 

   ssh -NL 0.0.0.0:5424:postgres1-db.dc.res0.local:5432 bigdata-master0.dc.res0.local

2. Launch the local database that the cloned dataset will be written to (i.e. the target database) by opening
   another tab in your Terminal Window, navigating to ~/GitHub/laser-ninja-dragon-service, and executing the following command:

   ./gradlew startDockerDependencies

3. Open your SQL client, and connect to both databases.  We are going to be copying the entire dataset rooted at the distributor with an id of 33 
   from the source database to the target database.  Confirm that this distributor currently exists in the source database but not in the target database
   by executing the following SQL statement against both databases:

   SELECT * FROM distributors WHERE id = 33;
   SELECT * FROM customers WHERE distributor_id = 33;
   SELECT * FROM nodes WHERE customer_id = 107 ORDER BY node_type_id;

4. Download the latest version of the resolute-datasetcloner-app JAR file from the following location:

   https://maddogtechnology.jfrog.io/artifactory/resolute-develop/com/resolute/resolute-dataset-cloner-app/[RELEASE]/resolute-dataset-cloner-app-[RELEASE]-all.jar

5. Create a new folder named resolute-dataset-cloner-demo in your Documents folder, and move the downloaded JAR file to this folder.

6. Within the resolute-dataset-cloner-demo folder, create a file named dataset-cloner-source.conf with the following contents (note that pureCopyMode must be set to true):

host=localhost
port=5424
database=resolute_cloud_dev
user=postgresadmin
password=<postgres-passoword>
logFile=dataset-cloner.log
numCopies=1
debug=false
pureCopyMode=true
outputFile=dataset-cloner.sql
entityType=DISTRIBUTOR
entityId=33           

6. Within the resolute-dataset-cloner-demo folder, create a second file named dataset-cloner-target.conf with the following contents 
(note that this file is an exact copy of the previous file, but with the database properties altered so that it points to your local database
instead of the Dev database):

host=localhost
port=5437
database=resolute_cloud_dev
user=postgres
password=
logFile=dataset-cloner.log
numCopies=1
debug=false
pureCopyMode=true
outputFile=dataset-cloner.sql
entityType=DISTRIBUTOR
entityId=33           


7. Within a Teminal Window, cd to the resolute-dataset-cloner-demo folder and execute the following command to generate a dataset-cloner.sql file containing
   the SQL script for copying the entire dataset into the target database (replace '<release-number>' with the actual release number of the JAR file you downloaded):

   java -jar resolute-dataset-cloner-app-<release-number>-all.jar dataset-cloner-source.conf

8. Within the same Teminal Window, run the following command to execute the generated a dataset-cloner.sql file against
   the target database (replace '<release-number>' with the actual release number of the JAR file you downloaded):

   java -jar resolute-dataset-cloner-app-<release-number>-all.jar dataset-cloner-target.conf --script

9. Within your SQL client, confirm that the distributor with an id of 33 now exists in the source database as well as the target database
   by executing the following SQL statement against both databases:

   SELECT * FROM distributors WHERE id = 33;
   SELECT * FROM customers WHERE distributor_id = 33;
   SELECT * FROM nodes WHERE customer_id = 107 ORDER BY node_type_id;
