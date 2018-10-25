package shuchaowen.core.db.storage;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import shuchaowen.core.beans.BeanListen;
import shuchaowen.core.db.proxy.BeanProxyUtils;

public class CacheUtils {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T decode(Class<T> type, byte[] data) {
		T t = BeanProxyUtils.newInstance(type);
		Schema schema = RuntimeSchema.getSchema(type);
		ProtostuffIOUtil.mergeFrom(data, t, schema);
		((BeanListen) t).start_field_listen();
		return t;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static byte[] encode(Object value) {
		Schema schema = RuntimeSchema.getSchema(value.getClass());
		return ProtobufIOUtil.toByteArray(value, schema, LinkedBuffer.allocate(512));
	}
}
