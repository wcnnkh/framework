package scw.data.redis.serialize;

import scw.core.utils.IOUtils;

public class JavaObjectRedisSerialize implements RedisSerialize {

	public byte[] serialize(String key, Object data) {
		if(data == null){
			return null;
		}
		
		return IOUtils.javaObjectToByte(data);
	}

	public <T> T deserialize(String key, byte[] data) {
		if(data == null){
			return null;
		}
		
		return IOUtils.byteToJavaObject(data);
	}

}
