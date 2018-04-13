package coalcamps.mainTest;

import coalcamps.classes.CampLease;
import coalcamps.classes.CoalCamp;
import coalcamps.classes.CoalCompany;
import java.util.logging.Level;

public class Sp502Hib5212BaseDAO2 { 
	
	// Class to test a Spring5 Hibernate5 example using class annotations
	// and a generic DAO Implementatino. MJS 4.8.18
	public static void main(String[] args) {
		System.out.println("Starting Sp502Hib5212BaseDAO2 main.");
		// Use spring.xml for spring3 example.
		// Moved applicationContext and getBean to pojos MJS 4.10.18.
		// cant do getBean( , BaseDAO<CoalCamp>) without error.
		// Cant create baseDAO if it is abstract (which it is, at least for now).
		// BaseDAO<CoalCamp> baseDao = (BaseDAO<CoalCamp>) context.getBean("baseDaoBean", BaseDAO.class);
		// show fewer log msgs - way too many to read.
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
				
		if (CoalCompany.getCoalCompanyCount() == 0) {
		 	addCoalCampData();
		}
				
		// Couldnt use lambdas originally as java version was 1.5 in project and 1.6 in pom.
		// Changed project properties->BuildPath->Libraries->Edit to 1.8JDK. 
		// Also verified compile options and changed pom.xml version.
		CoalCompany.getCoalCompanies().stream().forEach((co)->System.out.println(co));
		System.out.println(" ------ getCoalCompanies(using lambda) above -------- ");
		
		boolean eager = true;  // LazyInitializationError if non-eager fetch
		CoalCamp.getCoalCamps(eager).stream().forEach(System.out::println); 
		System.out.println(" ------ getCoalCamps(eager=true above) -------- ");
		// note lazy initialization => dont get back companyBuilding => toString
		// will throw exception which will be caught, and company wont be printed.
		CoalCamp byId1 = CoalCamp.getById(1); 
		System.out.println("The coal camp with id=1 is " + byId1);
		
		for (CampLease cl: CampLease.getCampLeases(eager)) {
			System.out.println(cl.toString());
		}
		System.out.println(" ------ getCampLeases(eager=true above) -------- ");	
			
		System.out.println("Ending Sp502Hib5212BaseDAO2");
	} // end main
		
		
	// addCoalCampData (will replicate data if it already exists).
	public static void addCoalCampData( ) { 
		System.out.println("Inserting records into database.");

		CoalCompany co = null;		
		// CoalCamp camp = null;
		CampLease lease = null;
	
		co = new CoalCompany("Rochester and Pittsburgh", 1885);
		CoalCompany randp = co;
		System.out.println("RandP id unset, value is " + randp.getId());
		randp.setId(8);  // this has no effect upon save since id is generated as identity
		System.out.println("RandP id set to 8, value is " + randp.getId() + " ... saving .... ");
		// compDao.saveCoalCompany(co);
		randp.save();
		System.out.println("RandP saved, id is " + randp.getId());
		// R&P coal camps
		// campDao.saveCoalCamp(new CoalCamp("Iselin", 1905, randp));
		new CoalCamp("Iselin", 1905, randp).save();
		// campDao.saveCoalCamp(new CoalCamp("Hart Town", 1906, randp));
		// campDao.saveCoalCamp(new CoalCamp("Whiskey Run", 1906, randp));	
		// campDao.saveCoalCamp(new CoalCamp("Nesbitt Run", 1906, randp));
		// campDao.saveCoalCamp(new CoalCamp("Earnest", 1910, randp));
		CoalCamp lucerne = new CoalCamp("Lucerne", 1912, randp);
		lucerne.save();
		// campDao.saveCoalCamp(lucerne);
		lucerne.delete();
		
		(co = new CoalCompany("Cambria Steel", 1852)).save();
		CoalCamp slickville = new CoalCamp("slickville", 1917, co);  // note capitalization
		slickville.save();
		try {Thread.sleep(2000);} catch (Exception ex) { };
		slickville.setCampName("SLICKville");
		slickville.update();
		// campDao.saveCoalCamp(new CoalCamp("Wherum", 1890, co));
		// campDao.saveCoalCamp(new CoalCamp("Cokeville", 1910, co));
		
		new CoalCompany("Edwards", 1912).save();
		// campDao.saveCoalCamp(new CoalCamp("Edwards", 1920, co));
		
		lease = new CampLease(slickville, randp, 1922, 1924);
		lease.save();
		// leaseDao.saveCampLease(lease);
	} // end addCoalCampData 
	
} // end class Sp502Hib5212BaseDAO2