package io.basc.framework.util.spi;

import io.basc.framework.util.Elements;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.exchange.Listenable;

/**
 * 标识这是一个可观察的ServiceLoader
 * 
 * @author shuchaowen
 *
 * @param <S>
 */
public interface ListenableServiceLoader<S> extends Listenable<Elements<ChangeEvent<S>>>, ServiceLoader<S> {
}
