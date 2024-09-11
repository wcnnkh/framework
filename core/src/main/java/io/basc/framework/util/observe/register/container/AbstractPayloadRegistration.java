package io.basc.framework.util.observe.register.container;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.concurrent.limit.DisposableLimiter;
import io.basc.framework.util.observe.register.LimitableRegistration;
import io.basc.framework.util.observe.register.PayloadRegistration;

public abstract class AbstractPayloadRegistration<S> extends LimitableRegistration implements PayloadRegistration<S> {

	public AbstractPayloadRegistration() {
		super(new DisposableLimiter());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractPayloadRegistration) {
			AbstractPayloadRegistration<?> other = (AbstractPayloadRegistration<?>) obj;
			return ObjectUtils.equals(this.getPayload(), other.getPayload());
		}
		// 一般做为包装器使用，所以要比较一下外部数据
		return ObjectUtils.equals(this.getPayload(), obj);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.getPayload());
	}

	@Override
	public String toString() {
		return ObjectUtils.toString(this.getPayload());
	}
}
