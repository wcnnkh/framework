package run.soeasy.framework.core.spi;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Provider;
import run.soeasy.framework.core.exchange.Listenable;
import run.soeasy.framework.core.exchange.event.ChangeEvent;

/**
 * 标识这是一个可观察的ServiceLoader
 * 
 * @author shuchaowen
 *
 * @param <S>
 */
public interface ListenableServiceLoader<S> extends Listenable<Elements<ChangeEvent<S>>>, Provider<S> {
}
