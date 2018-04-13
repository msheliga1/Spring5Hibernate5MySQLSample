package coalcamps.classes;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** 
 * Contains a POJO class annotated for ORM databases that represent a common base class for 
 * all other base coal camp object POJO classes. Such "company towns" were also known as coal
 * camps. This class consists of properties that will be common to all pojo classes in the 
 * database such as the date created, the date last modified and a primary key ID.
 * 
 * @author Mike Sheliga 4.11.18
 *
 */
@MappedSuperclass  // Child classes will include fields in their hibernate table
public abstract class BaseCCObject implements Serializable {
	
	private static ApplicationContext applicationContext = 
		new ClassPathXmlApplicationContext("spring4.xml");	
	static final long serialVersionUID = 1L;
	
	/**
	 * If application context is null, initialize it.  Otherwise simply return it.
	 * 
	 * @return the application context obtained from an xml file such as appContext.xml
	 */
	public static ApplicationContext getApplicationContext() {
		if (applicationContext == null) {
			applicationContext = new ClassPathXmlApplicationContext("spring4.xml");
		}
		return applicationContext;
	}	

	// ============ Instance Members ============
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO) 
	// IDENTITY Wont work in hib5 unless pk is autoIncremented??? At least sometimes.
	// @GenericGenerator(name="gen",strategy="increment")
    // @GeneratedValue(generator="gen")
	private int id;
	@Column
	private ZonedDateTime dateCreated;
	@Column
	private ZonedDateTime dateModified;
	
	// ------------ Constructors ----------------
	/**
	 * No argument constructor.
	 */
	public BaseCCObject( ) {
		dateCreated = ZonedDateTime.now();
		dateModified = dateCreated;
	}

	/**
	 * Constructor with all arguments except primary key.
	 * 
	 * @param dateCreated    a date representing when the record was created
	 * @param dateModified   a date representing when the record was last modified
	 */
	public BaseCCObject(ZonedDateTime dateCreated, ZonedDateTime dateModified) {
		this.dateCreated = dateCreated;
		this.dateModified = dateModified;
	}
	
	// ------ static routines ------
	/**
	 * If application context is null, initialize it.  Otherwise simply return it.
	 * 
	 * @return the application context obtained from an xml file such as appContext.xml
	 */
	/* static private ApplicationContext getApplicationContext() {
		if (applicationContext == null) {
			applicationContext = new ClassPathXmlApplicationContext("spring4.xml");
		}
		return applicationContext;
	} */
	
	// ------ database routines (in CRUD order) ------
	// routines without camp in name are generic routines in BaseDAO
			
	/*
	 * Gets the base coal camp object from the database by fetching the BaseCCObjectDAO and calling getBaseCCObjectById.
	 * 
	 * @param ID the ID of the base coal camp object. 
	 * @return a BaseCCObject or null if the camp could not be found.

	public static BaseCCObject getBaseCCObjectById(int ID) {
		BaseCCObjectDAO campDao = (BaseCCObjectDAO) getApplicationContext().getBean("campDaoBean", BaseCCObjectDAO.class);
		return campDao.getBaseCCObjectById(ID);
	} // end getBaseCCObjectById(int ID)	
	 */

	/*
	 * Deletes the base coal camp object from the database by fetching the BaseCCObjectDAO and calling the generic delete.

	public void delete() {
		BaseCCObjectDAO campDao = (BaseCCObjectDAO) getApplicationContext().getBean("campDaoBean", BaseCCObjectDAO.class);
		campDao.delete(this);
	}
	 */
	
	// ------ standard getters and setters --------
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	
	public ZonedDateTime getDateCreated() {return dateCreated;}
	public void setDateCreated(ZonedDateTime dateCreated) {this.dateCreated = dateCreated;}

	public ZonedDateTime getDateModified() {return dateModified;}	
	public void setDateModified(ZonedDateTime dateModified) {this.dateModified = dateModified;}
	
	/**
	 * Returns all base coal camp object info including the companyBuilding if initializede. 
	 * If the foreign key company building is not set (likely due to lazy initialization),
	 * a message is output. This is mainly done for demonstration purpose.
	 * 
	 * @return  a string representing the base coal camp object, including the company that built the camp if known
	 */
	@Override
	public String toString() {
		String result = "BaseCCObject: ID=" + id +  " Created=" + dateCreated + " Modified=" + dateModified;
		/* CoalCompany companyBuilding = null;
		if (companyBuilding == null) return result;	
		try {
			// next line may produce a LazyInitializationException
			String company = " Company Building=" + companyBuilding.getCompanyName();
			result = result + company;
		} catch (org.hibernate.LazyInitializationException ex) {
			System.out.println("LazyInitializationException in BaseCCObject.toString(). " + 
					"Returning result with no company.");
		} catch (Exception ex) {
			System.out.println("Unexpected Exception in BaseCCObject.toString(). " + 
					"Returning result with no company.");
		} */
		return result;
	} // end toString
	
} // end class BaseCCObject
