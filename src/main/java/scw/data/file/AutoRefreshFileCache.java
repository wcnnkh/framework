package scw.data.file;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Callable;
import scw.data.AutoRefreshCache;
import scw.io.serializer.NoTypeSpecifiedSerializer;

public class AutoRefreshFileCache extends FileCache implements AutoRefreshCache {
	private ConcurrentHashMap<String, Callable<?>> loaderMap = new ConcurrentHashMap<String, Callable<?>>();

	public AutoRefreshFileCache(int period, NoTypeSpecifiedSerializer serializer, String charsetName,
			String cacheDirectory) {
		super(period, serializer, charsetName, cacheDirectory);
	}

	@Override
	protected void expireExecute(File file) {
		String key = file.getName();
		key = decodeKey(key);
		refresh(key);
	}

	private Object refresh(String key) {
		Callable<?> callable = loaderMap.get(key);
		if (callable != null) {
			Object v = callable.call();
			if (v != null) {
				set(key, v);
			}
			return v;
		}
		return null;
	}

	@Override
	protected File getNotExpireFile(String key) {
		File file = getFile(key);
		if (file.exists()) {
			return file;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, Callable<? extends T> loader) {
		Callable<?> callable = loaderMap.putIfAbsent(key, loader);
		if (callable == null) {// 不存在
			callable = loader;
		}

		Object value = get(key);
		if (value == null) {
			value = refresh(key);
		}
		return (T) value;
	}
}
