package scw.value.property;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import scw.core.utils.CollectionUtils;
import scw.util.MultiEnumeration;
import scw.value.SimpleValueFactory;
import scw.value.Value;

public class PropertyFactory extends SimpleValueFactory implements
		BasePropertyFactory {
	private LinkedList<BasePropertyFactory> basePropertyFactories;

	public void addBasePropertyFactory(BasePropertyFactory basePropertyFactory) {
		if (basePropertyFactory == null) {
			return;
		}

		if (basePropertyFactories == null) {
			basePropertyFactories = new LinkedList<BasePropertyFactory>();
		}
		basePropertyFactories.addFirst(basePropertyFactory);
	}

	public void addBasePropertyFactory(
			List<BasePropertyFactory> basePropertyFactories) {
		if (CollectionUtils.isEmpty(basePropertyFactories)) {
			return;
		}

		ListIterator<BasePropertyFactory> listIterator = basePropertyFactories
				.listIterator(basePropertyFactories.size());
		while (listIterator.hasPrevious()) {
			addBasePropertyFactory(listIterator.previous());
		}
	}

	@Override
	public Value get(String key) {
		if (basePropertyFactories != null) {
			for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
				Value value = basePropertyFactory.get(key);
				if (value != null) {
					return value;
				}
			}
		}
		return super.get(key);
	}

	public Enumeration<String> enumerationKeys() {
		if (basePropertyFactories == null) {
			return Collections.emptyEnumeration();
		}

		List<Enumeration<String>> enumerations = new LinkedList<Enumeration<String>>();
		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			enumerations.add(basePropertyFactory.enumerationKeys());
		}
		return new MultiEnumeration<String>(enumerations);
	}
}
