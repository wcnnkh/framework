package io.basc.framework.event;

import io.basc.framework.util.Supplier;

public interface Observable<T> extends Supplier<T>, EventRegistry<ChangeEvent<T>>{
}
