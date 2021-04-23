package scw.data.file;

import java.io.File;

import scw.convert.Converter;
import scw.io.Serializer;
import scw.io.SerializerUtils;

public class AutoRefreshDirectoryStorage extends DirectoryStorage {
	private final Converter<String, ?> converter;

	/**
	 * @param period 单位:秒
	 * @param converter
	 */
	protected AutoRefreshDirectoryStorage(int period, Converter<String, ?> converter) {
		super(period);
		this.converter = converter;
	}

	/**
	 * @param period 单位:秒
	 * @param cacheDirectory
	 * @param converter
	 */
	public AutoRefreshDirectoryStorage(int period, String cacheDirectory, Converter<String, ?> converter) {
		this(period, SerializerUtils.getSerializer(), cacheDirectory, converter);
	}

	/**
	 * @param period 单位:秒
	 * @param serializer
	 * @param charsetName
	 * @param cacheDirectory
	 * @param converter
	 */
	public AutoRefreshDirectoryStorage(int period, Serializer serializer,
			String cacheDirectory, Converter<String, ?> converter) {
		super(period, serializer, cacheDirectory);
		this.converter = converter;
	}

	@Override
	protected void expireExecute(File file, long currentTimeMillis) {
		String key = getKey(file);
		refresh(key);
	}

	private Object refresh(String key) {
		Object value = converter.convert(key);
		if (value != null) {
			set(key, value);
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
