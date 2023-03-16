package io.basc.framework.event;

/**
 * 广播
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface BroadcastEventDispatcher<T> extends EventDispatcher<T>, BroadcastEventRegistry<T> {
}
