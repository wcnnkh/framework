package io.basc.framework.mapper;

public class ItemWrapper<W extends Item> extends NamedWrapper<W> implements Item {

	public ItemWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public int getPositionIndex() {
		return wrappedTarget.getPositionIndex();
	}
}
