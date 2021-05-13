package scw.event;

import scw.util.Supplier;

public interface Observable<T> extends Supplier<T>, EventRegistry<ChangeEvent<T>>{
}
