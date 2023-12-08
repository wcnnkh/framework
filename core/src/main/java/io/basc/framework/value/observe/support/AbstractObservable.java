package io.basc.framework.value.observe.support;

import io.basc.framework.event.support.DefaultBroadcastEventDispatcher;
import io.basc.framework.observe.value.ValueChangeEvent;
import io.basc.framework.value.observe.Observable;

public abstract class AbstractObservable<T> extends DefaultBroadcastEventDispatcher<ValueChangeEvent<T>>
		implements Observable<T> {

}
