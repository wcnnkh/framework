package shuchaowen.core.db.storage;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import shuchaowen.core.db.ColumnInfo;
import shuchaowen.core.db.DB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.proxy.BeanProxy;
import shuchaowen.core.db.proxy.BeanProxyMethodInterceptor;
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
		return sb.toString();
	}
	
	public static String getObjectPrimaryKey(Object obj) throws IllegalArgumentException, IllegalAccessException{
		TableInfo tableInfo = DB.getTableInfo(obj.getClass());
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}
		
		if(tableInfo.getPrimaryKeyColumns().length == 0){
			throw new ShuChaoWenRuntimeException(ClassUtils.getCGLIBRealClassName(obj.getClass()) + " not found primary key");
		}
		
		StringBuilder sb = new StringBuilder(64);
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			if(i > 0){
				sb.append(OBJECT_KEY_COCAT);
			}
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(obj));
		}
		return sb.toString();
	}
	
	public static String getObjectPrimaryKey(Class<?> tableClass, Object ...params){
		TableInfo tableInfo = DB.getTableInfo(tableClass);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}
		
		if(tableInfo.getPrimaryKeyColumns().length == 0){
			throw new ShuChaoWenRuntimeException(ClassUtils.getCGLIBRealClassName(tableClass) + " not found primary key");
		}
		
		StringBuilder sb = new StringBuilder(64);
		for(int i=0; i<params.length; i++){
			if(i > 0){
				sb.append(OBJECT_KEY_COCAT);
			}
			sb.append(params[i]);
		}
		return sb.toString();
	}
	
	public static String getObjectPrimaryKey(Class<?> tableClass, PrimaryKeyParameter params){
		return getObjectPrimaryKey(tableClass, params.getParams());
	}
	
	private static String getPrimaryKey(Object ...params){
		StringBuilder sb = new StringBuilder(64);
		for(Object v : params){
			sb.append(OBJECT_KEY_COCAT);
			sb.append(v);
		}
		return sb.toString();
	}
	
	public static String getObjectKey(Class<?> tableClass, PrimaryKeyParameter params){
		return getObjectKey(tableClass, params.getParams());
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
		
		return ClassUtils.getCGLIBRealClassName(tableClass) + getPrimaryKey(params);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T decode(Class<T> type, byte[] data){
		T t = BeanProxyMethodInterceptor.newInstance(type);
		Schema schema = RuntimeSchema.getSchema(type);
		ProtostuffIOUtil.mergeFrom(data, t, schema);
		((BeanProxy) t).startListen();
		return t;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static byte[] encode(Object value){
		Schema schema = RuntimeSchema.getSchema(value.getClass());
		return ProtobufIOUtil.toByteArray(value, schema,
				LinkedBuffer.allocate(512));
	}
}
