package shuchaowen.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TableMapping implements Serializable{
	private static final long serialVersionUID = 7540610276034050693L;
	private Map<Class<?>, String> mapping;
	
	public void register(Class<?> tableClass, String tableName){
		if(mapping == null){
			mapping = new HashMap<Class<?>, String>();
		}
		mapping.put(tableClass, tableName);
	}
	
	public String getTableName(Class<?> tableClass){
		String name = null;
		if(mapping != null){
			name = mapping.get(tableClass);
		}
		
		if(name == null){
			return DB.getTableInfo(tableClass).getName();
		}
		return name;
	}
}
