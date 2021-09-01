package io.basc.framework.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.CrossLanguageSerializer;
import io.basc.framework.lang.NamedThreadLocal;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class PrototufSerializer implements CrossLanguageSerializer {
	private static final ThreadLocal<LinkedBuffer> bufferLocal = new NamedThreadLocal<LinkedBuffer>(PrototufSerializer.class.getName()) {
		protected LinkedBuffer initialValue() {
			return LinkedBuffer.allocate(1024);
		};
	};

	public static LinkedBuffer getLinkedBuffer() {
		return bufferLocal.get().clear();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void serialize(OutputStream out, TypeDescriptor type, Object data) throws IOException {
		Schema<Object> schema = (Schema<Object>) RuntimeSchema.getSchema(type.getType());
		ProtostuffIOUtil.writeTo(out, data, schema, getLinkedBuffer());
		out.flush();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream input, TypeDescriptor type) throws IOException {
		Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(type.getType());
		T t = schema.newMessage();
		ProtostuffIOUtil.mergeFrom(input, t, schema, getLinkedBuffer());
		return t;
	}
}
