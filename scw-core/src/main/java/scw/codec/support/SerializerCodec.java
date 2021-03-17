package scw.codec.support;

import scw.codec.DecodeException;
import scw.codec.EncodeException;
import scw.io.JavaSerializer;
import scw.io.NoTypeSpecifiedSerializer;
import scw.io.SerializerUtils;


public class SerializerCodec<T> extends AbstractToByteCodec<T>{
	public static final SerializerCodec<Object> DEFAULT = new SerializerCodec<Object>(SerializerUtils.DEFAULT_SERIALIZER);
	public static final SerializerCodec<Object> JAVA = new SerializerCodec<Object>(JavaSerializer.INSTANCE);
	
	private final NoTypeSpecifiedSerializer serializer;
	
	public SerializerCodec(NoTypeSpecifiedSerializer serializer){
		this.serializer = serializer;
	}
	
	public byte[] encode(T source) throws EncodeException {
		return serializer.serialize(source);
	}

	public T decode(byte[] source) throws DecodeException {
		try {
			return serializer.deserialize(source);
		} catch (ClassNotFoundException e) {
			throw new DecodeException(e);
		}
	}

}
