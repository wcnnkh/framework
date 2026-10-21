package run.soeasy.framework.jackson.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

/**
 * 数组序列化器：null → [], 空数组 → []
 */
public class EmptyArraySerializer<T> extends StdScalarSerializer<T> {
	private static final long serialVersionUID = 1L;

	public EmptyArraySerializer(Class<T> t) {
		super(t);
	}

	@Override
	public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		if (value == null) {
			gen.writeStartArray();
			gen.writeEndArray();
		} else {
			gen.writeObject(value); // 空数组→[]，非空数组正常序列化
		}
	}
}