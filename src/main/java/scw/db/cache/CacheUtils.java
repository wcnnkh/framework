package scw.db.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public abstract class CacheUtils {

	public static String getObjectCacheKey(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getClz().getName());
		try {
			for (ColumnInfo columnInfo : tableInfo.getPrimaryKeyColumns()) {
				sb.append("\\").append(columnInfo.getFieldInfo().forceGet(bean)).append("\\");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public static String getByIdCacheKey(Class<?> type, Object... params) {
		return appendKey(type.getName(), params);
	}

	public static String appendKey(String key, Object... params) {
		StringBuilder sb = new StringBuilder();
		sb.append(key);
		for (int i = 0; i < params.length; i++) {
			sb.append("\\").append(params[i]).append("\\");
		}
		return sb.toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T decode(Class<T> type, byte[] data) {
		T t = ORMUtils.getTableInfo(type).newInstance();
		Schema schema = RuntimeSchema.getSchema(type);
		ProtostuffIOUtil.mergeFrom(data, t, schema);
		return ORMUtils.restartFieldLinsten(t);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static byte[] encode(Object value) {
		Schema schema = RuntimeSchema.getSchema(value.getClass());
		return ProtobufIOUtil.toByteArray(value, schema, LinkedBuffer.allocate(512));
	}
}
