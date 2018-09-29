package shuchaowen.core.db.storage;

import shuchaowen.core.db.ColumnInfo;
import shuchaowen.core.db.DB;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;

public class CacheUtils {
	private static final String OBJECT_KEY_COCAT = "#";
	
	public static String getObjectKey(Object obj) throws IllegalArgumentException, IllegalAccessException{
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
			sb.append(columnInfo.getFieldInfo().forceGet(obj));
		}
		System.out.println("op:" + sb.toString());
		return sb.toString();
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
		System.out.println("get:" + sb.toString());
		return sb.toString();
	}
}
