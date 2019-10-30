package scw.data.memory;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Destroy;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public final class MemoryCacheManager implements Destroy {
	// 单位：秒
	private static final long DEFAULT_CLEAR_PERIOD = StringUtils
			.parseLong(SystemPropertyUtils.getProperty("memory.temporary.cache.clear.period"), 60);
	private static Logger logger = LoggerFactory.getLogger(MemoryCacheManager.class);

	private final ConcurrentHashMap<String, MemoryCache> cacheMap = new ConcurrentHashMap<String, MemoryCache>();
	private Timer timer;
	private TimerTask timerTask;

	public MemoryCacheManager() {
		this(DEFAULT_CLEAR_PERIOD);
	}

	/**
	 * @param clearPeriodSecond
	 *            单位：秒
	 */
	public MemoryCacheManager(long clearPeriodSecond) {
		if (clearPeriodSecond > 0) {
			this.timerTask = new ClearExpireKeyTask();
			timer = new Timer(getClass().getSimpleName());
			this.timer.schedule(timerTask, clearPeriodSecond * 1000L, clearPeriodSecond * 1000L);
		}
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
		if (timerTask != null) {
			timerTask.cancel();
		}

		if (timer != null) {
			timer.cancel();
		}
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
						iterator.remove();
						logger.debug("Deleting expired key:{}", entry.getKey());
					}
				}
			} catch (Exception e) {
				logger.error(e, "clear expired key error");
			}
		}
	}
}
