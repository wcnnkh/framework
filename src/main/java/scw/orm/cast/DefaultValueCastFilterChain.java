package scw.orm.cast;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.core.utils.TypeUtils;

public final class DefaultValueCastFilterChain implements ValueCastFilterChain {
	private Iterator<ValueCastFilter> iterator;
	private ValueCastFilterChain chain;

	public DefaultValueCastFilterChain(Collection<ValueCastFilter> filters,
			ValueCastFilterChain chain) {
		if (!CollectionUtils.isEmpty(filters)) {
			iterator = filters.iterator();
		}
		this.chain = chain;
	}

	public Object doFilter(Type type, Object value) throws ValueCastException {
		ValueCastFilter filter = getNext();
		if (filter == null) {
			return chain == null ? TypeUtils.toClass(type).cast(value) : chain
					.doFilter(type, value);
		}
		return filter.doFilter(type, value, this);
	}

	private ValueCastFilter getNext() {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next();
		}

		return null;
	}
}
