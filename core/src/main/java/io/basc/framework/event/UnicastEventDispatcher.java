package io.basc.framework.event;

/**
 * 单播
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface UnicastEventDispatcher<T> extends EventDispatcher<T>, UnicastEventRegistry<T> {
}
