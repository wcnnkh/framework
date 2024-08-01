package io.basc.framework.util.register;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.concurrent.limit.Limiter;
import io.basc.framework.util.element.Elements;
import lombok.NonNull;

public abstract class AbstractPayloadRegistration<T> extends CombinableRegistration<Registration>
		implements PayloadRegistration<T> {

	public AbstractPayloadRegistration(@NonNull Limiter limiter) {
		super(limiter, Elements.empty());
	}

	protected AbstractPayloadRegistration(CombinableRegistration<Registration> combinableRegistration) {
		super(combinableRegistration);
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
