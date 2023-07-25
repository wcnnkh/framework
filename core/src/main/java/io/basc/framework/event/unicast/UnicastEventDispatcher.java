package io.basc.framework.event.unicast;

import io.basc.framework.event.EventDispatcher;

/**
 * 单播
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface UnicastEventDispatcher<T> extends EventDispatcher<T>, UnicastEventRegistry<T> {
}
