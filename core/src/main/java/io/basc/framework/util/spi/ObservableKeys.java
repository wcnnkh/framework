package io.basc.framework.util.spi;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Keys;
import io.basc.framework.util.Listenable;
import io.basc.framework.util.actor.ChangeEvent;

/**
 * 标识这是一个可以观察的keys
 * 
 * @author shuchaowen
 *
 * @param <K>
 */
public interface ObservableKeys<K> extends Listenable<Elements<ChangeEvent<K>>>, Keys<K> {
}
