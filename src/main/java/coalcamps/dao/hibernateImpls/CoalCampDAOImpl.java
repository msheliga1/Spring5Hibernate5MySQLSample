package coalcamps.dao.hibernateImpls;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.*;
import javax.persistence.TypedQuery;  // replace query in hibernate5

import coalcamps.classes.CoalCamp;
import coalcamps.dao.CoalCampDAO;

/** 
 * Implements database access routines using hibernate for the {@link CoalCamp} class.
 * Routines are transaction protected using begin, commit and rollback.  Exceptions in 
 * hibernate routines (such as save, getResultList and delete) are caught and handled. 
 * @author Mike Sheliga 4.9.18
 */
// Routines are in CRUD order and include save, getById, get all, getCount, update and delete.
// A session factory is the only instance variable and is used in all routines.
// Routines follow a general pattern of opening the session, beginning a transaction, 
// business logic (such as save or delete), committing the transaction and closing the session.
// Routines also normally use a try-catch-finally structure.  Note that try with resources is 
// NOT used since Session is not auto-closeable.
public class CoalCampDAOImpl extends BaseDAOImpl<CoalCamp> implements CoalCampDAO {

	/**
	 *  Bean from applicationContext.xml.  Set via dependency injection.
	 */
	private SessionFactory campFactory;

	/**
	 * Set the session factory.  Needed for implementing the SessionFactory bean.
	 * 
	 * @param campFactory  the SessionFactory to be stored
	 *
	 */
	public void setSessionFactory(SessionFactory campFactory) {
		this.campFactory = campFactory;
	}
    
	/**
	 * Return the session factory instance variable.
	 * 
	 * @return the campFactory instance variable.
	 */
	public SessionFactory getSessionFactory() {
		return campFactory;
	}
	
	// ------ NonStandard Methods ------
	/**
	 * Returns the actual class for this DAO.
	 * 
	 * @return a Class variable of the parameterized type CampLease 
	 */
	@Override
	public Class<CoalCamp> getParameterizedClass() {
		return CoalCamp.class;
	}
	
	// ------ Hibernate Database Methods ------	
	/*** 
	 * Save the CoalCamp to the database using session.save
	 * 
	 * @deprecated replaced by generic save routine
	 * @param camp   the CoalCamp to be saved to the database.
	 */
	public void saveCoalCamp(CoalCamp camp) {
		Session session = null;
		Transaction tx = null;
		try { 
			session = campFactory.openSession();
			tx = session.beginTransaction();
			// save and persist are slightly different - using GenerationType.IDENTITY or AUTO
			session.save(camp);  // save creates 3 of 3 records - MJS 4.1.18
			// persist creates 2 of 3 companies MJS 4.1.18 (randp id=8 rolled back).
			// session.persist(company);  // creates 2 of 3 companies 4.1.18 MJS
			tx.commit();
		} catch (org.hibernate.PropertyValueException | 
			 org.springframework.dao.DataIntegrityViolationException ex) {
			System.out.println(camp.toString() + 
				" could not be saved. It appears to already be in the database? " + 
				" ---- Exception ---- " + ex.toString() + ex.getMessage() );
				if (tx != null) tx.rollback();
		} catch (Exception ex) {
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println(camp.toString() + " could not be saved. Maybe it already exists? " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
			if (tx != null) tx.rollback();
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
	}  // end saveCoalCamp(CoalCamp camp)
	
	/**
	 * Returns a CoalCamp based upon primary key ID
	 * 
	 * @deprecated replaced by generic getById in BaseCCDAOImpl
	 * @param ID  an int representing the primary key.
	 * @return the CoalCamp with the given ID or null if no such record exists
	 */
	public CoalCamp getCoalCampById(int ID) {
		CoalCamp camp = null;
		Session session = null;
		Transaction tx = null;
		try { 
			session = campFactory.openSession();
			tx = session.beginTransaction();
			camp = (CoalCamp) session.get(CoalCamp.class, ID);
			tx.commit();
		} catch (org.hibernate.HibernateException ex) {
			System.out.println("Camp " + ID + " could not be retrieved.  " + 
				" ---- Hibernate Exception ---- " + ex.toString() + ex.getMessage() );
				if (tx != null) tx.rollback();
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Camp " + ID + " could not be retrieved. " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
		return camp;
	} // end getCoalCampById
				
	/**
	 * Return a list of all CoalCamps using the default initialization scheme. 
	 * If lazy initialization is the default, companyBuilding will not be initialized.
	 * @return  a List of all coal camps using default initialization for companyBuilding.
	 */
	public List<CoalCamp> getCoalCamps() {  
		Session session = null;
		Transaction tx = null;
	    List<CoalCamp> list = null;
		try {
		    session = campFactory.openSession();
		    // must be beginTransaction, not getTransaction unless also tx.begin
		    tx = session.beginTransaction(); 
		    // pre hibernate5 used Query, older still HibernateTemplate.loadAll
		    TypedQuery<CoalCamp> query = session.createQuery("FROM CoalCamp", CoalCamp.class);
		    list = query.getResultList(); // pre hibernate5 was query.list()
		    tx.commit();
		} catch (org.hibernate.HibernateException ex) {
			if (tx != null) tx.rollback();
			System.out.println(" List of all coalCamps could not be retrieved.  " + 
				" ---- Hibernate Exception ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) { 
			if (tx != null) tx.rollback();
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println("Could not get list of coalCamps. " + 
				" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
			ex.printStackTrace();
		} finally { 
		    if (session != null) session.close();
		} // end try-catch-finally
		return list;  
	} // end getCoalCamps
	
	/*** 
	* Return all coalCamps either with or without the companyBuilding already fetched.
	* CompanyBuilding is a foreign key that will be fetched based upon the eager parameter.
	* @param  eager  A boolean indicating if eager loading of the building CoalCompany should occur. 
	* @return A list of all coal camps.
	*/
	public List<CoalCamp> getCoalCamps(boolean eager) { 
		List<CoalCamp> ccList = null;
		String hql = null;
		Session session = null;
		Transaction tx = null;
	    // return template.loadAll(CoalCamp.class);  // can lead to lazy initialization error
		try { 
			session = campFactory.openSession();  // not sure if to use classic subtype or not
			tx = session.beginTransaction();
		    if (eager) {
				// For hbm.xml files, We CAN force eager with either crit.createAlias(fkField, alias, LEFT_JOIN)
				// or with setFetchMode(fkField, FetchMode.EAGER). Works for crit or detachedCrit MJS 3.28.18
		    	// For Annotation Lazy Fetching (ie. @ManyToOne(fetch=FetchType.LAZY, optional=false), 
		    	// forced eager fetching after many, many tries.  MJS 4.1.18
		    	hql = "SELECT camp FROM CoalCamp camp LEFT JOIN FETCH camp.companyBuilding co";
		    	// hql = "FROM CoalCamp AS camp LEFT JOIN FETCH camp.companyBuilding";  // also works
		    	TypedQuery<CoalCamp> query = session.createQuery(hql, CoalCamp.class);
		    	ccList = query.getResultList();   // old Way - list() - older:template.find(hql);
		    	ccList.stream().filter(cc -> cc != null && cc.getCompanyBuilding() != null).forEach(
		    		cc -> System.out.println("Eager-loaded CompanyBuilding " + cc.getCompanyBuilding().getCompanyName()));
		    	// Alternate methods of trying to enforce eager fetching. Criteria deprecated Hibernate5
		    	// ccList = session.createCriteria(CoalCamp.class).setFetchMode("companyBuilding", FetchMode.EAGER).list();
		    	// session.enableFetchProfile("camp_builder");
		    	// Criteria criteria = session.createCriteria(CoalCamp.class); 
		    	// criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY); 
		    	// ccList = criteria.list();		    	
		    } else {  // Want to enforce lazy retrieval for foreign keys here.
		    	// Could NOT do so MJS 3.28.18 ... just live with eager fetching if that is the default. 
		    	// Tried crit.createAlias with setFecthMode(lazy), and both alone.
		    	// Tried same 3 combos with DetachedCrit both with and without LEFT_JOIN.
		    	// Also tried setResultTransformer and .setFetchMode.list.createAlias
		    	// CriteriaBuilder/Query not recognized by Eclipse for old Hibernate3
		    	ccList = session.createQuery("FROM CoalCamp", CoalCamp.class).getResultList();
		    	// old Way - Query.list() - older:template.find(hql);
		    } // end if eager ... else
		    tx.commit();
	    }  catch (Exception ex ) {
	    	if (tx != null) tx.rollback();
	    	System.out.println("Exception in getCoalCamps(eager=" + eager + "). " + ex.getMessage());
	    	ex.printStackTrace();
	    } finally {
	    	if (session != null) session.close();	    	
	    }  // end try-catch-finally
	    return ccList; 
	} // end getCoalCamps

	/**
	 * Returns a count of the number of CoalCamps in the database.
	 * 
	 * @return  an int representing the number of database coal camps.
	 */ 
	public int getCoalCampCount( ) {
		Session session = null;
		Transaction tx = null;
		int result = 0;
		try {
		    session = campFactory.openSession();
		    // must be beginTransaction, not getTransaction unless also tx.begin
		    tx = session.beginTransaction(); 
		    // pre hibernate5 used Query, older still HibernateTemplate.loadAll
			TypedQuery<Long> query = session.createQuery("SELECT count(*) FROM CoalCamp", Long.class);	
			//  (Long) query.uniqueResult()).intValue(); In pre hibernate 5
			result = ((Number) query.getSingleResult()).intValue(); 
		    if (tx != null) tx.commit();
		} catch (org.hibernate.HibernateException ex) {
			System.out.println("Count of coalCamps could not be retrieved.  " + 
				" ---- Hibernate Exception ---- " + ex.toString() + ex.getMessage() );
				if (tx != null) tx.rollback();
		} catch (Exception ex) { 
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println("Count of coalCamps could not be retrieved. " + 
				" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
			ex.printStackTrace();
			if (tx != null) tx.rollback();
		} finally { 
		    if (session != null) session.close();
		} // end try-catch-finally
		return result;
	} // end getCoalCampCount

	/**
	 * Updates the data in a CoalCamp
	 * 
	 * @deprecated replaced by generic update routine
	 * @param camp  a CoalCamp that is to updated in the database.
	 */
	public void updateCoalCamp(CoalCamp camp) {
		Session session = null;
		Transaction tx = null;
		try {
		    session = campFactory.openSession();
		    tx = session.beginTransaction(); 
			session.update(camp);
		    if (tx != null) tx.commit();
		} catch (org.hibernate.HibernateException ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not update CoalCamp.  " + camp +  
				" ---- Hibernate Exception ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) { 
			if (tx != null) tx.rollback();
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println("Could not update CoalCamp.  " + camp +   
				" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally { 
		    if (session != null) session.close();
		} // end try-catch-finally
	}  // end updateCoalCamp
	
	/**
	 * Deletes the CoalCamp from the database. An exception will be thrown and handled 
	 * if this camp is used in other tables (such as the camp lease table).  In this case 
	 * the coal camp will not be deleted.
	 * 
	 * @deprecated replaced by the generic delete routine.
	 * @param camp  the CoalCamp that is to deleted from the hibernate database.
	 */
	public void deleteCoalCamp(CoalCamp camp) {
		Session session = null;
		Transaction tx = null;
		try {
		    session = campFactory.openSession();
		    tx = session.beginTransaction(); 
			session.delete(camp);
		    if (tx != null) tx.commit();
		} catch (org.hibernate.HibernateException ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not delete CoalCamp.  " + camp +  
				" ---- Hibernate Exception ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) { 
			if (tx != null) tx.rollback();
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println("Could not delete CoalCamp.  " + camp +   
				" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally { 
		    if (session != null) session.close();
		} // end try-catch-finally
	}  // end deleteCoalCamp
			
} // end class CoalCampDAOImpl
