package scw.data.file;

import java.io.File;

import scw.core.Converter;
import scw.io.serializer.NoTypeSpecifiedSerializer;

public class AutoRefreshFileCache extends FileCache {
	private final Converter<String, Object> converter;

	public AutoRefreshFileCache(int period, NoTypeSpecifiedSerializer serializer, String charsetName,
			String cacheDirectory, Converter<String, Object> converter) {
		super(period, serializer, charsetName, cacheDirectory);
		this.converter = converter;
	}
	
	@Override
	protected void expireExecute(File file, long currentTimeMillis) {
		String key = file.getName();
		key = decodeKey(key);
		refresh(key);
	}

	private Object refresh(String key) {
		Object value = null;
		try {
			value = converter.convert(key);
			if (value != null) {
				set(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T getNotFound(String key) {
		return (T) refresh(key);
	}

	@Override
	protected File getNotExpireFile(String key) {
		File file = getFile(key);
		if (file.exists()) {
			return file;
		}
		return null;
	}
}
