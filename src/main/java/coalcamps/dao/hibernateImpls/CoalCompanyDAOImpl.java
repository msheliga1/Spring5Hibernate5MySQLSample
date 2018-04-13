package coalcamps.dao.hibernateImpls;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import coalcamps.classes.CoalCompany;
import coalcamps.dao.CoalCompanyDAO;
import java.util.*;
import javax.persistence.TypedQuery; 

/** 
 * Implements the {@link CoalCompany} database access interface using hibernate.
 * A SessionFactory is the only instance variable.  Routines are in CRUD order 
 * and include save, getById, get all, get count, update and delete.
 * 
 * @author Mike Sheliga 4.8.18
 */
public class CoalCompanyDAOImpl extends BaseDAOImpl<CoalCompany> implements CoalCompanyDAO {
			
	/**
	 *  This is the bean from the applicationContext xml file. Set using Dependency Injection.
	 */
	private SessionFactory companyFactory;  
		

	/**
	 * Set the companyFactory.  Needed for bean class.
	 * 
	 * @param companyFactory  A companyFactory to be stored.
	 */
	public void setSessionFactory(SessionFactory companyFactory) {
			this.companyFactory = companyFactory;
	} 
	
	/** 
	 * Return the companyFactory instance variable. Needed for bean class.
	 * 
	 * @return the immutable companyFactory instance variable.
	 */
	public SessionFactory getSessionFactory() {
	 	return companyFactory;
	}

	
	// ------ NonStandard Methods ------
	/**
	 * Returns the actual class for this DAO.
	 * 
	 * @return a Class variable with the parameterized type CoalComapny 
	 */
	@Override
	public Class<CoalCompany> getParameterizedClass() {
		return CoalCompany.class;
	}
			
	// ------ Hibernate Database Methods (in CRUD order) ------
	/*** 
	 * Save the CoalCompany to the database
	 * 
	 * @deprecated  replaced by generic save routine
	 * @param company  a coal company that is saved to the database using session.save
	 */		
	public void saveCoalCompany(CoalCompany company) {
		Session session = null;
		Transaction tx = null;
		try {
			session = companyFactory.openSession();
			tx = session.beginTransaction();
			// save and persist are slightly different - using GenerationType.IDENTITY or AUTO
			session.save(company);  // save creates 3 of 3 records - MJS 4.1.18
			// persist creates 2 of 3 companies MJS 4.1.18 (randp id=8 rolled back).
			// session.persist(company);  // creates 2 of 3 companies 4.1.18 MJS
			tx.commit();
		} catch (org.hibernate.PropertyValueException | 
				 org.springframework.dao.DataIntegrityViolationException ex) {
			System.out.println(company.toString() + 
				" could not be saved. Maybe it is already in the database? " + 
				" ---- Exception Details ---- " + ex.toString() + ex.getMessage() );
			if (tx != null) tx.rollback();
		} catch (Exception ex) {
			// even though exception is caught, exception trace is printed to the console.
			System.out.println(company.toString() + " could not be saved. Maybe it already exists? " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
			if (tx != null) tx.rollback();
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
	}  // end saveCoalCompany(CoalCompany company)		
			
	/** 
	 * Returns a CoalCompany based upon primary key ID
	 * 
	 * @deprecated replaced by generic getById in BaseDAOImpl
	 * @param ID  an int representing the primary key of the coal company
	 * @return the CoalCompany with the given ID or null if its not in the database
	 */
	public CoalCompany getCoalCompanyById(int ID) {
		Session session = null;
		Transaction tx = null;
		CoalCompany company = null;
		try {
			session = companyFactory.openSession();
			tx = session.beginTransaction();
			company = (CoalCompany) session.get(CoalCompany.class, ID);			
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Company could not be retrieved by ID (" + ID + "). Maybe it does not exist? " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) 		session.close();
		}
		return company;
	} // end getCoalCompanyById
			
	/**
	 * Returns a list of all CoalCompanies
	 * 
	 * @return a list of all coal companies using TypedQuery.
	 */
	public List<CoalCompany> getCoalCompanies() {  
		// loadAll only for HibernateTemplates (hibernate 3)
		Session session = null;
		Transaction tx = null;
	    List<CoalCompany> list = null;
	    try {
		    session = companyFactory.openSession();
		    // must be beginTransaction, not getTransaction unless also tx.begin
		    tx = session.beginTransaction();  
		    TypedQuery<CoalCompany> query = session.createQuery("FROM CoalCompany", CoalCompany.class);
		    list = query.getResultList();  // replaces pre hibernate5 Query and query.list()
		    if (tx != null) tx.commit();
		} catch (Exception ex) {
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println("Could not get list of coalCompanies. " + 
				" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
			if (tx != null) tx.rollback();
	    } finally { 
		    if (session != null) session.close();
		} // end try-catch-finally
		return list;  
	} // end getCoalCompanies
 
	public List<CoalCompany> getCoalCompaniesWRONG() {			
	    // loadAll only for HibernatgeTemplates	
		Session session = null;
		Transaction tx = null;
		List<CoalCompany> list = null;
		try {  // session doesnt implement auto-closeable, so no try with resources.
			session = companyFactory.openSession();
			tx = session.getTransaction();   // THIS IS WRONG, use beginTransaction!
		    TypedQuery<CoalCompany> query = session.createQuery("FROM CoalCompany", CoalCompany.class);
		    list = query.getResultList();  // replaces pre hibernate5 Query and query.list()
			tx.commit();
		} catch (Exception ex) {
			System.out.println("Could not get list of coalCompanies. " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
			if (tx != null) tx.rollback();
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
		return list;  
	}  // getCoalCompaniesWRONG
		
	/**
	 * Returns a count of the number of CoalCompanies
	 * 
	 * @return an int representing the total number of coal companies in the database.
	 */ 
	public int getCoalCompanyCount( ) {
		int result = 0;
		Session session = null;
		Transaction tx = null;
		try {
			session = companyFactory.openSession();
			tx = session.beginTransaction();
			TypedQuery<Long> query = session.createQuery("SELECT count(*) FROM CoalCompany", Long.class);
			result = ((Long) query.getSingleResult()).intValue();  // pre hibernate5 was Query and uinqueResult
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not get count of coalCompanies. " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
		return result;
	} // end getCoalCompanyCount

	/**
	 * Updates a CoalCompany database record.
	 * 
	 * @deprecated  replaced by generic update routine
	 * @param company  A CoalCompany that is to be updated in the hibernate database.
	 */	
	public void updateCoalCompany(CoalCompany company) {
		Session session = null;
		Transaction tx = null;
		try {
			session = companyFactory.openSession();
			tx = session.beginTransaction();
			session.update(company);
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not get counnt of coalCompanies. " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
	} // end UpdateCoalCompany

	/*** 
	 * Deletes the CoalCompany from the database. The company will not be deleted if it is 
	 * referenced from another table (such as the CoalCamp or CampLease tables).  In this case
	 * an exception is thrown and handled.
	 * 
	 * @deprecated  replaced by generic delete routine
	 * @param company  A CoalCompany that is to be updated in the hibernate database.
	 */	
	public void deleteCoalCompany(CoalCompany company) {
		Session session = null;
		Transaction tx = null;
		try {
			session = companyFactory.openSession();
			tx = session.beginTransaction();
			session.delete(company);
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not delete coalCompany. " + company + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
	} // end DeleteCoalCompany		
		
} // end class CoalCompanyDAOImpl
