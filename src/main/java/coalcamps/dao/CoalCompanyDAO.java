package coalcamps.dao;

import java.util.List;
import coalcamps.classes.CoalCompany; 

/** 
 * Interface that must be implemented for a CoalCompany 
 * to access a database.
 * 
 * @author Mike Sheliga 3.25.18
 *
 */
public interface CoalCompanyDAO extends BaseDAO<CoalCompany> {
		
	// ------ database methods (in CRUD order)  ------
	// public void saveCoalCompany(CoalCompany co);

	// public CoalCompany getCoalCompanyById(int ID);
	// (no need to worry about lazy vs eager since no foreign keys).  
	public List<CoalCompany> getCoalCompanies();
	public List<CoalCompany> getCoalCompaniesWRONG();
	public int getCoalCompanyCount( );	

	// public void updateCoalCompany(CoalCompany e);

	// public void deleteCoalCompany(CoalCompany e);
		
} // end interface CoalCompanyDao

