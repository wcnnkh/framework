package io.basc.framework.register;

import io.basc.framework.limit.DisposableLimiter;
import io.basc.framework.limit.Limiter;
import io.basc.framework.limit.NoOpLimiter;
import io.basc.framework.util.ObjectUtils;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class PayloadRegistration<T> extends LimitedRegistration {
	private final T payload;

	public PayloadRegistration(T payload) {
		this(new DisposableLimiter(), Registration.EMPTY, payload);
	}

	public PayloadRegistration(Registration registration, T payload) {
		this(new NoOpLimiter(), registration, payload);
	}

	public PayloadRegistration(@NonNull Limiter limiter, Registration registration, T payload) {
		super(limiter, registration);
		this.payload = payload;
	}

	protected PayloadRegistration(@NonNull PayloadRegistration<T> payloadRegistration) {
		this(payloadRegistration, payloadRegistration.payload);
	}

	private PayloadRegistration(@NonNull LimitedRegistration context, T payload) {
		super(context);
		this.payload = payload;
	}

	@Override
	public PayloadRegistration<T> and(Registration registration) {
		return new PayloadRegistration<>(super.and(registration), this.payload);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PayloadRegistration) {
			PayloadRegistration<?> other = (PayloadRegistration<?>) obj;
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
}
