package io.basc.framework.util.dict;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Observable;
import io.basc.framework.util.event.ChangeEvent;

public interface ObservableKeys<K> extends Observable<Elements<ChangeEvent<K>>> {
	Elements<K> keys();
}
