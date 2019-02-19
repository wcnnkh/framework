package scw.db.storage;

import java.util.HashMap;
import java.util.Map;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import scw.beans.BeanFieldListen;
import scw.db.storage.cache.Cache;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public class CacheUtils {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T decode(Class<T> type, byte[] data) {
		T t = ORMUtils.getTableInfo(type).newInstance();
		Schema schema = RuntimeSchema.getSchema(type);
		ProtostuffIOUtil.mergeFrom(data, t, schema);
		if(t instanceof BeanFieldListen){
			((BeanFieldListen) t).start_field_listen();
		}
		return t;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static byte[] encode(Object value) {
		Schema schema = RuntimeSchema.getSchema(value.getClass());
		return ProtobufIOUtil.toByteArray(value, schema, LinkedBuffer.allocate(512));
	}
	
	public static String getObjectKey(Object bean) throws IllegalArgumentException, IllegalAccessException{
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			sb.append(Cache.SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean));
		}
		return sb.toString();
	}
	
	public static Map<String, Object> getObjectProperties(Object bean) throws IllegalArgumentException, IllegalAccessException{
		Map<String, Object> map = new HashMap<String, Object>();
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		for (int i = 0; i < tableInfo.getColumns().length; i++) {
			map.put(tableInfo.getColumns()[i].getFieldInfo().getName(), tableInfo.getColumns()[i].getFieldInfo().forceGet(bean));
		}
		return map;
	}
}
