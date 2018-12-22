package scw.database;

import java.io.Serializable;

public interface SQL extends Serializable{
	String getSql();
	
	Object[] getParams();
}