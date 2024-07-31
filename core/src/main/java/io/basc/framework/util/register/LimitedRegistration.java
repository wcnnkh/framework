package io.basc.framework.util.register;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.concurrent.limit.Limiter;
import lombok.NonNull;

public class LimitedRegistration extends AbstractRegistration {
	private final Registration registration;

	public LimitedRegistration(@NonNull Limiter limiter, Registration registration) {
		super(limiter);
		this.registration = registration == null ? Registration.EMPTY : registration;
	}

	protected LimitedRegistration(@NonNull LimitedRegistration limitedRegistration) {
		this(limitedRegistration, limitedRegistration.registration);
	}

	private LimitedRegistration(@NonNull AbstractRegistration abstractRegistration,
			@NonNull Registration registration) {
		super(abstractRegistration);
		this.registration = registration;
	}

	@Override
	public final boolean isInvalid() {
		return isInvalid(registration::isInvalid);
	}

	@Override
	public final void deregister() throws RegistrationException {
		deregister(registration::deregister);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LimitedRegistration) {
			LimitedRegistration other = (LimitedRegistration) obj;
			return ObjectUtils.equals(this.registration, other.registration);
		}
		// 一般做为包装器使用，所以要比较一下外部数据
		return ObjectUtils.equals(this.registration, obj);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(registration);
	}

	@Override
	public String toString() {
		return ObjectUtils.toString(registration);
	}
}
