package io.basc.framework.transform.factory;

import io.basc.framework.util.Item;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.observe_old.Observable;

public interface ObservablePropertyFactory extends Observable<ChangeEvent<String>> {

}
