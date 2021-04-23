package scw.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.codec.Codec;
import scw.codec.support.CharsetCodec;
import scw.convert.TypeDescriptor;
import scw.io.CrossLanguageSerializer;
import scw.io.IOUtils;

public class JsonSerializer implements CrossLanguageSerializer {
	public static final JsonSerializer INSTANCE = new JsonSerializer();
	
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
	public void serialize(OutputStream out, TypeDescriptor type, Object data) throws IOException {
		String content = jsonSupport.toJSONString(data);
		out.write(codec.encode(content));
	}

	@Override
	public <T> T deserialize(InputStream input, TypeDescriptor type) throws IOException {
		byte[] data = IOUtils.toByteArray(input);
		String content = codec.decode(data);
		return jsonSupport.parseObject(content, type.getResolvableType().getType());
	}

}
