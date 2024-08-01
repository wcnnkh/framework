package io.basc.framework.util.register;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.concurrent.limit.DisposableLimiter;
import io.basc.framework.util.concurrent.limit.Limiter;
import io.basc.framework.util.element.Elements;
import lombok.NonNull;

public class ServiceRegistration<S> extends AbstractPayloadRegistration<S> {
	private final S payload;

	public ServiceRegistration(S payload) {
		// 一般来说都是一次性的
		this(new DisposableLimiter(), payload);
	}

	public ServiceRegistration(@NonNull Limiter limiter, S payload) {
		super(limiter);
		this.payload = payload;
	}

	protected ServiceRegistration(ServiceRegistration<S> serviceRegistration) {
		this(serviceRegistration, serviceRegistration.payload);
	}

	private ServiceRegistration(CombinableRegistration<Registration> context, S payload) {
		super(context);
		this.payload = payload;
	}

	@Override
	public S getPayload() {
		return payload;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ServiceRegistration) {
			ServiceRegistration<?> other = (ServiceRegistration<?>) obj;
			return ObjectUtils.equals(this.payload, other.payload);
		}
		// 一般做为包装器使用，所以要比较一下外部数据
		return ObjectUtils.equals(this.payload, obj);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.payload);
	}

	@Override
	public String toString() {
		return ObjectUtils.toString(this.payload);
	}

	@Override
	public ServiceRegistration<S> and(@NonNull Registration registration) {
		return new ServiceRegistration<>(super.and(registration), this.payload);
	}

	@Override
	public ServiceRegistration<S> andAll(@NonNull Elements<? extends Registration> registrations) {
		return new ServiceRegistration<>(super.andAll(registrations), this.payload);
	}
}