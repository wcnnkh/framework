package io.basc.framework.core;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.util.Elements;

public abstract class DecorationMembers<E, R extends DecorationMembers<E, R>> extends Members<E> {

	public DecorationMembers(Class<?> source, Function<? super Class<?>, ? extends Elements<E>> processor) {
		super(source, processor);
	}

	public DecorationMembers(Members<E> structure) {
		super(structure);
	}

	public DecorationMembers(ResolvableType source, Elements<E> elements,
			Function<? super ResolvableType, ? extends Elements<E>> processor) {
		super(source, elements, processor);
	}

	@Override
	public R all() {
		Members<E> structure = super.all();
		return getStructureDecorator().apply(structure);
	}

	@Override
	public R clone() {
		Members<E> structure = super.clone();
		return getStructureDecorator().apply(structure);
	}

	@Override
	public R exclude(Predicate<? super E> predicate) {
		Members<E> structure = super.exclude(predicate);
		return getStructureDecorator().apply(structure);
	}

	@Override
	public R filter(Predicate<? super E> predicate) {
		Members<E> structure = super.filter(predicate);
		return getStructureDecorator().apply(structure);
	}

	@Override
	public Elements<R> getInterfaces() {
		Elements<? extends Members<E>> elements = super.getInterfaces();
		return elements == null ? null : elements.map((e) -> getStructureDecorator().apply(e));
	}

	public abstract Function<? super Members<E>, ? extends R> getStructureDecorator();

	@Override
	public R getSuperclass() {
		Members<E> structure = super.getSuperclass();
		return structure == null ? null : getStructureDecorator().apply(structure);
	}

	@Override
	public R map(Function<? super Elements<E>, ? extends Elements<E>> mapper) {
		Members<E> structure = super.map(mapper);
		return getStructureDecorator().apply(structure);
	}

	@Override
	public Elements<R> recursion() {
		return super.recursion().map((e) -> getStructureDecorator().apply(e));
	}

	@Override
	public R with(Function<? super Elements<E>, ? extends Elements<E>> withProcessor) {
		Members<E> structure = super.with(withProcessor);
		return getStructureDecorator().apply(structure);
	}

	@Override
	public Elements<E> getElements() {
		return super.getElements();
	}
}
