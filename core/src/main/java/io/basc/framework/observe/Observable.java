package io.basc.framework.observe;

import io.basc.framework.event.batch.BatchEventRegistry;

/**
 * 可观察的定义
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface Observable<E> extends BatchEventRegistry<E> {
}
