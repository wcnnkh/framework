package run.soeasy.framework.core.collection.factory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface ListFactory<E, T extends List<E>> extends CollectionFactory<E, T> {
	@Override
	default List<E> display(Collection<E> source) {
		return (source instanceof List) ? Collections.unmodifiableList((List<E>) source) : clone(source);
	}
}
