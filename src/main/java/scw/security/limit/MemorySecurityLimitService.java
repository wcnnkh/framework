package scw.security.limit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 使用内存实现指定时间内的使用次数限制
 * @author shuchaowen
 *
 */
public class MemorySecurityLimitService extends AbstractSecurityLimitService {
	private final ConcurrentHashMap<String, LimitContent> map = new ConcurrentHashMap<String, LimitContent>();

	public MemorySecurityLimitService(int maxLimit, int timeout) {
		super(maxLimit, timeout);
	}

	public boolean tryLimit(String name) {
		LimitContent content = new LimitContent();
		LimitContent oldContent = map.putIfAbsent(name, content);
		if (oldContent != null) {
			content = oldContent;
		}

		return content.incr();
	}

	public void reset(String name) {
		LimitContent content = map.get(name);
		if (content == null) {
			return;
		}

		content.reset();
	}

	private final class LimitContent {
		private final AtomicLong count;
		private final AtomicLong lastTimeout;

		public LimitContent() {
			count = new AtomicLong(0);
			this.lastTimeout = new AtomicLong(System.currentTimeMillis());
		}

		public boolean incr() {
			if (lastTimeout.get() - System.currentTimeMillis() >= getTimeout()) {
				count.set(1);
				lastTimeout.set(System.currentTimeMillis());
				return true;
			}

			if (count.get() > getMaxLimit()) {
				return false;
			}

			count.incrementAndGet();
			return true;
		}

		public void reset() {
			count.set(0);
			lastTimeout.set(System.currentTimeMillis());
		}
	}
}
