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

	public void setter(MappingContext context, Setter setter, Object value) throws Exception {
		SetterFilter setterFilter = getNext(context, setter, value);
		if (setterFilter == null) {
			if (chain == null) {
				setter.setter(context, value);
			} else {
				chain.setter(context, setter, value);
			}
			return;
		}
		setterFilter.setter(context, setter, value, this);
	}

	protected SetterFilter getNext(MappingContext context, Setter setter, Object value) {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next();
		}

		return null;
	}
}
