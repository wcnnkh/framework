package io.basc.framework.event;

import java.util.function.Supplier;

public interface Observable<T> extends Supplier<T>, EventRegistry<ChangeEvent<T>> {
}
