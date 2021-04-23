package scw.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import scw.convert.TypeDescriptor;
import scw.io.CrossLanguageSerializer;
import scw.lang.NamedThreadLocal;

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
