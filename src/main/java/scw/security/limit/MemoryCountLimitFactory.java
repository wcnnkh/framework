package scw.security.limit;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用内存来实现一段时间内的访问次数限制
 * 
 * @author shuchaowen
 *
 */
public final class MemoryCountLimitFactory implements CountLimitFactory {
	private final ConcurrentHashMap<String, MemoryCountLimit> map = new ConcurrentHashMap<String, MemoryCountLimit>();

	public CountLimit getCountLimit(CountLimitConfig countLimitConfig) {
		MemoryCountLimit countLimit = new MemoryCountLimit();
		MemoryCountLimit old = map.putIfAbsent(countLimitConfig.getName(), countLimit);
		if (old != null) {
			countLimit = old;
		}

		countLimit.setConfig(countLimitConfig);
		return countLimit;
	}

}
