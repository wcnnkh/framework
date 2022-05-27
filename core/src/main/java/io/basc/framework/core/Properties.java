package io.basc.framework.core;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import io.basc.framework.util.StringUtils;

public abstract class Properties<S extends Member, T extends Properties<S, T>> extends MembersMapper<S, T> {

	public Properties(Class<?> sourceClass, Function<Class<?>, ? extends Stream<S>> processor) {
		super(sourceClass, processor);
	}

	public Properties(Members<S> members) {
		super(members);
	}

	public Properties(Properties<S, T> members) {
		super(members);
	}

	/**
	 * 排除一些成员
	 * 
	 * @param names
	 * @return
	 */
	public T exclude(Collection<String> names) {
		return exclude((e) -> names.contains(e.getName()));
	}

	public T byName(String name) {
		return filter((e) -> StringUtils.equals(e.getName(), name));
	}

	/**
	 * 忽略常量
	 * 
	 * @return
	 */
	public T ignoreFinal() {
		return exclude((e) -> Modifier.isFinal(e.getModifiers()));
	}

	public T ignoreStatic() {
		return exclude((e) -> Modifier.isStatic(e.getModifiers()));
	}

	/**
	 * 忽略transient描述的字段
	 * 
	 * @return
	 */
	public T ignoreTransient() {
		return exclude((e) -> Modifier.isTransient(e.getModifiers()));
	}
}
