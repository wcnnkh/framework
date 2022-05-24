package io.basc.framework.data.file;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.basc.framework.io.Serializer;
import io.basc.framework.io.SerializerUtils;
import io.basc.framework.util.stream.Processor;

public class AutoRefreshDirectoryStorage extends DiskStorage {
	private final Processor<String, ?, RuntimeException> converter;

	protected AutoRefreshDirectoryStorage(long period, TimeUnit periodUnit, Processor<String, ?, RuntimeException> converter) {
		super(period, periodUnit);
		this.converter = converter;
	}

	public AutoRefreshDirectoryStorage(long period, TimeUnit periodUnit, String cacheDirectory,
			Processor<String, ?, RuntimeException> converter) {
		this(period, periodUnit, SerializerUtils.getSerializer(), cacheDirectory, converter);
	}

	public AutoRefreshDirectoryStorage(long period, TimeUnit periodUnit, Serializer serializer, String cacheDirectory,
			Processor<String, ?, RuntimeException> converter) {
		super(period, periodUnit, serializer, cacheDirectory);
		this.converter = converter;
	}

	@Override
	protected void expireExecute(File file, long currentTimeMillis) {
		String key = getKey(file);
		refresh(key);
	}

	private Object refresh(String key) {
		Object value = converter.process(key);
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
