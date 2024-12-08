package io.basc.framework.data.file;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.basc.framework.util.io.serializer.Serializer;
import io.basc.framework.util.io.serializer.SerializerUtils;

public class AutoRefreshDirectoryStorage extends DiskStorage {
	private final Function<String, ?> refresh;

	protected AutoRefreshDirectoryStorage(long period, TimeUnit periodUnit, Function<String, ?> refresh) {
		super(period, periodUnit);
		this.refresh = refresh;
	}

	public AutoRefreshDirectoryStorage(long period, TimeUnit periodUnit, String cacheDirectory,
			Function<String, ?> refresh) {
		this(period, periodUnit, SerializerUtils.getSerializer(), cacheDirectory, refresh);
	}

	public AutoRefreshDirectoryStorage(long period, TimeUnit periodUnit, Serializer serializer, String cacheDirectory,
			Function<String, ?> refresh) {
		super(period, periodUnit, serializer, cacheDirectory);
		this.refresh = refresh;
	}

	@Override
	protected void expireExecute(File file, long currentTimeMillis) {
		String key = getKey(file);
		refresh(key);
	}

	private Object refresh(String key) {
		Object value = refresh.apply(key);
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
