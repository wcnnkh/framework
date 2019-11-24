package scw.orm;

import java.util.Collection;
import java.util.Iterator;

import scw.core.reflect.FieldDefinition;
import scw.core.utils.CollectionUtils;

public class DefaultGetterFilterChain implements GetterFilterChain {
	private Iterator<GetterFilter> iterator;
	private GetterFilterChain chain;

	public DefaultGetterFilterChain(Collection<GetterFilter> filters,
			GetterFilterChain chain) {
		if (!CollectionUtils.isEmpty(filters)) {
			this.iterator = filters.iterator();
		}
		this.chain = chain;
	}

	public Object getter(FieldDefinition fieldDefinition, Object bean)
			throws Throwable {
		GetterFilter getterFilter = getNext(fieldDefinition, bean);
		if (getterFilter == null) {
			return chain == null ? fieldDefinition.get(bean) : chain.getter(
					fieldDefinition, bean);
		}
		return getterFilter.getter(fieldDefinition, bean, this);
	}

	protected GetterFilter getNext(FieldDefinition fieldDefinition, Object bean) {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
}
