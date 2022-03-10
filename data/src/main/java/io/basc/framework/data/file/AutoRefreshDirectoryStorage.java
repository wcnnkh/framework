package io.basc.framework.data.file;

import io.basc.framework.convert.Converter;
import io.basc.framework.io.Serializer;
import io.basc.framework.io.SerializerUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class AutoRefreshDirectoryStorage extends DiskStorage {
	private final Converter<String, ?> converter;

	protected AutoRefreshDirectoryStorage(long period, TimeUnit periodUnit, Converter<String, ?> converter) {
		super(period, periodUnit);
		this.converter = converter;
	}

	public AutoRefreshDirectoryStorage(long period, TimeUnit periodUnit, String cacheDirectory,
			Converter<String, ?> converter) {
		this(period, periodUnit, SerializerUtils.getSerializer(), cacheDirectory, converter);
	}

	public AutoRefreshDirectoryStorage(long period, TimeUnit periodUnit, Serializer serializer, String cacheDirectory,
			Converter<String, ?> converter) {
		super(period, periodUnit, serializer, cacheDirectory);
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
