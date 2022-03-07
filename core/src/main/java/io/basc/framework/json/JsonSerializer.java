package io.basc.framework.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.support.CharsetCodec;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.CrossLanguageSerializer;
import io.basc.framework.io.IOUtils;

public class JsonSerializer implements CrossLanguageSerializer {
	public static final CrossLanguageSerializer INSTANCE = new JsonSerializer();

	private JSONSupport jsonSupport;
	private Codec<String, byte[]> codec = CharsetCodec.UTF_8;

	public JSONSupport getJsonSupport() {
		return jsonSupport == null ? JSONUtils.getJsonSupport() : jsonSupport;
	}

	public void setJsonSupport(JSONSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

	public Codec<String, byte[]> getCodec() {
		return codec;
	}

	public void setCodec(Codec<String, byte[]> codec) {
		this.codec = codec;
	}

	@Override
	public void serialize(Object source, TypeDescriptor sourceTypeDescriptor, OutputStream target) throws IOException {
		String content = getJsonSupport().toJSONString(source);
		target.write(codec.encode(content));
	}

	@Override
	public <T> T deserialize(InputStream input, TypeDescriptor type) throws IOException {
		byte[] data = IOUtils.toByteArray(input);
		String content = codec.decode(data);
		return getJsonSupport().parseObject(content, type.getResolvableType().getType());
	}

}
