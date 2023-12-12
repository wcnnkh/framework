package io.basc.framework.observe.value;

import io.basc.framework.observe.ChangeEvent;
import io.basc.framework.observe.Observer;

public abstract class AbstractObservableValue<V> extends Observer<ChangeEvent> implements ObservableValue<V> {

}
