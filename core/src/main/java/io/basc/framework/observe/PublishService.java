package io.basc.framework.observe;

import io.basc.framework.event.batch.BatchEventDispatcher;

public interface PublishService<E> extends BatchEventDispatcher<E>, Observable<E> {
}
