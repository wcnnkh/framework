package scw.orm;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;

public class DefaultSetterFilterChain implements SetterFilterChain {
	private Iterator<SetterFilter> iterator;
	private SetterFilterChain chain;

	public DefaultSetterFilterChain(Collection<SetterFilter> filters, SetterFilterChain chain) {
		if (!CollectionUtils.isEmpty(filters)) {
			this.iterator = filters.iterator();
		}
		this.chain = chain;
	}

	public void setter(MappingContext context, Object bean, Object value) throws Exception {
		SetterFilter setterFilter = getNext(context, bean, value);
		if (setterFilter == null) {
			if (chain == null) {
				context.getFieldDefinition().set(bean, value);
			} else {
				chain.setter(context, bean, value);
			}
			return;
		}
		setterFilter.setter(context, bean, value, this);
	}

	protected SetterFilter getNext(MappingContext context, Object bean, Object value) {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next();
		}

		return null;
	}
}
