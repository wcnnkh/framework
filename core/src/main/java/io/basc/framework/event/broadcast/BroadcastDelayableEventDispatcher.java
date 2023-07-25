package io.basc.framework.event.broadcast;

import io.basc.framework.event.DelayableEventDispatcher;

public interface BroadcastDelayableEventDispatcher<T> extends BroadcastEventDispatcher<T>, DelayableEventDispatcher<T> {
}
