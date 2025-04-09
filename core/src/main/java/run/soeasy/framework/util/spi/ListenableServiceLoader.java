package run.soeasy.framework.util.spi;

import run.soeasy.framework.util.collection.Elements;
import run.soeasy.framework.util.collection.Provider;
import run.soeasy.framework.util.exchange.Listenable;
import run.soeasy.framework.util.exchange.event.ChangeEvent;

/**
 * 标识这是一个可观察的ServiceLoader
 * 
 * @author shuchaowen
 *
 * @param <S>
 */
public interface ListenableServiceLoader<S> extends Listenable<Elements<ChangeEvent<S>>>, Provider<S> {
}
