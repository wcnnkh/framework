package scw.orm.support;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.orm.Getter;
import scw.orm.GetterFilter;
import scw.orm.GetterFilterChain;
import scw.orm.MappingContext;
import scw.orm.ORMException;

public class DefaultGetterFilterChain implements GetterFilterChain {
	private Iterator<? extends GetterFilter> iterator;
	private GetterFilterChain chain;

	public DefaultGetterFilterChain(Collection<? extends GetterFilter> filters, GetterFilterChain chain) {
		if (!CollectionUtils.isEmpty(filters)) {
			this.iterator = filters.iterator();
		}
		this.chain = chain;
	}

	public Object getter(MappingContext context, Getter getter) throws ORMException {
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
