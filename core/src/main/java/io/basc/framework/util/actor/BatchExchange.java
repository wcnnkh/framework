package io.basc.framework.util.actor;

import io.basc.framework.util.Elements;

public interface BatchExchange<T> extends Exchange<Elements<T>> {
	/**
	 * 一个个处理
	 * 
	 * @return
	 */
	default Exchange<T> single() {
		return new FakeSingleExchange<>(this);
	}
}
