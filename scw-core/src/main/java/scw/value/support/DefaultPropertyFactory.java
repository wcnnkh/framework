package scw.value.support;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import scw.util.MultiIterator;
import scw.value.ConfigurablePropertyFactory;
import scw.value.PropertyFactory;

public class DefaultPropertyFactory extends DefaultValueFactory<String, PropertyFactory>
		implements ConfigurablePropertyFactory {

	public DefaultPropertyFactory(boolean concurrent) {
		super(concurrent);
	}

	public Iterator<String> iterator() {
		List<Iterator<String>> iterators = new LinkedList<Iterator<String>>();
		iterators.add(getValueMap().keySet().iterator());
		Iterator<PropertyFactory> iterator = getFactoriesIterator();
		while (iterator.hasNext()) {
			iterators.add(iterator.next().iterator());
		}
		return new MultiIterator<String>(iterators);
	}
}
