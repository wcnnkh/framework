package scw.db.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import scw.sql.orm.ORMUtils;

public abstract class CacheUtils {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T decode(Class<T> type, byte[] data) {
		T t = ORMUtils.getTableInfo(type).newInstance();
		Schema schema = RuntimeSchema.getSchema(type);
		ProtostuffIOUtil.mergeFrom(data, t, schema);
		try {
			return ORMUtils.restartFieldLinsten(t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static byte[] encode(Object value) {
		Schema schema = RuntimeSchema.getSchema(value.getClass());
		return ProtobufIOUtil.toByteArray(value, schema, LinkedBuffer.allocate(512));
	}
}
