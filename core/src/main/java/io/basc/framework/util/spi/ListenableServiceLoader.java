package io.basc.framework.util.spi;

import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.collection.ServiceLoader;
import io.basc.framework.util.exchange.Listenable;
import io.basc.framework.util.exchange.event.ChangeEvent;

/**
 * 标识这是一个可观察的ServiceLoader
 * 
 * @author shuchaowen
 *
 * @param <S>
 */
public interface ListenableServiceLoader<S> extends Listenable<Elements<ChangeEvent<S>>>, ServiceLoader<S> {
}
