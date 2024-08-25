package io.basc.framework.util.register.container;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.concurrent.limit.DisposableLimiter;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.CombinableRegistration;
import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.Registration;

public abstract class AbstractPayloadRegistration<S> extends CombinableRegistration<Registration>
		implements PayloadRegistration<S> {

	public AbstractPayloadRegistration() {
		super(new DisposableLimiter(), Elements.empty());
	}

	protected AbstractPayloadRegistration(CombinableRegistration<Registration> context) {
		super(context);
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
