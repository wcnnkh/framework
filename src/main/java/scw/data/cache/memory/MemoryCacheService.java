package scw.data.cache.memory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Destroy;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XTime;
import scw.data.cache.CacheService;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

@SuppressWarnings("unchecked")
public final class MemoryCacheService implements CacheService, Destroy {
	private static final long DEFAULT_CLEAR_PERIOD = StringUtils.parseLong(
			SystemPropertyUtils
					.getProperty("memory.temporary.cache.clear.period"),
			XTime.ONE_MINUTE);
	private static Logger logger = LoggerFactory
			.getLogger(MemoryCacheService.class);
	private Timer timer = new Timer(getClass().getSimpleName());
	private final ConcurrentHashMap<String, MemoryCache> cacheMap = new ConcurrentHashMap<String, MemoryCache>();

	public MemoryCacheService() {
		this.timer.schedule(new ClearExpireKeyTask(), DEFAULT_CLEAR_PERIOD,
				DEFAULT_CLEAR_PERIOD);
	}

	public <T> T get(String key) {
		MemoryCache memoryCache = getMemoryCache(key);
		if (memoryCache == null) {
			return null;
		}
		return (T) memoryCache.get();
	}

	private MemoryCache getMemoryCache(String key) {
		MemoryCache memoryCache = cacheMap.get(key);
		if (memoryCache == null) {
			return null;
		}

		if (memoryCache.isExpire(System.currentTimeMillis())) {
			return null;
		}

		return memoryCache;
	}

	public <T> T getAndTouch(String key, int newExp) {
		MemoryCache memoryCache = getMemoryCache(key);
		if (memoryCache == null) {
			return null;
		}

		memoryCache.setExpire(newExp);
		memoryCache.touch();
		return (T) memoryCache.get();
	}

	public boolean set(String key, Object value) {
		MemoryCache memoryCache = new DefaultMemoryCache(value);
		MemoryCache old = cacheMap.putIfAbsent(key, memoryCache);
		if (old != null) {
			memoryCache = old;
		}

		memoryCache.set(value);
		return true;
	}

	public boolean set(String key, int exp, Object value) {
		MemoryCache memoryCache = new DefaultMemoryCache(value);
		MemoryCache old = cacheMap.putIfAbsent(key, memoryCache);
		if (old != null) {
			memoryCache = old;
		}

		memoryCache.set(value);
		memoryCache.setExpire(exp);
		return true;
	}

	public boolean add(String key, Object value) {
		MemoryCache memoryCache = new DefaultMemoryCache(value);
		MemoryCache old = cacheMap.putIfAbsent(key, memoryCache);
		if (old != null) {
			memoryCache = old;
		}

		return memoryCache.setIfAbsent(value);
	}

	public boolean add(String key, int exp, Object value) {
		MemoryCache memoryCache = new DefaultMemoryCache(value);
		MemoryCache old = cacheMap.putIfAbsent(key, memoryCache);
		if (old != null) {
			memoryCache = old;
		}

		if (!memoryCache.setIfAbsent(value)) {
			return false;
		}

		memoryCache.setExpire(exp);
		return true;
	}

	public boolean touch(String key, int exp) {
		MemoryCache memoryCache = getMemoryCache(key);
		if (memoryCache == null) {
			return false;
		}

		memoryCache.setExpire(exp);
		return true;
	}

	public <T> Map<String, T> get(Collection<String> keyCollections) {
		if (CollectionUtils.isEmpty(keyCollections)) {
			return Collections.EMPTY_MAP;
		}

		Map<String, T> map = new HashMap<String, T>(keyCollections.size());
		for (String key : keyCollections) {
			T value = get(key);
			if (value == null) {
				continue;
			}

			map.put(key, value);
		}
		return map;
	}

	public boolean delete(String key) {
		return cacheMap.remove(key) != null;
	}

	public boolean isExist(String key) {
		return getMemoryCache(key) != null;
	}

	public long incr(String key, long delta) {
		return incr(key, delta, 0, 0);
	}

	public long incr(String key, long delta, long initialValue) {
		return incr(key, delta, initialValue, 0);
	}

	public long incr(String key, long delta, long initialValue, int exp) {
		MemoryCache memoryCache = new CounterMemoryCache();
		MemoryCache old = cacheMap.putIfAbsent(key, memoryCache);
		if (old != null) {
			memoryCache = old;
		}

		long v = memoryCache.incr(delta, initialValue);
		memoryCache.setExpire(exp);
		return v;
	}

	public long decr(String key, long delta) {
		return decr(key, delta, 0, 0);
	}

	public long decr(String key, long delta, long initialValue) {
		return decr(key, delta, initialValue, 0);
	}

	public long decr(String key, long delta, long initialValue, int exp) {
		MemoryCache memoryCache = new CounterMemoryCache();
		MemoryCache old = cacheMap.putIfAbsent(key, memoryCache);
		if (old != null) {
			memoryCache = old;
		}

		long v = memoryCache.decr(-delta, initialValue);
		memoryCache.setExpire(exp);
		return v;
	}

	public void destroy() {
		timer.cancel();
	}

	private final class ClearExpireKeyTask extends TimerTask {

		@Override
		public void run() {
			try {
				long currentTime = scheduledExecutionTime();
				Iterator<Entry<String, MemoryCache>> iterator = cacheMap
						.entrySet().iterator();
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
