package io.basc.framework.event.unicast;

import io.basc.framework.event.DelayableEventDispatcher;

public interface UnicastDelayableEventDispatcher<T> extends DelayableEventDispatcher<T>, UnicastEventDispatcher<T> {
}
