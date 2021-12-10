package io.basc.framework.data.file;

import io.basc.framework.convert.Converter;
import io.basc.framework.io.Serializer;
import io.basc.framework.io.SerializerUtils;

import java.io.File;

public class AutoRefreshDirectoryStorage extends DirectoryStorage {
	private final Converter<String, ?> converter;

	/**
	 * @param period    单位:秒
	 * @param converter
	 */
	protected AutoRefreshDirectoryStorage(long period, Converter<String, ?> converter) {
		super(period);
		this.converter = converter;
	}

	/**
	 * @param period         单位:秒
	 * @param cacheDirectory
	 * @param converter
	 */
	public AutoRefreshDirectoryStorage(long period, String cacheDirectory, Converter<String, ?> converter) {
		this(period, SerializerUtils.getSerializer(), cacheDirectory, converter);
	}

	/**
	 * @param period         单位:秒
	 * @param serializer
	 * @param charsetName
	 * @param cacheDirectory
	 * @param converter
	 */
	public AutoRefreshDirectoryStorage(long period, Serializer serializer, String cacheDirectory,
			Converter<String, ?> converter) {
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
