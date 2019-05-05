package scw.data.redis.serialize;

import java.nio.charset.Charset;

public class StringRedisSerialize implements RedisSerialize {
	private final Charset charset;

	public StringRedisSerialize(Charset charset) {
		this.charset = charset;
	}

	public byte[] serialize(String key, Object data) {
		if (data == null) {
			return null;
		}

		return data.toString().getBytes(charset);
	}

	@SuppressWarnings("unchecked")
	public <T> T deserialize(String key, byte[] data) {
		if (data == null) {
			return null;
		}

		return (T) new String(data, charset);
	}

	public Charset getCharset() {
		return charset;
	}
}
