package scw.db.derby;

import scw.core.Assert;

public class DerbyUtils {
	private static final String DERBY_SYSTEM_HOME = "derby.system.home";
	
	public static void setDerbySystemHome(String derbySystemHome){
		Assert.requiredArgument(derbySystemHome != null, "derbySystemHome");
		System.setProperty(DERBY_SYSTEM_HOME, derbySystemHome);
	}
}
