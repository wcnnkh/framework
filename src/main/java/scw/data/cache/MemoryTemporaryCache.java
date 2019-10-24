package scw.data.cache;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import scw.core.Destroy;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XTime;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

/**
 * 内存实现
 * 
 * @author shuchaowen
 *
 */
public final class MemoryTemporaryCache implements TemporaryCache, Destroy {
	private static final long DEFAULT_CLEAR_PERIOD = StringUtils.parseLong(
			SystemPropertyUtils
					.getProperty("memory.temporary.cache.clear.period"),
			XTime.ONE_MINUTE);
	private static Logger logger = LoggerFactory
			.getLogger(MemoryTemporaryCache.class);
	private Timer timer = new Timer(getClass().getSimpleName());
	private volatile ConcurrentHashMap<String, CacheContent> map = new ConcurrentHashMap<String, CacheContent>();

	public MemoryTemporaryCache() {
		timer.schedule(new ClearExpireKeyTask(), DEFAULT_CLEAR_PERIOD,
				DEFAULT_CLEAR_PERIOD);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		CacheContent content = map.get(key);
		if (content == null) {
			return null;
		}

		if (content.isExpire(System.currentTimeMillis())) {
			return null;
		}

		return (T) content.getValue();
	}

	@SuppressWarnings("unchecked")
	public <T> T getAndTouch(String key, int exp) {
		CacheContent content = map.get(key);
		if (content == null) {
			return null;
		}

		if (content.isExpire(System.currentTimeMillis())) {
			return null;
		}

		content.touch(exp);
		return (T) content.getValue();
	}

	public void touch(String key, int exp) {
		CacheContent content = map.get(key);
		if (content == null) {
			return;
		}

		content.touch(exp);
	}

	public void delete(String key) {
		map.remove(key);
	}

	public void set(String key, int exp, Object value) {
		CacheContent content = new CacheContent(value, exp);
		CacheContent oldContent = map.putIfAbsent(key, content);
		if (oldContent != null) {
			oldContent.setValue(value);
			oldContent.touch(exp);
		}
	}

	private final class ClearExpireKeyTask extends TimerTask {

		@Override
		public void run() {
			try {
				long currentTime = scheduledExecutionTime();
				Iterator<Entry<String, CacheContent>> iterator = map.entrySet()
						.iterator();
				while (iterator.hasNext()) {
					Entry<String, CacheContent> entry = iterator.next();
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

	private final class CacheContent {
		private Object value;
		private final AtomicLong lastTouch;
		private int exp;

		public CacheContent(Object value, int exp) {
			this.lastTouch = new AtomicLong(System.currentTimeMillis());
			this.value = value;
			this.exp = exp;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public void touch(int exp) {
			this.exp = exp;
			this.lastTouch.set(System.currentTimeMillis());
		}

		public boolean isExpire(long currentTimeMillis) {
			if (exp <= 0) {
				return false;
			}

			return currentTimeMillis - lastTouch.get() > exp * 1000L;
		}
	}

	public void destroy() {
		timer.cancel();
	}
}
