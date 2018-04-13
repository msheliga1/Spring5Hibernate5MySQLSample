package coalcamps.dao;

import coalcamps.classes.CampLease;
import java.util.List; 

/** 
 * Contains routines that must be implemented for a coal
 * CampLease to access a database.
 * 
 * @author Michael Sheliga 3.25.18
 *
 */

public interface CampLeaseDAO extends BaseDAO<CampLease> {

	// ------ Database routines (in CRUD order) ------
	// public void saveCampLease(CampLease lease);
			
	// public CampLease getCampLeaseById(int ID) ;			
	public List<CampLease> getCampLeases(); 
	// Return all campLeases with either lazy or eager fetching  
	public List<CampLease> getCampLeases(boolean eager);
	public int getCampLeaseCount( );
		
	// public void updateCampLease(CampLease e);

	// public void deleteCampLease(CampLease e);
			
} // end interface CampLeaseDao