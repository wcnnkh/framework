package scw.data.cache;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Destroy;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.timer.RunnableTimerTask;

/**
 * 内存实现
 * 
 * @author shuchaowen
 *
 */
public final class MemoryTemporaryCache implements TemporaryCache, Destroy {
	private static Logger logger = LoggerFactory.getLogger(MemoryTemporaryCache.class);
	private Timer timer = new Timer(getClass().getSimpleName());
	private volatile Map<String, TemporaryCacheContent> map = new ConcurrentHashMap<String, TemporaryCacheContent>();

	public MemoryTemporaryCache() {
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		TemporaryCacheContent content = map.get(key);
		return (T) (content == null ? null : content.getValue());
	}

	@SuppressWarnings("unchecked")
	public <T> T getAndTouch(String key, int exp) {
		TemporaryCacheContent content = map.get(key);
		if (content == null) {
			return null;
		}

		content.touch(exp);
		return (T) content.getValue();
	}

	public void touch(String key, int exp) {
		TemporaryCacheContent content = map.get(key);
		if (content == null) {
			return;
		}

		content.touch(exp);
	}

	public void delete(String key) {
		TemporaryCacheContent content = map.remove(key);
		if (content == null) {
			return;
		}

		content.cancel();
	}

	public void set(String key, int exp, Object value) {
		TemporaryCacheContent content = new TemporaryCacheContent(key, value);
		TemporaryCacheContent oldContent = map.putIfAbsent(key, content);
		if (oldContent != null) {
			oldContent.setValue(value);
			oldContent.touch(exp);
		} else {
			content.touch(exp);
		}
	}

	private final class TemporaryCacheContent implements Runnable {
		private final String key;
		private Object value;
		private volatile TimerTask timerTask;

		public TemporaryCacheContent(String key, Object value) {
			this.key = key;
			this.value = value;
		}

		public void run() {
			logger.info("Deleting expired key:{}", key);
			map.remove(key);
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public void cancel() {
			if (timerTask != null) {
				timerTask.cancel();
			}
		}

		public void touch(int exp) {
			cancel();
			if (exp > 0) {
				this.timerTask = new RunnableTimerTask(this);
				timer.schedule(timerTask, exp * 1000);
			}
		}
	}

	public void destroy() {
		timer.cancel();
	}
}
