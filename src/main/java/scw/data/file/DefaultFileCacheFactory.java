package scw.data.file;

import java.io.File;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Constants;
import scw.core.Destroy;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XUtils;
import scw.data.AutoRefreshCache;
import scw.data.Cache;
import scw.data.CacheFactory;
import scw.io.SerializerUtils;
import scw.io.serializer.NoTypeSpecifiedSerializer;

public final class DefaultFileCacheFactory implements CacheFactory<AutoRefreshCache>, Destroy {
	private ConcurrentHashMap<Integer, AutoRefreshCache> cacheMap = new ConcurrentHashMap<Integer, AutoRefreshCache>();
	private final NoTypeSpecifiedSerializer serializer;
	private final String charsetName;
	private final String cacheDirectory;

	public DefaultFileCacheFactory() {
		this(SystemPropertyUtils.getTempDirectoryPath() + File.separator + "file_cache_factory");
	}

	public DefaultFileCacheFactory(String cacheDirectory) {
		this(SerializerUtils.DEFAULT_SERIALIZER, Constants.DEFAULT_CHARSET_NAME, cacheDirectory);
	}

	public DefaultFileCacheFactory(NoTypeSpecifiedSerializer serializer, String charsetName, String cacheDirectory) {
		this.serializer = serializer;
		this.charsetName = charsetName;
		this.cacheDirectory = cacheDirectory;
	}

	public Cache getCache(String key, int exp) {
		AutoRefreshCache create = new AutoRefreshFileCache(exp, serializer, charsetName, cacheDirectory + File.separator + exp);
		AutoRefreshCache cache = cacheMap.putIfAbsent(exp, create);
		if (cache == null) {
			XUtils.init(cache);
			cache = create;
		}
		return cache;
	}

	public void destroy() {
		for (Entry<Integer, AutoRefreshCache> entry : cacheMap.entrySet()) {
			XUtils.destroy(entry.getValue());
		}
	}
}
