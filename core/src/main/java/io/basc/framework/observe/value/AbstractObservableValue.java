package io.basc.framework.observe.value;

import io.basc.framework.observe.VariableObserver;
import io.basc.framework.util.observe.event.ChangeEvent;

public abstract class AbstractObservableValue<V> extends VariableObserver<ChangeEvent> implements ObservableValue<V> {

}
