package coalcamps.classes;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import coalcamps.dao.CoalCampDAO;

/** 
 * Contains a POJO class annotated for ORM databases that represent a town built by a coal company for 
 * the purpose of mining coal.  Such "company towns" were also known as coal
 * camps. This class consists of standard getter and setters, two constructors and 
 * an overridden toString routine. The class is annotated to be used in a hibernate
 * database. The standard getters and setters have no side effects and are not further 
 * commented for purposes of brevity.
 * 
 * @author Mike Sheliga 3.15.18
 *
 */
@Entity
@Table(name="Coal_Camp")
// @FetchProfiles({ @FetchProfile(name = "camp_builder", fetchOverrides = 
// 	{ @FetchProfile.FetchOverride(entity = CoalCamp.class, association = "companyBuilding", mode = FetchMode.JOIN ) }) })
public class CoalCamp extends BaseCCObject implements Serializable {
	
	static final long serialVersionUID = 1L;

	@Column 
	private String campName; 
	private int yearBuilt;
	@ManyToOne(fetch=FetchType.LAZY, optional=false)  // optional=false
	private CoalCompany companyBuilding;  // foreign key in DB
	
	// ------ Constructors ------------------------
	/**
	 * No argument constructor.
	 */
	public CoalCamp( ) {}

	/**
	 * Constructor with all arguments except primary key.
	 * 
	 * @param campName         a string representing the common name of the town
	 * @param yearBuilt        an int representing the year the town was built
	 * @param companyBuilding  the CoalCompany that built the coal camp 
	 */
	public CoalCamp(String campName, int yearBuilt, CoalCompany companyBuilding) {
		this.campName = campName;
		this.yearBuilt = yearBuilt;
		this.companyBuilding = companyBuilding;
	}
	
	// ------ database routines (in CRUD order) ------
	// routines without camp in name are generic routines in BaseDAO
	/**
	 * Saves the coal camp to the database by fetching the CoalCampDAO and calling the generic save.
	 */
	public void save() {
		CoalCampDAO campDao = (CoalCampDAO) getApplicationContext().getBean("campDaoBean", CoalCampDAO.class);
		campDao.save(this);
	}
			
	/**
	 * Gets the coal camp from the database by fetching the CoalCampDAO and calling getCoalCampById.
	 * 
	 * @param ID the ID of the coal camp. 
	 * @return a CoalCamp or null if the camp could not be found.
	 */
	public static CoalCamp getById(int ID) {
		CoalCampDAO campDao = (CoalCampDAO) getApplicationContext().getBean("campDaoBean", CoalCampDAO.class);
		return campDao.getById(ID);
	} // end getById(int ID)	
	
	/**
	 * Gets the coal camps from the database using the default fetching strategy.
	 *  
	 * @return a list of coal camps using the default fetching strategy for companyBuilding
	 */
	public static List<CoalCamp> getCoalCamps() {
		CoalCampDAO campDao = (CoalCampDAO) getApplicationContext().getBean("campDaoBean", CoalCampDAO.class);
		return campDao.getCoalCamps();
	} // end getCoalCamps()
	
	/**
	 * Gets the coal camps from the database by fetching the CoalCampDAO and calling getCoalCamps.
	 * 
	 * @param eager indicates if eager fetching of buildingCompany should occur. 
	 * @return a list of coal camps either with or without the buildingCompany fetched.
	 */
	public static List<CoalCamp> getCoalCamps(boolean eager) {
		CoalCampDAO campDao = (CoalCampDAO) getApplicationContext().getBean("campDaoBean", CoalCampDAO.class);
		return campDao.getCoalCamps(eager);
	} // end getCoalCamps(eager)
	
	/**
	 * Updates the coal camp in the database by fetching the CoalCampDAO and calling the generic update.
	 */
	public void update() {
		CoalCampDAO campDao = (CoalCampDAO) getApplicationContext().getBean("campDaoBean", CoalCampDAO.class);
		campDao.update(this);  // uses campDao to get CoalCamp class for getById
	}
	
	/**
	 * Deletes the coal camp to the database by fetching the CoalCampDAO and calling the generic delete.
	 */
	public void delete() {
		CoalCampDAO campDao = (CoalCampDAO) getApplicationContext().getBean("campDaoBean", CoalCampDAO.class);
		campDao.delete(this);
	}
	
	// ------ standard getters and setters --------
	// public int getId() {return id;}
	// public void setId(int id) {this.id = id;}
	
	public String getCampName() {return campName;}
	public void setCampName(String campName) {this.campName = campName;}

	public int getYearBuilt() {return yearBuilt;}	
	public void setYearBuilt(int yearBuilt) {this.yearBuilt = yearBuilt;}
	
	public CoalCompany getCompanyBuilding() {return companyBuilding;}
	public void setCompanyBuilding(CoalCompany companyBuilding) {this.companyBuilding = companyBuilding;}
	
	/**
	 * Returns all coal camp info including the companyBuilding if initializede. 
	 * If the foreign key company building is not set (likely due to lazy initialization),
	 * a message is output. This is mainly done for demonstration purpose.
	 * 
	 * @return  a string representing the coal camp, including the company that built the camp if known
	 */
	@Override
	public String toString() {
		String result = "CoalCamp: ID=" + this.getId() + " Name=" + campName + " Year Built=" + yearBuilt;
		if (companyBuilding == null) return result;	
		try {
			// next line may produce a LazyInitializationException
			String company = " Company Building=" + companyBuilding.getCompanyName();
			result = result + company;
		} catch (org.hibernate.LazyInitializationException ex) {
			System.out.println("LazyInitializationException in CoalCamp.toString(). " + 
					"Returning result with no company.");
		} catch (Exception ex) {
			System.out.println("Unexpected Exception in CoalCamp.toString(). " + 
					"Returning result with no company.");
		}
		return result;
	}
	
} // end class CoalCamp
