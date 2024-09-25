package io.basc.framework.observe.value;

import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.function.Optional;
import io.basc.framework.util.observe_old.Observable;

/**
 * 一个可观察的值
 * 
 * @author shuchaowen
 *
 * @param <V> 值类型
 */
public interface ObservableValue<V> extends Optional<V>, Observable<ChangeEvent<V>> {
}
