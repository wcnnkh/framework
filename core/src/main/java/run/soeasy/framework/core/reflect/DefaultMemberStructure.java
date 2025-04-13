package run.soeasy.framework.core.reflect;

import java.lang.reflect.Member;
import java.util.function.Function;

import run.soeasy.framework.core.Members;

final class DefaultMemberStructure<E extends Member, R extends ReflectionMembers<E, R>> extends ReflectionMembers<E, R> {
	private final Function<? super ReflectionMembers<E, R>, ? extends R> membersDecorator;

	public DefaultMemberStructure(Members<E> members,
			Function<? super ReflectionMembers<E, R>, ? extends R> membersDecorator) {
		super(members);
		this.membersDecorator = membersDecorator;
	}

	@Override
	public Function<? super ReflectionMembers<E, R>, ? extends R> getMemberStructureDecorator() {
		return membersDecorator;
	}

}
