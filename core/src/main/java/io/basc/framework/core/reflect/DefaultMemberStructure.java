package io.basc.framework.core.reflect;

import java.lang.reflect.Member;
import java.util.function.Function;

import io.basc.framework.core.DefaultStructure;

final class DefaultMemberStructure<E extends Member, R extends MemberStructure<E, R>> extends MemberStructure<E, R> {
	private final Function<? super MemberStructure<E, R>, ? extends R> membersDecorator;

	public DefaultMemberStructure(DefaultStructure<E> members,
			Function<? super MemberStructure<E, R>, ? extends R> membersDecorator) {
		super(members);
		this.membersDecorator = membersDecorator;
	}

	@Override
	public Function<? super MemberStructure<E, R>, ? extends R> getMemberStructureDecorator() {
		return membersDecorator;
	}

}
