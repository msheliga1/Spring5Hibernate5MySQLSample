package coalcamps.classes;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import coalcamps.dao.CoalCompanyDAO;

/** 
 * Contains a POJO class annotated for ORM databases that represent a coal mining company.
 * Of particular interest are companies that built "company towns", also known as coal
 *  camps. The class is annotated to be used in a hibernate database.
 * This class consists of standard getter and setters, two constructors and 
 * an overridden toString routine. 
 * The standard getters and setters and toString have no side effects and are not further 
 * commented for purposes of brevity.
 * 
 * @author Mike Sheliga 3.15.18
 */

@Entity
@Table(name="Coal_Company")
public class CoalCompany extends BaseCCObject implements Serializable {
	    
	// private static ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring4.xml");
	static final long serialVersionUID = 1L;

	@Column  
	private String companyName; 
	private int yearFounded; 
	    
	// ----------------- Constructors --------------------------------------
	/**
	 * No argument constructor.
	 */
	public CoalCompany() { }
	    
	/**
	 * Constructor with all values except primary key id. 
	 * 
	 * @param companyName  a String representing the name of the company. Do not
	 * include the word company in the name.
	 * @param yearFounded  an int indicating the year the company was incorporated.
	 */
	public CoalCompany(String companyName, int yearFounded) {
	    this.companyName = companyName;
	    this.yearFounded = yearFounded;
	}
	    
	// ------ Database Methods (in CRUD order) ------
	// Methods without CoalCompany in the name are generic routines.
	/**
	 * Saves the coal company to the database using the CoalCompanyDAO appContext bean.
	 */
	public void save() {
	    CoalCompanyDAO companyDao = (CoalCompanyDAO) getApplicationContext().getBean("compDaoBean");
	    companyDao.save(this);
	} // end save
	    
	/**
     * Retrieve a coal company by ID using the CoalCompanyDAO appContext bean.
     * 
     * @param ID the primary key ID of the coal company
     * @return a coal company with the given ID from the database, or null if not present
     */
	public static CoalCompany getById(int ID) {
		CoalCompanyDAO companyDao = (CoalCompanyDAO) getApplicationContext().getBean("compDaoBean");
		return companyDao.getById(ID);  // uses companyDao to get CoalCompany.class
		// return companyDao.getCoalCompanyById(ID);
	} // end getById

	/**
	 * Retrieve a list of all database coal companies using the CoalCompanyDAO appContext bean.
	 * 
	 * @return a list of all coal companies in the database
	 */
	public static List<CoalCompany> getCoalCompanies() {
		CoalCompanyDAO companyDao = (CoalCompanyDAO) getApplicationContext().getBean("compDaoBean");
	    return companyDao.getCoalCompanies();
	} // end getCoalCompanies
	
	/**
     * Retrieve the number of database coal companies using the CoalCompanyDAO appContext bean.
     * 
     * @return the number of coal companies in the database
     */
	public static int getCoalCompanyCount() {
		CoalCompanyDAO companyDao = (CoalCompanyDAO) getApplicationContext().getBean("compDaoBean");
		return companyDao.getCoalCompanyCount();
	} // end getCoalCompanyCount
	    
	/**
	 * Updates the coal company in the database using the CoalCompanyDAO appContext bean.
	 */
	public void update() {
	    CoalCompanyDAO companyDao = (CoalCompanyDAO) getApplicationContext().getBean("compDaoBean");
	    companyDao.update(this);  // uses companyDao to get CoalCompany.class
	 } // end update
	    
	/**
	 * Deletes the coal company from the database using the CoalCompanyDAO appContext bean.
	 */
	public void delete() {
	    CoalCompanyDAO companyDao = (CoalCompanyDAO) getApplicationContext().getBean("compDaoBean");
	    companyDao.delete(this);
	} // end delete
	    
	// --------------------- Standard Methods ----------------------
	@Override public String toString() {
	    return "Coal Company: ID: " + this.getId() + " Name: " + companyName + " Year Founded: " + yearFounded;
	}
	    
	// ------ Standard Getters and Setters (no side effects, not javaDoced) -------

	    public String getCompanyName() {return companyName;}
	    public void setCompanyName(String companyName) {this.companyName = companyName;}

	    public int getYearFounded() {return yearFounded;}
	    public void setYearFounded(int yearFounded) {this.yearFounded = yearFounded;}

} // end class CoalCompany

