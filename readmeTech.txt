Technical Notes: MJS 4.9.18

Basic Purpose: Create common BaseDAO that implements common methods such as 
update and delete.

First created a BaseDAO<T> inteface and BaseDAOImpl<T> class.

Then tried having coalCampDAOImpl extends BaseDAOImpl<CoalCamp> which 
would seem to mean that coalCampDAOImpl would have a copy the delete routine. 

First tried having deleteCoalCamp call delete(camp).  This failed because the 
SessionFactory inside BaseDAOImpl was null.

Tried adding a new bean to the spring4(appContext).xml file.  This did not help at first.

Tried 
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring4.xml");	
	BaseDAO<CoalCamp> baseDao        = (BaseDAO<CoalCamp>) context.getBean("baseDaoBean");
	baseDao.delete(camp);
This works but I dont like it stylistically.
Tried this.delete(camp) which fails since baseSessionFactory is null. 
Note that this.delete(camp) or just delete(camp) both allow access to 
campSessionFactory, as well as getSessionFactory.  Maybe using getSessionFactory 
is the best we can do for now. 

Removed sessionFactory from this class.  Declared getSessionFactory and this class abstract. 
If a BaseCoalCampObject is created this setup of having SessionFactories in each class 
might be changed.  

Moved applicationContext and getBean from mainTest to pojos.  This allows routines such as 
slickville.save (where slickville is a CoalCamp) to be used in main.  The CoalCamp pojo then 
fetches the campDao and calls campDao.save.  This worked without any problems.

Lots of problems calling campDao.save().  Could call campDao.saveCoalCamp(this) which then 
would call save, but could not directly call campDao.save().  After much trying tracked 
this down to CampDao interface not extending BaseDao interface.  Was able to get things working 
pretty smoothly after this.  coalCampObject.save calls save routine inside CoalCamp class, which 
picks up the coalCampDao bean and then calls the generic coalCampDao.save(this).  Also did 
update and delete.

----------------------
Next tried creating a baseCCObject class.  First created DateCreated and DateModifed members.  After
discovering @MappedSuperclass things went smoothly.  Was able to initialized both dates inside the 
no-argument BaseCCObject constructor.

Tried putting ID inside BaseCCObject and deleting from other pojos.  
Seemed to work for CoalCompany.  Then got working for CoalCamp, but 
if CoalLease has its ID removed there is a NoClassDefFoundError problem loading the CoalCamp (yes Camp) bean, 
but this is reported as an error in the ClassPathXmlApplicationContext() routine in CoalCompany (yes Company).
Triple arghhh... After much fooling around, changed BaseCCObject strategy = GenerationType.AUTO to remove 
the spaces around the equals.  This seemed to work!!  Then changed it back to have spaces and it worked 
anyways!!  Who knows???

Next error was just in campLease.  Discovered campLease had @Id but not @Column.  Changed this 
back then got complaint that @Column could not be used on a many-to-one field.  So moved 
StartYear and EndYear to after @Column.  This seemed to work. 

Next implemented getID in baseCCObject class, and then getById in BaseDAOImpl class.  This required 
that the class (such as CoalCamp.class) be passed in.  Since getById is needed in update (to get 
the original DateTime created), update also requires the class be passed in.  I could not get it 
from the parameterized object T, even though T is of the same class (ie. CoalCamp.class). 

Next tried to have only one application context and one sessionFactory.  Moved application context 
from each pojo to baseCCObject.  Seems to have worked fine.  Tried having a dependency injected 
SessionFactory in BaseDAOImpl (like in CoalCampDAOImpl, etc.), but it was always null. 




