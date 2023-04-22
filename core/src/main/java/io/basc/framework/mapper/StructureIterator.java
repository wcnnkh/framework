package io.basc.framework.mapper;

import io.basc.framework.core.MembersIterator;

public class StructureIterator<T extends Field> extends MembersIterator<T> {

	public StructureIterator(Mapping<T> structure) {
		super(structure);
	}

	@Override
	public Mapping<T> next() {
		return (Mapping<T>) super.next();
	}
}
