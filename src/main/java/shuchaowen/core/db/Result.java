package shuchaowen.core.db;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import shuchaowen.core.db.proxy.BeanProxy;
import shuchaowen.core.db.proxy.BeanProxyMethodInterceptor;
import shuchaowen.core.util.ClassUtils;

public class Result implements Serializable{
	private static final long serialVersionUID = -3443652927449459314L;
	private TableMapping tableMapping;
	private LinkedHashMap<String, Object> dataMap;

	public Result() {
	};
	
	public TableMapping getTableMapping() {
		return tableMapping;
	}

	public void setTableMapping(TableMapping tableMapping) {
		this.tableMapping = tableMapping;
	}

	public Result(TableMapping tableMapping, ResultSet resultSet) throws SQLException {
		this.tableMapping = tableMapping;
		render(resultSet);
	}
	
	public String getTableName(Class<?> tableClass){
		return tableMapping == null? DB.getTableInfo(tableClass).getName():tableMapping.getTableName(tableClass);
	}
	
	public void render(ResultSet resultSet) throws SQLException {
		if(dataMap == null){
			dataMap = new LinkedHashMap<String, Object>();
		}
		
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnCount = rsmd.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			StringBuilder sb = new StringBuilder();
			String tName = rsmd.getTableName(i);
			if (tName != null && tName.length() != 0) {
				sb.append(tName);
				sb.append(".");
			}
			sb.append(rsmd.getColumnName(i));
			dataMap.put(sb.toString(), resultSet.getObject(i));
		}
	}

	public LinkedHashMap<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(LinkedHashMap<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type) {
		if(dataMap == null){
			return null;
		}
		
		if (type.isArray()) {
			Object[] arr = new Object[dataMap.size()];
			int i = 0;
			for (Entry<String, Object> entry : dataMap.entrySet()) {
				arr[i++] = entry.getValue();
			}
			return (T) arr;
		} else if (type.getName().startsWith("java") || ClassUtils.containsBasicValueType(type)) {
			for (Entry<String, Object> entry : dataMap.entrySet()) {
				return (T) entry.getValue();
			}
			return null;
		} else {
			TableInfo tableInfo = DB.getTableInfo(type);
			if (tableInfo.isTable()) {
				String tableName = getTableName(type);
				T t = BeanProxyMethodInterceptor.newInstance(type, tableInfo);
				String prefix = tableName + ".";
				boolean b = false;
				for (ColumnInfo columnInfo : tableInfo.getColumns()) {
					String name = prefix + columnInfo.getName();
					if (dataMap.containsKey(name)) {
						columnInfo.setValue(t, dataMap.get(name));
						b = true;
					}
				}

				if (b) {
					((BeanProxy) t).startListen();
					return t;
				}
			} else {
				T t = null;
				try {
					t = type.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

				if (t == null) {
					return null;
				}

				boolean b = false;
				for (ColumnInfo columnInfo : tableInfo.getColumns()) {
					String name = columnInfo.getName();
					if (dataMap.containsKey(name)) {
						columnInfo.setValue(t, dataMap.get(name));
						b = true;
					}
				}

				if (b) {
					return t;
				}
			}
			return null;
		}
	}
}
