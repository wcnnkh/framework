package shuchaowen.core.db.cache;

import java.util.Arrays;

import shuchaowen.core.db.ColumnInfo;
import shuchaowen.core.db.DB;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;

public class CacheUtils {
	private static final String OBJECT_KEY_COCAT = "#";
	
	public static String getObjectKey(Object obj){
		TableInfo tableInfo = DB.getTableInfo(obj.getClass());
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}
		
		if(tableInfo.getPrimaryKeyColumns().length == 0){
			throw new ShuChaoWenRuntimeException(ClassUtils.getCGLIBRealClassName(obj.getClass()) + " not found primary key");
		}
		
		StringBuilder sb = new StringBuilder(64);
		sb.append(ClassUtils.getCGLIBRealClassName(obj.getClass()));
		for(ColumnInfo columnInfo : tableInfo.getPrimaryKeyColumns()){
			sb.append(OBJECT_KEY_COCAT);
			sb.append(columnInfo.getValue(obj));
		}
		return sb.toString();
	}
	
	public static String getObjectParamsKey(Object ...params){
		return Arrays.toString(params);
	}
	
	public static String getObjectPrimaryKeyColumns(Object bean){
		TableInfo tableInfo = DB.getTableInfo(bean.getClass());
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}
		
		if(tableInfo.getPrimaryKeyColumns().length == 0){
			throw new ShuChaoWenRuntimeException(ClassUtils.getCGLIBRealClassName(bean.getClass()) + " not found primary key");
		}
		
		Object[] params = new Object[tableInfo.getPrimaryKeyColumns().length];
		for(int i=0; i<params.length; i++){
			ColumnInfo columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			params[i] = columnInfo.getValue(bean);
		}
		return getObjectParamsKey(params);
	}
	
	public static String getObjectKey(Class<?> tableClass, Object ...params){
		TableInfo tableInfo = DB.getTableInfo(tableClass);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}

		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (tableInfo.getPrimaryKeyColumns().length != params.length) {
			throw new NullPointerException(
					"params length not equals primary key lenght");
		}
		
		StringBuilder sb = new StringBuilder(64);
		sb.append(ClassUtils.getCGLIBRealClassName(tableClass));
		for(Object v : params){
			sb.append(OBJECT_KEY_COCAT);
			sb.append(v);
		}
		return sb.toString();
	}
}
