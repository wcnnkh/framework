package io.basc.framework.core;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class MembersMapper<S, T extends Members<S>> extends Members<S> {

	public MembersMapper(Class<?> sourceClass, Function<Class<?>, ? extends Stream<S>> processor) {
		super(sourceClass, processor);
	}

	public MembersMapper(Members<S> members) {
		super(members);
	}

	public MembersMapper(MembersMapper<S, T> members) {
		super(members);
	}

	@Override
	public T all() {
		return map(super.all());
	}

	@Override
	public T clone() {
		Members<S> members = super.clone();
		return map(members);
	}

	@Override
	public T filter(Predicate<? super S> predicate) {
		return map(super.filter(predicate));
	}

	@Override
	public T jumpTo(Class<?> cursorId) {
		Members<S> members = super.jumpTo(cursorId);
		return map(members);
	}

	protected abstract T map(Members<S> members);

	@Override
	public T next() {
		return map(super.next());
	}

	@Override
	public T with(Members<S> with) {
		return map(super.with(with));
	}

	@Override
	public T withAll() {
		return map(super.withAll());
	}

	@Override
	public T withAll(Predicate<Class<?>> predicate) {
		return map(super.withAll(predicate));
	}

	@Override
	public T withAll(Predicate<Class<?>> predicate, Function<Class<?>, ? extends Stream<S>> processor) {
		return map(super.withAll(predicate, processor));
	}

	@Override
	public T withClass(Class<?> sourceClass) {
		return map(super.withClass(sourceClass));
	}

	@Override
	public T withClass(Class<?> sourceClass, Function<Class<?>, ? extends Stream<S>> processor) {
		return map(super.withClass(sourceClass, processor));
	}

	@Override
	public T withInterfaces() {
		return map(super.withInterfaces());
	}

	@Override
	public T withInterfaces(Predicate<Class<?>> predicate) {
		return map(super.withInterfaces(predicate));
	}

	@Override
	public T withInterfaces(Predicate<Class<?>> predicate, Function<Class<?>, ? extends Stream<S>> processor) {
		return map(super.withInterfaces(predicate, processor));
	}

	@Override
	public T withStream(Stream<S> stream) {
		return map(super.withStream(stream));
	}

	@Override
	public T withSuperclass() {
		return map(super.withSuperclass());
	}

	@Override
	public T withSuperclass(boolean interfaces) {
		return map(super.withSuperclass(interfaces));
	}

	@Override
	public T withSuperclass(boolean interfaces, Predicate<Class<?>> predicate) {
		return map(super.withSuperclass(interfaces, predicate));
	}

	@Override
	public T withSuperclass(boolean interfaces, Predicate<Class<?>> predicate,
			Function<Class<?>, ? extends Stream<S>> processor) {
		return map(super.withSuperclass(interfaces, predicate, processor));
	}

	@Override
	public T withSuperclass(Predicate<Class<?>> predicate) {
		return map(super.withSuperclass(predicate));
	}

	@Override
	public T withSuperclass(Predicate<Class<?>> predicate, Function<Class<?>, ? extends Stream<S>> processor) {
		return map(super.withSuperclass(predicate, processor));
	}

	@Override
	public T shared() {
		return map(super.shared());
	}

	@Override
	public T exclude(Predicate<? super S> predicate) {
		return map(super.exclude(predicate));
	}
}
