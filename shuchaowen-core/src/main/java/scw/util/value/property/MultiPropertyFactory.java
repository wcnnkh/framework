package scw.util.value.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import scw.core.utils.CollectionUtils;
import scw.util.MultiEnumeration;
import scw.util.value.Value;

public class MultiPropertyFactory extends AbstractPropertyFactory {
	private LinkedList<PropertyFactory> propertyFactories = new LinkedList<PropertyFactory>();

	public Value get(String key) {
		for (PropertyFactory propertyFactory : propertyFactories) {
			Value value = propertyFactory.get(key);
			if (value == null) {
				continue;
			}
			return value;
		}
		return null;
	}

	public Enumeration<String> enumerationKeys() {
		Collection<Enumeration<? extends String>> enumerations = new LinkedList<Enumeration<? extends String>>();
		for (PropertyFactory propertyFactory : propertyFactories) {
			enumerations.add(propertyFactory.enumerationKeys());
		}
		return new MultiEnumeration<String>(enumerations);
	}

	public void add(PropertyFactory propertyFactory) {
		propertyFactories.add(propertyFactory);
	}

	public void addFirst(PropertyFactory propertyFactory) {
		propertyFactories.addFirst(propertyFactory);
	}

	public void addAll(Collection<? extends PropertyFactory> propertyFactories) {
		this.propertyFactories.addAll(propertyFactories);
	}

	public void addAll(Collection<? extends PropertyFactory> propertyFactories, boolean first) {
		if (first) {
			if (CollectionUtils.isEmpty(propertyFactories)) {
				return;
			}

			List<PropertyFactory> list = new ArrayList<PropertyFactory>(propertyFactories);
			ListIterator<PropertyFactory> listIterator = list.listIterator(list.size());
			while (listIterator.hasPrevious()) {
				addFirst(listIterator.previous());
			}
		} else {
			addAll(propertyFactories);
		}
	}
}
