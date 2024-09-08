package io.basc.framework.util.observe.future;

import java.util.concurrent.Future;

import io.basc.framework.util.observe.Listenable;
import io.basc.framework.util.observe.Receipt;

public interface ListenableFuture<V> extends Listenable<ListenableFuture<? extends V>>, Receipt, Future<V> {

	/**
	 * 立刻获取结果，如果还未执行完或失败则返回空。但成功的结果集也可能为空所以应该综合{@link #isSuccess()}的结果
	 * 
	 * @return
	 */
	V getNow();
}
