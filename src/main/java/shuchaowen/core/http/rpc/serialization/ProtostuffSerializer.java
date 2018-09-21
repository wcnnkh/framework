package shuchaowen.core.http.rpc.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class ProtostuffSerializer implements Serializer{
	public <T> T decode(InputStream in, Class<T> type) throws IOException {
		Schema<T> schema = RuntimeSchema.getSchema(type);
		T message = schema.newMessage();
		ProtostuffIOUtil.mergeFrom(in, message, schema);
		return message;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void encode(OutputStream out, Object data) throws IOException {
		Schema schema = RuntimeSchema.getSchema(data.getClass());
		ProtobufIOUtil.writeTo(out, data, schema, LinkedBuffer.allocate(512));
	}
	
}
