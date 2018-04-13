package coalcamps.classes;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import coalcamps.dao.CampLeaseDAO;

/** 
 * Contains a POJO class annotated for ORM databases that represent a lease of a coal camp. 
 * Before the advent of automobiles entire towns, or coal camps, were built by mining 
 * companies to house miners. On occasion these towns and their associated mines 
 * were leased to other mining companies. 
 * <p> 
 * This class is annotated to be used in a hibernate database. 
 * It consists of database acess routines, standard getter and setters, two constructors and 
 * an overridden toString routine. 
 * The standard getters and setters use Java Bean names, encapsulate private instance 
 * variables, have no side effects and are not further commented for purposes of brevity.
 * 
 * @author Mike Sheliga 3.15.18
 */
@Entity
@Table(name="Camp_Lease")
public class CampLease extends BaseCCObject implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	// @Id
	@Column
	// @GeneratedValue(strategy=GenerationType.AUTO)  // Identity may give a hib5 exception. 
	// private int id;
	private int beginYear;
	private int endYear;
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private CoalCamp campLeased;  // foreign key in DB
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private CoalCompany companyLeasing;  // foreign key in DB
	
	// ------ Constructors -------
	/** 
	 * No argument no body constructor.
	 */
	public CampLease( ) {}
	
	/**
	 * Constructor with a parameter for all fields except primary key id.
	 * 
	 * @param campLeased     the CoalCamp that was leased
	 * @param companyLeasing the CoalCompany that leased the coal camp
	 * @param beginYear      the year the lease began
	 * @param endYear        the year the lease ended
	 */
	public CampLease(CoalCamp campLeased, CoalCompany companyLeasing, int beginYear, int endYear) {
		this.campLeased = campLeased;
		this.companyLeasing = companyLeasing;
		this.beginYear = beginYear;
		this.endYear = endYear;
	} // end CampLease constructor
	
	// ------ database routines (in CRUD order) ------
	// routines without campLease in the name are generic routines in BaseDAO
	/**
	 * Saves the camp lease to the database by fetching the CampLeaseDAO and calling the generic save.
	 */
	public void save() {
		CampLeaseDAO leaseDao = (CampLeaseDAO) getApplicationContext().getBean("leaseDaoBean", CampLeaseDAO.class);
		leaseDao.save(this);
	}
			
	/**
	 * Gets the camp lease of the given ID from the database using the applicationContext CampLeaseDAO bean.
	 * 
	 * @param ID the ID of the camp lease. 
	 * @return a CampLease or null if the camp could not be found.
	 */
	public static CampLease getById(int ID) {
		CampLeaseDAO leaseDao = (CampLeaseDAO) getApplicationContext().getBean("leaseDaoBean", CampLeaseDAO.class);
		return leaseDao.getById(ID);
	} // end getById(int ID)	
	
	/**
	 * Gets the camp leases from the database using the default fetching strategy.
	 *  
	 * @return a list of camp leases using the default fetching strategy for companyBuilding
	 */
	public static List<CampLease> getCampLeases() {
		CampLeaseDAO leaseDao = (CampLeaseDAO) getApplicationContext().getBean("leaseDaoBean", CampLeaseDAO.class);
		return leaseDao.getCampLeases();
	} // end getCampLeases()
	
	/**
	 * Gets the camp leases from the database by fetching the CampLeaseDAO and calling getCampLeases.
	 * 
	 * @param eager indicates if eager fetching of buildingCompany should occur. 
	 * @return a list of camp leases either with or without the buildingCompany fetched.
	 */
	public static List<CampLease> getCampLeases(boolean eager) {
		CampLeaseDAO leaseDao = (CampLeaseDAO) getApplicationContext().getBean("leaseDaoBean", CampLeaseDAO.class);
		return leaseDao.getCampLeases(eager);
	} // end getCampLeases(eager)
	
	/**
	 * Retrieves the number of camp leases in the database using the applicationContext CampLeaseDAO bean.
	 * 
	 * @return a list of camp leases either with or without the buildingCompany fetched.
	 */
	public static int getCampLeaseCount( ) {
		CampLeaseDAO leaseDao = (CampLeaseDAO) getApplicationContext().getBean("leaseDaoBean", CampLeaseDAO.class);
		return leaseDao.getCampLeaseCount();
	} // end getCampLeaseCount()

	/**
	 * Updates the camp lease in the database by fetching the CampLeaseDAO and calling the generic update.
	 */
	public void update() {
		CampLeaseDAO leaseDao = (CampLeaseDAO) getApplicationContext().getBean("leaseDaoBean", CampLeaseDAO.class);
		leaseDao.update(this);  // routine now uses leaseDao to get CampLease class
	}
	
	/**
	 * Deletes the camp lease to the database by fetching the CampLeaseDAO and calling the generic delete.
	 */
	public void delete() {
		CampLeaseDAO leaseDao = (CampLeaseDAO) getApplicationContext().getBean("leaseDaoBean", CampLeaseDAO.class);
		leaseDao.delete(this);
	}	
	
	// ------ standard methods --------------------
	/**
	 * Returns all camp lease info including the foreign keys campLeased and companyBuilding. 
	 * If the camp that was leased or the company building the camp is not set 
	 * (likely due to lazy initialization) this information is not output.
	 * 
	 * @return  a String representing the coal town lease, with the camp and company
	 * not mentioned if they are not known.
	 */
	@Override
	public String toString() {
		String camp = "";
		try {
			if (campLeased != null) camp = " Camp " + campLeased.getCampName() + " Leased";
		} catch (Exception ex) {
			// swallow exceptions such as LazyInitialization Exception
		}
		String company = "";
		try {
			if (companyLeasing != null) company = " by " + companyLeasing.getCompanyName();
		} catch (Exception ex) {
			// swallow exceptions such as LazyInitialization Exception
		}
		String id = "Camp Lease: ID=" + this.getId();
		String years = " (" + beginYear + " - " + endYear + ")";
		return id + camp + company + years;
	} // end toString
	
	// ------ standard getters and setters (no side effects, not javaDoced --------
	// public int getId() {return id;}
	// public void setId(int id) {this.id = id;}
		
	public CoalCamp getCampLeased() {return campLeased;}
	public void setCampLeased(CoalCamp campLeased) {this.campLeased = campLeased;}

	public CoalCompany getCompanyLeasing() {
		return companyLeasing;
	}
	public void setCompanyLeasing(CoalCompany companyLeasing) {
		this.companyLeasing = companyLeasing;
	}
	public int getBeginYear() {
		return beginYear;
	}
	public void setBeginYear(int beginYear) {
		this.beginYear = beginYear;
	}
	public int getEndYear() {
		return endYear;
	}
	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}
	
} // end class CampLease
