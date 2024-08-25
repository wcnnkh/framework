package io.basc.framework.observe;

import io.basc.framework.util.event.batch.BatchEventDispatcher;
import io.basc.framework.util.observe.Observable;

public interface PublishService<E> extends BatchEventDispatcher<E>, Observable<E> {
}
