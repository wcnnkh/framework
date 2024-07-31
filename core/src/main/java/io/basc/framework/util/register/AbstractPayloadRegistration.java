package io.basc.framework.util.register;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.concurrent.limit.Limiter;
import io.basc.framework.util.element.Elements;
import lombok.Getter;
import lombok.NonNull;

@Getter
public abstract class AbstractPayloadRegistration<T, R extends Registration> extends CombinableRegistration<R>
		implements PayloadRegistration<T> {

	public AbstractPayloadRegistration(@NonNull Limiter limiter) {
		this(limiter, Elements.empty());
	}

	public AbstractPayloadRegistration(@NonNull Limiter limiter, @NonNull Elements<R> registrations) {
		super(limiter, registrations);
	}

	protected AbstractPayloadRegistration(CombinableRegistration<R> context) {
		super(context);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PayloadRegistration) {
			PayloadRegistration<?> other = (PayloadRegistration<?>) obj;
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
