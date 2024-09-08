package io.basc.framework.util.observe.event;

import java.util.Queue;

import io.basc.framework.util.Elements;
import io.basc.framework.util.observe.Listenable;
import io.basc.framework.util.observe.Listener;
import io.basc.framework.util.observe.Receipt;
import io.basc.framework.util.observe.Registration;

/**
 * 每个listener只会被消费一次
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class DisposableBatchExchange<T> implements BatchExchange<T> {
	private final Queue<Listener<? super Elements<T>>> failureCallbacks;

}
