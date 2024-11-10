package io.basc.framework.util;

import io.basc.framework.util.alias.NamedWrapper;

public interface ItemWrapper<W extends Item> extends Item, NamedWrapper<W> {

	@Override
	default int getPositionIndex() {
		return getSource().getPositionIndex();
	}
}
