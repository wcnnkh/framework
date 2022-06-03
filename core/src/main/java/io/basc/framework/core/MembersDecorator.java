package io.basc.framework.core;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class MembersDecorator<S, T extends Members<S>> extends Members<S> {

	public MembersDecorator(Class<?> sourceClass, Function<Class<?>, ? extends Stream<S>> processor) {
		super(sourceClass, processor);
	}

	public MembersDecorator(Members<S> members) {
		super(members);
	}

	@Override
	public T all() {
		return decorate(super.all());
	}

	@Override
	public T clone() {
		return decorate(super.clone());
	}

	protected abstract T decorate(Members<S> members);

	@Override
	public T distinctAll() {
		return decorate(super.distinctAll());
	}

	@Override
	public T distinctMembers() {
		return decorate(super.distinctMembers());
	}

	@Override
	public T exclude(Predicate<? super S> predicate) {
		return decorate(super.exclude(predicate));
	}

	@Override
	public T filter(Predicate<? super S> predicate) {
		return decorate(super.filter(predicate));
	}

	@Override
	public T jumpTo(Class<?> cursorId) {
		return decorate(super.jumpTo(cursorId));
	}

	@Override
	public T next() {
		return decorate(super.next());
	}

	@Override
	public T shared() {
		return decorate(super.shared());
	}

	@Override
	public T with(Members<S> with) {
		return decorate(super.with(with));
	}

	@Override
	public T withAll() {
		return decorate(super.withAll());
	}

	@Override
	public T withAll(Predicate<Class<?>> predicate) {
		return decorate(super.withAll(predicate));
	}

	@Override
	public T withAll(Predicate<Class<?>> predicate, Function<Class<?>, ? extends Stream<S>> processor) {
		return decorate(super.withAll(predicate, processor));
	}

	@Override
	public T withClass(Class<?> sourceClass) {
		return decorate(super.withClass(sourceClass));
	}
	
	@Override
	public T withClass(Class<?> sourceClass, Predicate<? super S> predicate) {
		return decorate(super.withClass(sourceClass, predicate));
	}
	
	@Override
	public T withMethod(WithMethod method) {
		return decorate(super.withMethod(method));
	}

	@Override
	public T withInterfaces() {
		return decorate(super.withInterfaces());
	}

	@Override
	public T withInterfaces(Predicate<Class<?>> predicate) {
		return decorate(super.withInterfaces(predicate));
	}

	@Override
	public T withInterfaces(Predicate<Class<?>> predicate, Function<Class<?>, ? extends Stream<S>> processor) {
		return decorate(super.withInterfaces(predicate, processor));
	}

	@Override
	public T withStream(Supplier<? extends Stream<S>> streamSupplier) {
		return decorate(super.withStream(streamSupplier));
	}

	@Override
	public T withSuperclass() {
		return decorate(super.withSuperclass());
	}

	@Override
	public T withSuperclass(boolean interfaces) {
		return decorate(super.withSuperclass(interfaces));
	}

	@Override
	public T withSuperclass(boolean interfaces, Predicate<Class<?>> predicate) {
		return decorate(super.withSuperclass(interfaces, predicate));
	}

	@Override
	public T withSuperclass(boolean interfaces, Predicate<Class<?>> predicate,
			Function<Class<?>, ? extends Stream<S>> processor) {
		return decorate(super.withSuperclass(interfaces, predicate, processor));
	}

	@Override
	public T withSuperclass(Predicate<Class<?>> predicate) {
		return decorate(super.withSuperclass(predicate));
	}

	@Override
	public T withSuperclass(Predicate<Class<?>> predicate, Function<Class<?>, ? extends Stream<S>> processor) {
		return decorate(super.withSuperclass(predicate, processor));
	}
}
