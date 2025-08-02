package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.exchange.Registed;
import run.soeasy.framework.core.exchange.Registration;

class PayloadRegisted<T> extends Registed implements PayloadRegistration<T> {
	/** 注册失败的默认实例（载荷为null） */
	public static final PayloadRegistration<?> FAILURE = new PayloadRegisted<>(true, null);
	/** 注册成功的默认实例（载荷为null） */
	public static final PayloadRegistration<?> SUCCESS = new PayloadRegisted<>(false, null);

	private static final long serialVersionUID = 1L;
	private final T payload;

	public PayloadRegisted(boolean cancelled, T payload) {
		super(cancelled);
		this.payload = payload;
	}

	@Override
	public PayloadRegistration<T> and(Registration registration) {
		return PayloadRegistration.super.and(registration);
	}

	@Override
	public T getPayload() {
		return payload;
	}
}