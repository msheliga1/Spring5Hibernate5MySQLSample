package coalcamps.dao;

import coalcamps.classes.BaseCCObject; 

/** 
 * Contains database routines that are implemented by all 
 * coal camp objects. It is limited to a few basic CRUD 
 * operations for now but can be expanded in the future.
 * 
 * @author Michael Sheliga 4.10.18
 *
 */

public interface BaseDAO<T extends BaseCCObject> {

	// ------Parameterized Database routines (in CRUD order) ------
		
	public int save(T e);
	
	public T getById(int ID);  // no longer pass in Class<> pojoClass
	
	public void update(T e);  // no longer pass in Class<> pojoClass

	public void delete(T e);
			
} // end interface BaseDao