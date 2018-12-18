package scw.db.result;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import scw.common.utils.ClassUtils;
import scw.db.DB;

public final class Result implements Serializable{
	private static final long serialVersionUID = 1L;
	private MetaData metaData;
	private Object[] values;
	
	public Result(ResultSet resultSet) throws SQLException{
		metaData = new MetaData(resultSet.getMetaData());
		values = new Object[metaData.getColumns().length];
		for (int i = 0; i < values.length; i++) {
			values[i] = resultSet.getObject(i + 1);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> type, TableMapping tableMapping){
		if(metaData == null || values == null || type == null){
			return null;
		}
		
		if (type.isArray()) {
			return (T) getValues();
		} else if (type.getName().startsWith("java")
				|| ClassUtils.isBasicType(type)) {
			if (values.length > 0) {
				return (T) values[0];
			}
			return null;
		} else {
			try {
				return (T) scw.db.result.ResultSet.wrapper(metaData, values, DB.getTableInfo(type),
						tableMapping);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> type, String ...tableNames){
		if (metaData == null || type == null || values == null) {
			return null;
		}

		if (type.isArray()) {
			return (T) getValues();
		} else if (type.getName().startsWith("java")
				|| ClassUtils.isBasicType(type)) {
			if (values != null && values.length > 0) {
				return (T) values[0];
			}
			return null;
		} else {
			try {
				return (T) scw.db.result.ResultSet.wrapper(metaData, values, DB.getTableInfo(type),
						tableNames);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public Object[] getValues() {
		if(values == null){
			return null;
		}
		
		Object[] dest = new Object[values.length];
		System.arraycopy(values, 0, dest, 0, dest.length);
		return dest;
	}
}
