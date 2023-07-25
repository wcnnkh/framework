package io.basc.framework.event.broadcast;

import io.basc.framework.event.EventDispatcher;

/**
 * 广播
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface BroadcastEventDispatcher<T> extends EventDispatcher<T>, BroadcastEventRegistry<T> {
}
