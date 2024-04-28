package io.basc.framework.mapper.support;

import java.util.Comparator;

import io.basc.framework.mapper.Item;
import io.basc.framework.mapper.Items;
import io.basc.framework.observe.register.ElementRegistry;
import io.basc.framework.util.element.Elements;

public class ItemRegistry<T extends Item> extends ElementRegistry<T> implements Items<T> {

	@Override
	public Elements<T> getElements() {
		return getServices().sorted(Comparator.comparing(Item::getPositionIndex));
	}
}
