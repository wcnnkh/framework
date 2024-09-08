package io.basc.framework.util.observe.event;

import io.basc.framework.util.observe.Observable;
import io.basc.framework.util.observe.Publisher;

public interface Exchange<T> extends Observable<T>, Publisher<T> {

	/**
	 * 批处理
	 * 
	 * @return 返回一个批处理的视图
	 */
	default BatchExchange<T> batch() {
		return new FakeBatchExchange<>(this);
	}
}
