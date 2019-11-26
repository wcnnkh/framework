package scw.orm;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;

public class DefaultGetterFilterChain implements GetterFilterChain {
	private Iterator<GetterFilter> iterator;
	private GetterFilterChain chain;

	public DefaultGetterFilterChain(Collection<GetterFilter> filters, GetterFilterChain chain) {
		if (!CollectionUtils.isEmpty(filters)) {
			this.iterator = filters.iterator();
		}
		this.chain = chain;
	}

	public Object getter(MappingContext context, Getter getter) throws Exception {
		GetterFilter getterFilter = getNext(context, getter);
		if (getterFilter == null) {
			return chain == null ? getter.getter(context) : chain.getter(context, getter);
		}
		return getterFilter.getter(context, getter, this);
	}

	protected GetterFilter getNext(MappingContext context, Getter getter) {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
}
