package scw.data.file;

import java.io.File;

import scw.core.Constants;
import scw.core.Converter;
import scw.core.utils.SystemPropertyUtils;
import scw.data.ExpiredCache;
import scw.serializer.NoTypeSpecifiedSerializer;
import scw.serializer.SerializerUtils;

public class AutoRefreshFileCache extends FileCache {
	private final Converter<String, ?> converter;

	protected AutoRefreshFileCache(int period, Converter<String, ?> converter) {
		super(period);
		this.converter = converter;
	}

	public AutoRefreshFileCache(int period, String cacheDirectory, Converter<String, ?> converter) {
		this(period, SerializerUtils.DEFAULT_SERIALIZER, Constants.DEFAULT_CHARSET_NAME, cacheDirectory, converter);
	}

	public AutoRefreshFileCache(int period, NoTypeSpecifiedSerializer serializer, String charsetName,
			String cacheDirectory, Converter<String, ?> converter) {
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

	public static ExpiredCache create(String cacheDirectorySuffix, int period, Converter<String, ?> converter) {
		return new AutoRefreshFileCache(period,
				SystemPropertyUtils.getTempDirectoryPath() + File.separator + cacheDirectorySuffix, converter);
	}
}
