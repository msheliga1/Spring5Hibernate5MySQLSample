package coalcamps.dao.hibernateImpls;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import coalcamps.classes.BaseCCObject;
import coalcamps.dao.BaseDAO;

import java.time.ZonedDateTime;

/** 
 * Implements common coal camp database access interface using hibernate.
 * A SessionFactory is the only instance variable, fetched from implementing classes 
 * using getSessionFactory, at least for now. Routines are in CRUD order 
 * and will eventually include save, getById, get all, get count, possibly a search, 
 * update and delete.
 * 
 * @author Mike Sheliga 4.8.18
 */
public abstract class BaseDAOImpl<T extends BaseCCObject> implements BaseDAO<T> { 
	
	// ============ Instance variables and getter-setters ============		
	/*
	 *  This is the bean from the applicationContext xml file. Set using Dependency Injection.
	 //
	// For now rely on each pojo having its own sessionFactory. 
	// This also causes a error if this class is made abstract, which is needed for 
	// getParameterizedType routine needed for getByID.
	@Autowired 
	private SessionFactory sessionFactory;  		

	/**
	 * Set the sessionFactory.  Needed for bean class.
	 * 
	 * @param sessionFactory  A sessionFactory to be stored.
	 //
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	} 
	
	/** 
	 * Return the sessionFactory instance variable. Needed for bean class.
	 * 
	 * @return the immutable sessionFactory instance variable.
	 //
	// public abstract SessionFactory getSessionFactory();
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}  
	*/

	// ------ Abstract Methods ------
	/**
	 *  Returns the parameterized type of the derivedDAO such as Class-CoalCompany for CoalCompanyDAOImpl
	 *  
	 *  @return a parameterized Class object with the parameter type being the derivedDAO's object type.
	 */
	public abstract Class<T> getParameterizedClass();
	public abstract SessionFactory getSessionFactory();
	
	// ------ Hibernate Database Methods ------
	/**
	 * Save a database record to the coal camp database using save (not persist).
	 * 
	 * @param ccObj  a coal camp object that is to be saved in the hibernate database
	 * @return   an int representing the primary key of the saved object.
	 */	
	public int save(T ccObj) {
		int result = 0;
		Session session = null;
		Transaction tx = null;
		try {
			session = getSessionFactory().openSession();
			tx = session.beginTransaction();
			result = (Integer) session.save(ccObj);
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not generically save " + ccObj + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
		return result;
	} // end save
	
	/** 
	 * Returns an object extending BaseCCObject based upon primary key ID and the class type 
	 * 
	 * @param ID  an int representing the primary key of the coal company
	 */
	public T getById(int ID) {
		// previously Class<T> objClass was passed into this routine.
		Session session = null;
		Transaction tx = null;
		T result = null;
		Class<T> objectClass = this.getParameterizedClass();
		try {
			// would like to get the Class (such as CoalCompany) from 
			// "this", but this is of type CoalCompanyDAOImpl MJS 4.12.18	
			// According to stackOverflow public class Foo<T extends Bar>{} will let you get Bar, 
			// but not at the subtype of Bar you are actually using. It doesn't work, sorry.
			session = getSessionFactory().openSession();
			tx = session.beginTransaction();
			result = (T) session.get(objectClass, ID);
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println(objectClass.getSimpleName() + "Could not be retrieved by ID(" + ID + 
				"). Maybe it does not exist? " + " ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		}
		return result;
	} // end getById
	
	/**
	 * Updates a database record for the coal camp database.
	 * 
	 * @param ccObj  A coal camp object that is to be updated in the hibernate database.
	 */	
	public void update(T ccObj) { // no longer pass in pojoClass
		Session session = null;
		Transaction tx = null;
		try {
			session = getSessionFactory().openSession();
			tx = session.beginTransaction();
			// need to get ID, get object by ID, get created Date,
			// set createdDate then update
			int id = ccObj.getId();
			// Class clazz = ccObj.getClass(); neither this nor ccObj.class would work.
			T dbObj = getById(id);
			ccObj.setDateCreated(dbObj.getDateCreated());
			ccObj.setDateModified(ZonedDateTime.now());
			session.update(ccObj);
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not generically update " + ccObj + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
	} // end update

	/**
	 * Deletes the object from the database. The ccObj will not be deleted if it is 
	 * referenced from another table.  In this case an exception is thrown and handled.
	 * 
	 * @param ccObj  A coal camp object that is to be deleted from the hibernate database.
	 */	
	public void delete(T ccObj) {
		Session session = null;
		Transaction tx = null;
		try {
			// session = sessionFactory.openSession();
			// for now, reply on each pojo having its own sessionFactory
			session = getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.delete(ccObj);
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not generically delete " + ccObj + 
					" ---- Exception Name ---- " + ex.toString() + " " + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
	} // end delete		
		
} // end class BaseDAOImpl
