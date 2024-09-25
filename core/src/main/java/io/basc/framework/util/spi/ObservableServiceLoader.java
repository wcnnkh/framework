package io.basc.framework.util.spi;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Observable;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.actor.ChangeEvent;

/**
 * 标识这是一个可观察的ServiceLoader
 * 
 * @author shuchaowen
 *
 * @param <S>
 */
public interface ObservableServiceLoader<S> extends Observable<Elements<ChangeEvent<S>>>, ServiceLoader<S> {
}
