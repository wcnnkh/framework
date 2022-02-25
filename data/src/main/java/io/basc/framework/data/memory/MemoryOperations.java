package io.basc.framework.data.memory;

import java.util.concurrent.TimeUnit;

import io.basc.framework.data.CasDataOperations;
import io.basc.framework.data.TemporaryCounter;
import io.basc.framework.data.cas.CAS;

public final class MemoryOperations implements CasDataOperations, TemporaryCounter {
	private final MemoryDataManager memoryDataManager;

	public MemoryOperations() {
		this(new MemoryDataManager());
	}

	public MemoryOperations(MemoryDataManager memoryDataManager) {
		this.memoryDataManager = memoryDataManager;
	}

	@Override
	public Object get(String key) {
		MemoryData memoryData = memoryDataManager.getMemoryCache(key);
		if (memoryData == null) {
			return null;
		}

		CAS<Object> cas = memoryData.get();
		return cas == null ? null : cas.getValue();
	}

	@Override
	public Object getAndTouch(String key, long exp, TimeUnit expUnit) {
		MemoryData memoryData = memoryDataManager.getMemoryCache(key);
		if (memoryData == null) {
			return null;
		}

		memoryData.setExpire(expUnit.toMillis(exp));
		memoryData.touch();
		CAS<Object> cas = memoryData.get();
		return cas == null ? null : cas.getValue();
	}

	@Override
	public void set(String key, Object value, long exp, TimeUnit expUnit) {
		MemoryData memoryData = memoryDataManager.createDefaultMemoryCache(key);
		memoryData.set(value);
		memoryData.setExpire(expUnit.toMillis(exp));
	}

	@Override
	public boolean setIfAbsent(String key, Object value, long exp, TimeUnit expUnit) {
		MemoryData memoryData = memoryDataManager.createDefaultMemoryCache(key);
		if (!memoryData.setIfAbsent(value)) {
			return false;
		}
		memoryData.setExpire(expUnit.toMillis(exp));
		return true;
	}

	@Override
	public boolean touch(String key, long exp, TimeUnit expUnit) {
		MemoryData memoryData = memoryDataManager.getMemoryCache(key);
		if (memoryData == null) {
			return false;
		}

		memoryData.setExpire(expUnit.toMillis(exp));
		return true;
	}

	@Override
	public boolean exists(String key) {
		return memoryDataManager.getMemoryCache(key) != null;
	}

	@Override
	public long incr(String key, long delta, long initialValue, long exp, TimeUnit expUnit) {
		MemoryData memoryData = memoryDataManager.createCounterMemoryCache(key);
		long v = memoryData.incr(delta, initialValue);
		memoryData.setExpire(expUnit.toMillis(exp));
		return v;
	}

	@Override
	public long decr(String key, long delta, long initialValue, long exp, TimeUnit expUnit) {
		MemoryData memoryData = memoryDataManager.createCounterMemoryCache(key);
		long v = memoryData.decr(-delta, initialValue);
		memoryData.setExpire(expUnit.toMillis(exp));
		return v;
	}

	public boolean delete(String key) {
		return memoryDataManager.delete(key);
	}

	@Override
	public boolean cas(String key, Object value, long cas, long exp, TimeUnit expUnit) {
		MemoryData memoryData = memoryDataManager.createDefaultMemoryCache(key);
		if (memoryData.set(new CAS<Object>(cas, value))) {
			memoryData.setExpire(expUnit.toMillis(exp));
			return true;
		}
		return false;
	}

	@Override
	public boolean delete(String key, long cas) {
		return memoryDataManager.delete(key, cas);
	}

	@Override
	public CAS<Object> gets(String key) {
		MemoryData memoryData = memoryDataManager.getMemoryCache(key);
		if (memoryData == null) {
			return null;
		}
		return memoryData.get();
	}

	@Override
	public Object getAndTouch(String key) {
		MemoryData memoryData = memoryDataManager.getMemoryCache(key);
		if (memoryData == null) {
			return null;
		}

		memoryData.touch();
		CAS<Object> cas = memoryData.get();
		return cas == null ? null : cas.getValue();
	}

	@Override
	public boolean touch(String key) {
		MemoryData memoryData = memoryDataManager.getMemoryCache(key);
		if (memoryData == null) {
			return false;
		}

		memoryData.touch();
		return true;
	}

	@Override
	public boolean expire(String key, long exp, TimeUnit expUnit) {
		MemoryData memoryData = memoryDataManager.getMemoryCache(key);
		if (memoryData == null) {
			return false;
		}

		memoryData.setExpire(expUnit.toMillis(exp));
		return true;
	}

	@Override
	public boolean setIfPresent(String key, Object value, long exp, TimeUnit expUnit) {
		MemoryData memoryData = memoryDataManager.createDefaultMemoryCache(key);
		if (!memoryData.setIfPresent(value)) {
			return false;
		}
		memoryData.setExpire(expUnit.toMillis(exp));
		return true;
	}
}
