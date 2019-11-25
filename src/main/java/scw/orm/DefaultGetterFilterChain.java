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

	public Object getter(FieldDefinitionContext context, Object bean) throws Exception {
		GetterFilter getterFilter = getNext(context, bean);
		if (getterFilter == null) {
			return chain == null ? context.getFieldDefinition().get(bean) : chain.getter(context, bean);
		}
		return getterFilter.getter(context, bean, this);
	}

	protected GetterFilter getNext(FieldDefinitionContext context, Object bean) {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
}
