package run.soeasy.framework.jackson.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

/**
 * Map/对象序列化器：null → {}, 空Map → {}, 空对象 → 正常序列化（如 {"name":null}）
 */
public class EmptyObjectSerializer<T> extends StdScalarSerializer<T> {
	private static final long serialVersionUID = 1L;

	protected EmptyObjectSerializer(Class<T> t) {
		super(t);
	}

	@Override
	public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		if (value == null) {
			gen.writeStartObject(); // 输出 {}
			gen.writeEndObject();
		} else {
			// 非空时：Map按原逻辑（空Map→{}），POJO正常序列化
			gen.writeObject(value);
		}
	}
}