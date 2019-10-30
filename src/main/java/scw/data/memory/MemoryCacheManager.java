package scw.data.memory;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Destroy;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XTime;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public final class MemoryCacheManager implements Destroy {
	private static final long DEFAULT_CLEAR_PERIOD = StringUtils
			.parseLong(SystemPropertyUtils.getProperty("memory.temporary.cache.clear.period"), XTime.ONE_MINUTE);
	private static Logger logger = LoggerFactory.getLogger(MemoryCacheManager.class);
	private Timer timer = new Timer(getClass().getSimpleName());
	private final ConcurrentHashMap<String, MemoryCache> cacheMap = new ConcurrentHashMap<String, MemoryCache>();

	public MemoryCacheManager() {
		this.timer.schedule(new ClearExpireKeyTask(), DEFAULT_CLEAR_PERIOD, DEFAULT_CLEAR_PERIOD);
	}

	public MemoryCache getMemoryCache(String key) {
		MemoryCache memoryCache = cacheMap.get(key);
		if (memoryCache == null) {
			return null;
		}

		if (memoryCache.isExpire(System.currentTimeMillis())) {
			return null;
		}

		return memoryCache;
	}

	public MemoryCache createDefaultMemoryCache(String key) {
		MemoryCache memoryCache = new DefaultMemoryCache();
		MemoryCache old = cacheMap.putIfAbsent(key, memoryCache);
		return old == null ? memoryCache : old;
	}

	public MemoryCache createCounterMemoryCache(String key) {
		MemoryCache memoryCache = new CounterMemoryCache();
		MemoryCache old = cacheMap.putIfAbsent(key, memoryCache);
		return old == null ? memoryCache : old;
	}

	public boolean delete(String key) {
		return cacheMap.remove(key) != null;
	}

	public boolean delete(String key, long cas) {
		MemoryCache memoryCache = getMemoryCache(key);
		if (memoryCache == null) {
			return false;
		}

		if (memoryCache.incrCasAndCompare(cas)) {
			return cacheMap.remove(key) != null;
		}
		return false;
	}

	public void destroy() {
		timer.cancel();
	}

	private final class ClearExpireKeyTask extends TimerTask {

		@Override
		public void run() {
			try {
				long currentTime = scheduledExecutionTime();
				Iterator<Entry<String, MemoryCache>> iterator = cacheMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, MemoryCache> entry = iterator.next();
					if (entry.getValue().isExpire(currentTime)) {
						logger.debug("Deleting expired key:{}", entry.getKey());
						iterator.remove();
					}
				}
			} catch (Exception e) {
				logger.error(e, "clear expired key error");
			}
		}
	}
}
