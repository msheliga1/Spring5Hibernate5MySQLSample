Project to demonstrate java spring5 hibernate5 example with annotated classes. 
Author: MJS 4.8.18.

This project demonstrates spring 5.0.2 and hibernate 5.2.12 using annotated 
classes to manipulate a 3 table database. This technology is fairly current as of today (Spring 2018).
The project was modified to allow the use of JavaSE1.8 features and a several 
lambdas and streams were implemented to verify this version works.

The database tables are automatically created when running the application.  In 
production mode this feature would be changed.

A common base class is extended by all POJOs and includes a primary key id, created and last 
modified dates.  These values are thereby automatically included in the database tables for 
the POJOs that extend this class, through the use of the @MappedSuperclass annotation. 

Common database routines are demonstrated including save, getById, getAll, getCountOfRecords, 
update and delete.  A getAll "eager" method which includes foreign key values is also used to overcome 
lazy initialization issues printing out foreign key data. For example, when displaying 
a town (ie. a coal camp), it is nice to display the company that built this town.  In order 
to do this an overloaded getAll method is supplied that can retrieve both the town and compnay 
data.  The original getAll method is more efficient but does not display as much data when, for 
example, a list of all towns are printed out.

Several of the database routines (save, getById, update, etc.) have been implemented using generics. 
These routines replace POJO specific routines such as getCoalCampById and updateCoalCompany. 

All database routines include exception handling and transaction management.  TypedQuery's and getResultList
are used as required in this version instead of the Query and Query.list() found in hibernate 4.

All classes are fully commented with JavaDoc comments and these html documentation files 
can be found in the /doc folder.
