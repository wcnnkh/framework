package io.basc.framework.value.support;

import io.basc.framework.util.MultiIterator;
import io.basc.framework.value.ConfigurablePropertyFactory;
import io.basc.framework.value.PropertyFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DefaultPropertyFactory extends DefaultValueFactory<String, PropertyFactory>
		implements ConfigurablePropertyFactory {

	public DefaultPropertyFactory(boolean concurrent) {
		super(concurrent);
	}

	public Iterator<String> iterator() {
		List<Iterator<String>> iterators = new LinkedList<Iterator<String>>();
		iterators.add(getValueMap().keySet().iterator());
		Iterator<PropertyFactory> iterator = getFactories();
		while (iterator.hasNext()) {
			iterators.add(iterator.next().iterator());
		}
		return new MultiIterator<String>(iterators);
	}
}
