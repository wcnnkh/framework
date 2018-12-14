package shuchaowen.db.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableMapping implements Serializable{
	private static final long serialVersionUID = 1L;
	private Map<Class<?>, List<String>> tableMapping;
	
	public void register(Class<?> tableClass, String ...tableName){
		if(tableMapping == null){
			tableMapping = new HashMap<Class<?>, List<String>>();
		}
		
		List<String> list = tableMapping.get(tableClass);
		if(list == null){
			list = new ArrayList<String>();
		}
		
		for(String name : tableName){
			list.add(name);
		}
		
		tableMapping.put(tableClass, list);
	}
	
	public List<String> getTableNameList(Class<?> tableClass){
		return tableMapping == null? null:tableMapping.get(tableClass);
	}
}
