package scw.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.codec.Codec;
import scw.codec.support.SerializerCodec;

/**
 * 序列化与反序列化
 * 
 * @author shuchaowen
 *
 */
public interface Serializer {
	void serialize(OutputStream out, Object data) throws IOException;

	default byte[] serialize(Object data) throws SerializerException {
		UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream();
		try {
			serialize(out, data);
			return out.toByteArray();
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			out.close();
		}
	}

	<T> T deserialize(InputStream input) throws IOException, ClassNotFoundException;

	default <T> T deserialize(byte[] data) throws ClassNotFoundException, SerializerException {
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(data);
		try {
			return deserialize(input);
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			input.close();
		}
	}
	
	default <D> Codec<D, byte[]> toCodec(){
		return new SerializerCodec<D>(this);
	}
}
