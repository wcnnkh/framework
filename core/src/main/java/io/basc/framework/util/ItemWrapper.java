package io.basc.framework.util;

public interface ItemWrapper<W extends Item> extends Item, NamedWrapper<W> {

	@Override
	default int getPositionIndex() {
		return getSource().getPositionIndex();
	}
}
