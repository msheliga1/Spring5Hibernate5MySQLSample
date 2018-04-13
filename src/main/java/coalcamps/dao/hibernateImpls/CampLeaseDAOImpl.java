package coalcamps.dao.hibernateImpls;
	
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import coalcamps.classes.CampLease;
import coalcamps.dao.CampLeaseDAO;
import java.util.*;
import javax.persistence.TypedQuery; 

/** 
 * Implements the {@link CampLease} database access interface using hibernate.
 * A SessionFactory is the only instance variable.  Routines are in CRUD order 
 * and include save, getById, get all, get count, update and delete.
 * 
 * @author Mike Sheliga 4.8.18
 */
public class CampLeaseDAOImpl extends BaseDAOImpl<CampLease> implements CampLeaseDAO {
			
	/**
	 *  This is the bean from the applicationContext xml file. Set using Dependency Injection.
	 */
	private SessionFactory leaseFactory; 	

	/**
	 * Set the leaseFactory.  Needed for bean class.
	 * 
	 * @param leaseFactory  A leaseFactory to be stored.
	 */
	public void setSessionFactory(SessionFactory leaseFactory) {
			this.leaseFactory = leaseFactory;
	} 
	
	/** 
	 * Return the leaseFactory instance variable. Needed for bean class.
	 * 
	 * @return the immutable leaseFactory instance variable.
	 */
	public SessionFactory getSessionFactory() {
		return leaseFactory;
	}
	
	// ------ NonStandard Methods ------
	/**
	 * Returns the actual class for this DAO.
	 * 
	 * @return a Class variable of the parameterized type CampLease 
	 */
	@Override
	public Class<CampLease> getParameterizedClass() {
		return CampLease.class;
	}

			
	// ------ Hibernate Database Methods ------
	/** 
	 * Save the CampLease to the database
	 * 
	 * @deprecated replaced by generic save method.
	 * @param lease  a camp lease that is saved to the database using session.save
	 */	
	public void saveCampLease(CampLease lease) {
		Session session = null;
		Transaction tx = null;
		try {
			session = leaseFactory.openSession();
			tx = session.beginTransaction();
			// save and persist are slightly different. Save works if primary key is already set.
			session.save(lease);  // save creates 3 of 3 records - MJS 4.1.18
			tx.commit();
		} catch (org.hibernate.PropertyValueException | 
				 org.springframework.dao.DataIntegrityViolationException ex) {
			System.out.println(lease.toString() + 
				" could not be saved. Maybe it is already in the database? " + 
				" ---- Exception Details ---- " + ex.toString() + ex.getMessage() );
			if (tx != null) tx.rollback();
		} catch (Exception ex) {
			// even though exception is caught, exception trace is printed to the console.
			System.out.println(lease.toString() + " could not be saved. Maybe it already exists? " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
			if (tx != null) tx.rollback();
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
	}  // end saveCampLease(CampLease lease)		
			
	/** 
	 * Returns a CampLease based upon primary key ID
	 * 
	 * @deprecated replaced by generic getById
	 * @param ID  an int representing the primary key of the camp lease
	 * @return a CampLease with the given ID or null if no such record exists
	 */
	public CampLease getCampLeaseById(int ID) {
		Session session = null;
		Transaction tx = null;
		CampLease lease = null;
		try {
			session = leaseFactory.openSession();
			tx = session.beginTransaction();
			lease = (CampLease) session.get(CampLease.class, ID);			
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println(lease.toString() + " could not be retrieved for id " + ID + ". Maybe it doesn't exist? " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		}
		return lease;
	} // end getCampLeaseById
			
	/**
	 * Returns a list of all CampLeases
	 * 
	 * @return a List of all camp leases using the default foreign key initialization scheme.
	 */
	public List<CampLease> getCampLeases() {  
		// loadAll only for HibernateTemplates (hibernate 3)
		Session session = null;
		Transaction tx = null;
	    List<CampLease> list = null;
	    try {
		    session = leaseFactory.openSession();
		    // must be beginTransaction, not getTransaction unless also tx.begin
		    tx = session.beginTransaction();  
		    TypedQuery<CampLease> query = session.createQuery("FROM CampLease", CampLease.class);
		    list = query.getResultList();  // replaces pre hibernate5 Query and query.list()
		    if (tx != null) tx.commit();
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			// even though exception is caught, exception info is printed to the console.
			System.out.println("Could not get list of CampLeases. " + 
				" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
	    } finally { 
		    if (session != null) session.close();
		} // end try-catch-finally
		return list;  
	} // end getCampLeases

	/**
	* Return all campLeases either with or without foreign keys company leasing
	* and camp leased already fetched.
	* 
	* @param eager  a boolean indicating if foreign key values campLeased and companyLeasing should be retrieved. 
	* @return       a list of all camp leases either with or without foreign key values.
	*/
	public List<CampLease> getCampLeases(boolean eager) { 
		List<CampLease> ccList = null;
		Session session = null;
		Transaction tx = null;
		String hql = null;
		try { 
			session = leaseFactory.openSession();  // not sure if to use classic subtype or not
			tx = session.beginTransaction();
		    if (eager) {
				// For hbm.xml files, We CAN force eager with either crit.createAlias(fkField, alias, LEFT_JOIN)
				// or with setFetchMode(fkField, FetchMode.EAGER). Works for crit or detachedCrit MJS 3.28.18
		    	// For Annotation Lazy Fetching (ie. @ManyToOne(fetch=FetchType.LAZY, optional=false), 
		    	// forced eager fetching after many, many tries.  MJS 4.1.18
			    hql = "SELECT lease FROM CampLease AS lease LEFT JOIN FETCH lease.campLeased camp " + 
		    		  " LEFT JOIN FETCH lease.companyLeasing co";
			    // hql = "FROM CampLease";
		    	TypedQuery<CampLease> query = session.createQuery(hql, CampLease.class);
		    	ccList = query.getResultList();  // old Way:query.list() - older: ccList = template.find(hql);
		    	// ccList.stream().filter(cc -> cc != null && cc.getCampLeased() != null)
		    	//	  .forEach(cc->System.out.println("Eager-loaded CampLeased " +cc.getCampLeased().getCampName()));
		    } else { // Want to enforce lazy retrieval for foreign keys here.
		    	// Could NOT do so MJS 3.28.18 ... just live with eager fetching if thats what xml file wants.		    	
		    	// Tried crit.createAlias with setFecthMode(lazy), and both alone.
		    	// Tried same 3 combos with DetachedCrit both with and without LEFT_JOIN.
		    	// Also tried setResultTransformer and .setFetchMode.list. 
		    	// 	.createAlias("companyBuilding", "companyBuilding", Criteria.LEFT_JOIN)
		    	ccList = session.createQuery("FROM CampLease", CampLease.class).getResultList();
		    	// old Way - Query.list() - older:template.find(hql);
		    } // end if eager ... else
		    tx.commit();
	    }  catch (Exception ex ) {
	    	if (tx != null) tx.rollback();
	    	System.out.println("Exception in getCampLeases(eager=" + eager + "). " + ex.getMessage());
	    	ex.printStackTrace();
	    } finally {
	    	if (session != null) session.close();	    	
	    }  // try-catch-finally block
	    return ccList; 
	} // end getCampLeases
	
	
	public List<CampLease> getCampLeasesWRONG() {			
	    // loadAll only for HibernatgeTemplates	
		Session session = null;
		Transaction tx = null;
		List<CampLease> list = null;
		try {  // session doesnt implement auto-closeable, so no try with resources.
			session = leaseFactory.openSession();
			tx = session.getTransaction();   // THIS IS WRONG, use beginTransaction!
		    TypedQuery<CampLease> query = session.createQuery("FROM CampLease", CampLease.class);
		    list = query.getResultList();  // replaces pre hibernate5 Query and query.list()
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not get list of CampLeases. " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
		return list;  
	}  // getCampLeasesWRONG
		
	/**
	 * Returns a count of the number of CampLeases
	 * 
	 * @return an int representing the total number of camp leases in the database.
	 */ 
	public int getCampLeaseCount( ) {
		int result = 0;
		Session session = null;
		Transaction tx = null;
		try {
			session = leaseFactory.openSession();
			tx = session.beginTransaction();
			TypedQuery<Long> query = session.createQuery("SELECT count(*) FROM CampLease", Long.class);
			result = ((Long) query.getSingleResult()).intValue();  // pre hibernate5 was Query and uinqueResult
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not get count of CampLeases. " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
		return result;
	} // end getCampLeaseCount

	/**
	 * Updates a CampLease database record.
	 * 
	 * @deprecated replaced by generic update method.
	 * @param lease  A CampLease that is to be updated in the hibernate database.
	 */	
	public void updateCampLease(CampLease lease) {
		Session session = null;
		Transaction tx = null;
		try {
			session = leaseFactory.openSession();
			tx = session.beginTransaction();
			session.update(lease);
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not get update the CampLease. " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
	} // end UpdateCampLease

	/**
	 * Deletes the CampLease from the database. The lease will not be deleted if it is 
	 * referenced from another table.  In this case an exception is thrown and handled.
	 * 
	 * @deprecated replaced by generic delete method.
	 * @param lease  A CampLease that is to be updated in the hibernate database.
	 */	
	public void deleteCampLease(CampLease lease) {
		Session session = null;
		Transaction tx = null;
		try {
			session = leaseFactory.openSession();
			tx = session.beginTransaction();
			session.delete(lease);
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not delete the camp lease. " + lease + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
	} // end DeleteCampLease		
		
} // end class CampLeaseDAOImpl
