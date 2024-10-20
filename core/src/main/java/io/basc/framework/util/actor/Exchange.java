package io.basc.framework.util.actor;

import io.basc.framework.util.Listenable;
import io.basc.framework.util.Publisher;

public interface Exchange<T> extends Listenable<T>, Publisher<T> {

	/**
	 * 批处理
	 * 
	 * @return 返回一个批处理的视图
	 */
	default BatchExchange<T> batch() {
		return new FakeBatchExchange<>(this);
	}
}
