package io.basc.framework.core.reflect;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Comparator;

public final class MemberScopeComparator<T extends Member> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		return Integer.compare(getOrder(o1), getOrder(o2));
	}

	public int getOrder(T member) {
		if (Modifier.isPublic(member.getModifiers())) {
			return 1;
		}

		if (Modifier.isProtected(member.getModifiers())) {
			return 2;
		}

		if (Modifier.isPrivate(member.getModifiers())) {
			return 3;
		}
		return 0;
	}
}
