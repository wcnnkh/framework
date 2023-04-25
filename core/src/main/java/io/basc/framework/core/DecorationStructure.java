package io.basc.framework.core;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.util.Elements;

public abstract class DecorationStructure<E, R extends DecorationStructure<E, R>> extends DefaultStructure<E> {

	public DecorationStructure(Members<E> members, Function<? super ResolvableType, ? extends Elements<E>> processor) {
		super(members, processor);
	}

	public DecorationStructure(ResolvableType source,
			Function<? super ResolvableType, ? extends Elements<E>> processor) {
		super(source, processor);
	}

	public DecorationStructure(DefaultStructure<E> members) {
		super(members);
	}

	public DecorationStructure(Class<?> source, Function<? super Class<?>, ? extends Elements<E>> processor) {
		super(source, processor);
	}

	@Override
	public R all() {
		DefaultStructure<E> structure = super.all();
		return getStructureDecorator().apply(structure);
	}

	@Override
	public R clone() {
		DefaultStructure<E> structure = super.clone();
		return getStructureDecorator().apply(structure);
	}

	@Override
	public R exclude(Predicate<? super E> predicate) {
		DefaultStructure<E> structure = super.exclude(predicate);
		return getStructureDecorator().apply(structure);
	}

	@Override
	public R filter(Predicate<? super E> predicate) {
		DefaultStructure<E> structure = super.filter(predicate);
		return getStructureDecorator().apply(structure);
	}

	@Override
	public R getMembers() {
		return getStructureDecorator().apply(super.getMembers());
	}

	@Override
	public Elements<R> getInterfaces() {
		Elements<? extends DefaultStructure<E>> elements = super.getInterfaces();
		return elements == null ? null : elements.map((e) -> getStructureDecorator().apply(e));
	}

	public abstract Function<? super DefaultStructure<E>, ? extends R> getStructureDecorator();

	@Override
	public R getSuperclass() {
		DefaultStructure<E> structure = super.getSuperclass();
		return structure == null ? null : getStructureDecorator().apply(structure);
	}

	@Override
	public R map(Function<? super Elements<E>, ? extends Elements<E>> mapper) {
		DefaultStructure<E> structure = super.map(mapper);
		return getStructureDecorator().apply(structure);
	}

	@Override
	public Elements<R> recursion() {
		return super.recursion().map((e) -> getStructureDecorator().apply(e));
	}

	@Override
	public Elements<E> getElements() {
		return super.getElements();
	}
}
