package io.basc.framework.mapper;

import io.basc.framework.core.MembersIterator;

public class StructureIterator<T extends Field> extends MembersIterator<T> {

	public StructureIterator(Structure<T> structure) {
		super(structure);
	}

	@Override
	public Structure<T> next() {
		return (Structure<T>) super.next();
	}
}
