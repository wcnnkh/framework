package shuchaowen.db.result;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import shuchaowen.common.utils.ClassUtils;
import shuchaowen.db.DB;

public final class Result implements Serializable{
	private static final long serialVersionUID = 1L;
	private MetaData metaData;
	private Object[] values;
	
	public Result(ResultSet resultSet) throws SQLException{
		metaData = new MetaData(resultSet.getMetaData());
		values = new Object[metaData.getColumns().length];
		for (int i = 0; i < values.length; i++) {
			values[i] = resultSet.getObject(i);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> type, TableMapping tableMapping){
		if(metaData == null || values == null || type == null){
			return null;
		}
		
		if (type.isArray()) {
			return (T) values;
		} else if (type.getName().startsWith("java")
				|| ClassUtils.isBasicType(type)) {
			if (values.length > 0) {
				return (T) values[0];
			}
			return null;
		} else {
			try {
				return (T) shuchaowen.db.result.ResultSet.wrapper(metaData, values, DB.getTableInfo(type),
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
			return (T) values;
		} else if (type.getName().startsWith("java")
				|| ClassUtils.isBasicType(type)) {
			if (values != null && values.length > 0) {
				return (T) values[0];
			}
			return null;
		} else {
			try {
				return (T) shuchaowen.db.result.ResultSet.wrapper(metaData, values, DB.getTableInfo(type),
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
		return values;
	}
}
