package scw.data.file;

import java.io.File;

import scw.core.Constants;
import scw.core.Converter;
import scw.io.serialzer.NoTypeSpecifiedSerializer;
import scw.io.serialzer.SerializerUtils;

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
		String key = getKey(file);
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

	@Override
	protected Object getNotFound(String key) {
		return refresh(key);
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
