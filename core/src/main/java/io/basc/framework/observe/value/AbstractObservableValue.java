package io.basc.framework.observe.value;

import io.basc.framework.observe.ChangeEvent;
import io.basc.framework.observe.VariableObserver;

public abstract class AbstractObservableValue<V> extends VariableObserver<ChangeEvent> implements ObservableValue<V> {

}
