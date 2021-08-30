package io.basc.framework.codec.support;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.io.JavaSerializer;
import io.basc.framework.io.Serializer;
import io.basc.framework.io.SerializerUtils;


public class SerializerCodec<T> implements BytesCodec<T>{
	public static final SerializerCodec<Object> DEFAULT = new SerializerCodec<Object>(SerializerUtils.getSerializer());
	public static final SerializerCodec<Object> JAVA = new SerializerCodec<Object>(JavaSerializer.INSTANCE);
	
	private final Serializer serializer;
	
	public SerializerCodec(Serializer serializer){
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
