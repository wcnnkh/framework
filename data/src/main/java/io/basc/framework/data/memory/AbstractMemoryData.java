package io.basc.framework.data.memory;

import io.basc.framework.io.SerializerUtils;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractMemoryData implements MemoryData {
	private volatile long lastTouch;
	private volatile long exp;
	protected AtomicLong cas = new AtomicLong();

	public AbstractMemoryData() {
		this.lastTouch = System.currentTimeMillis();
	}

	public void setExpire(long exp) {
		this.exp = exp;
	}

	public void touch() {
		this.lastTouch = System.currentTimeMillis();
	}

	public boolean isExpire() {
		return exp <= 0 ? false : (System.currentTimeMillis() - lastTouch) > exp;
	}

	@Override
	public long getRemainingSurvivalTime() {
		return exp <= 0 ? -1 : (exp - (System.currentTimeMillis() - lastTouch));
	}

	public boolean setIfAbsent(Object value) {
		if (cas.get() == 0) {// 第一次set数据
			set(value);
			return true;
		}

		if (!isExpire()) {
			return false;
		}

		set(value);
		return true;
	}

	@Override
	public boolean setIfPresent(Object value) {
		if (cas.get() == 0) {// 第一次set数据
			set(value);
			return true;
		}

		if (!isExpire()) {
			return false;
		}

		set(value);
		return true;
	}

	public boolean setIfAbsent(CAS<Object> value) {
		if (cas.get() == 0) {// 第一次set数据
			return set(value);
		}

		if (!isExpire()) {
			return false;
		}

		return set(value);
	}

	@SuppressWarnings("unchecked")
	public <T> CAS<T> get() {
		Object value = getValue();
		if (value == null) {
			return null;
		}

		return new CAS<T>(cas.get(), (T) SerializerUtils.clone(value));
	}

	public boolean incrCasAndCompare(long cas) {
		long c;
		long a;
		do {
			a = this.cas.get();
			c = isExpire() ? 0 : a;
		} while (!this.cas.compareAndSet(a, c + 1));
		if (c == 0) {
			touch();
		}
		return c == cas;
	}

	public boolean set(CAS<? extends Object> value) {
		if (incrCasAndCompare(value.getCas())) {
			return setValue(SerializerUtils.clone(value.getValue()));
		}
		return false;
	}

	public void set(Object value) {
		cas.incrementAndGet();
		setValue(SerializerUtils.clone(value));
	}

	protected abstract boolean setValue(Object value);

	protected abstract Object getValue();
}
