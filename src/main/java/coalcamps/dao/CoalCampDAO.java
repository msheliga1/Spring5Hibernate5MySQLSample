package coalcamps.dao;

import java.util.*; 
import coalcamps.classes.CoalCamp;

/** 
 * Interface that must be implemented for a CoalCamp to 
 * access database routines. 
 * 
 * @author Mike Sheliga 3.25.18
 *
 */
public interface CoalCampDAO extends BaseDAO<CoalCamp> {
// be sure to extend BaseDAO or calls such as campDao.delete(CoalCamp) 
// wont work in classes such as CoalCamp.
	
	// ------ Database Methods ------			
	// public void saveCoalCamp(CoalCamp camp);
	
	// public CoalCamp getCoalCampById(int ID);			
	public List<CoalCamp> getCoalCamps();
	public List<CoalCamp> getCoalCamps(boolean eager);		
	public int getCoalCampCount( );	

	// public void updateCoalCamp(CoalCamp camp);

	// public void deleteCoalCamp(CoalCamp camp);
			
} // end class CoalCampDAO
