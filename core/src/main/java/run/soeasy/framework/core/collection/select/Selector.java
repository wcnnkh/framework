package run.soeasy.framework.core.collection.select;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Filter;

/**
 * 单选
 * 
 * @author soeasy.run
 *
 * @param <T>
 */
public interface Selector<T> extends Filter<T> {
	T select(Elements<T> elements);

	@Override
	default Elements<T> apply(Elements<T> elements) {
		T singleton = select(elements);
		return singleton == null ? Elements.empty() : Elements.singleton(singleton);
	}
}
