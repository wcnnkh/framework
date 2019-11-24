package scw.orm;

import java.util.Collection;
import java.util.Iterator;

import scw.core.reflect.FieldDefinition;
import scw.core.utils.CollectionUtils;

public class DefaultSetterFilterChain implements SetterFilterChain {
	private Iterator<SetterFilter> iterator;
	private SetterFilterChain chain;

	public DefaultSetterFilterChain(Collection<SetterFilter> filters,
			SetterFilterChain chain) {
		if (!CollectionUtils.isEmpty(filters)) {
			this.iterator = filters.iterator();
		}
		this.chain = chain;
	}

	public void setter(FieldDefinition fieldDefinition, Object bean,
			Object value) throws Throwable {
		SetterFilter setterFilter = getNext(fieldDefinition, bean, value);
		if (setterFilter == null) {
			if (chain == null) {
				fieldDefinition.set(bean, value);
			} else {
				chain.setter(fieldDefinition, bean, value);
			}
			return;
		}
		setterFilter.setter(fieldDefinition, bean, value, this);
	}

	protected SetterFilter getNext(FieldDefinition fieldDefinition,
			Object bean, Object value) {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next();
		}

		return null;
	}
}
