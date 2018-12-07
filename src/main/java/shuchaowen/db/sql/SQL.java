package shuchaowen.db.sql;

import java.io.Serializable;

public interface SQL extends Serializable{
	String getSql();
	
	Object[] getParams();
}