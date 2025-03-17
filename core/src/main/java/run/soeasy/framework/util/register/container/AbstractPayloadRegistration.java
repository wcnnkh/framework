package run.soeasy.framework.util.register.container;

import run.soeasy.framework.util.ObjectUtils;
import run.soeasy.framework.util.register.AbstractLifecycleRegistration;
import run.soeasy.framework.util.register.PayloadRegistration;

public abstract class AbstractPayloadRegistration<S> extends AbstractLifecycleRegistration
		implements PayloadRegistration<S> {

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
