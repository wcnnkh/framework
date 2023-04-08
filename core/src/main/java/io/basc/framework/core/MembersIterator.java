package io.basc.framework.core;

import io.basc.framework.util.page.PageableIterator;

public class MembersIterator<T> extends PageableIterator<Class<?>, T> {

	public MembersIterator(Members<T> pageables) {
		super(pageables);
	}

	@Override
	public Members<T> next() {
		return (Members<T>) super.next();
	}

}
