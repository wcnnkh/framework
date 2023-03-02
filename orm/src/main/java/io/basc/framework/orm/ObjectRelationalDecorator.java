package io.basc.framework.orm;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.basc.framework.core.Members;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldDescriptor;

public abstract class ObjectRelationalDecorator<S extends Property, T extends ObjectRelationalDecorator<S, T>>
		extends ObjectRelational<S> {

	public ObjectRelationalDecorator(Class<?> sourceClass, ObjectRelationalResolver objectRelationalResolver, S parent,
			Function<Class<?>, ? extends Stream<S>> processor) {
		super(sourceClass, objectRelationalResolver, parent, processor);
	}

	public ObjectRelationalDecorator(Members<S> members) {
		super(members);
	}

	public ObjectRelationalDecorator(Members<? extends Field> members, Function<? super Field, ? extends S> map) {
		super(members, map);
	}

	@Override
	public T clone() {
		return decorate(super.clone());
	}

	protected abstract T decorate(ObjectRelational<S> members);

	@Override
	public T setParent(S parent) {
		return decorate(super.setParent(parent));
	}

	@Override
	public T setParentField(Field field) {
		return decorate(super.setParentField(field));
	}

	@Override
	public T setObjectRelationalResolver(ObjectRelationalResolver objectRelationalResolver) {
		return decorate(super.setObjectRelationalResolver(objectRelationalResolver));
	}

	@Override
	public T all() {
		return decorate(super.all());
	}

	public T byGetterName(String name, @Nullable Type type) {
		return decorate(super.byGetterName(name, type));
	}

	public T byName(String name) {
		return decorate(super.byName(name));
	}

	public T byName(String name, @Nullable Type type) {
		return decorate(super.byName(name, type));
	}

	public T bySetterName(String name, @Nullable Type type) {
		return decorate(super.bySetterName(name, type));
	}

	@Override
	public T distinct() {
		return decorate(super.distinct());
	}

	public T entity() {
		return decorate(super.entity());
	}

	public T exclude(Collection<String> names) {
		return decorate(super.exclude(names));
	}

	@Override
	public T exclude(Predicate<? super S> predicate) {
		return decorate(super.exclude(predicate));
	}

	@Override
	public T filter(Predicate<? super S> predicate) {
		return decorate(super.filter(predicate));
	}

	public T getters() {
		return decorate(super.getters());
	}

	public T getters(Predicate<? super FieldDescriptor> accept) {
		if (accept == null) {
			return decorate(this);
		}
		return decorate(super.getters(accept));
	}

	public T ignoreFinal() {
		return decorate(super.ignoreFinal());
	}

	public T ignoreStatic() {
		return decorate(super.ignoreStatic());
	}

	public T ignoreTransient() {
		return decorate(super.ignoreTransient());
	}

	@Override
	public T jumpTo(Class<?> cursorId) {
		return decorate(super.jumpTo(cursorId));
	}

	@Override
	public T next() {
		return decorate(super.next());
	}

	public T setters() {
		return decorate(super.setters());
	}

	public T setters(Predicate<? super FieldDescriptor> accept) {
		if (accept == null) {
			return decorate(this);
		}
		return decorate(super.setters(accept));
	}

	@Override
	public T shared() {
		return decorate(super.shared());
	}

	public T strict() {
		return decorate(super.strict());
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
	public T concat(Supplier<? extends Stream<? extends S>> streamSupplier) {
		return decorate(super.concat(streamSupplier));
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

	@Override
	public T rename(String name) {
		return decorate(super.rename(name));
	}

	@Override
	public T withEntitys(Function<? super S, ? extends ObjectRelational<S>> processor) {
		return decorate(super.withEntitys(processor));
	}

	@Override
	public T setNameNestingConnector(String nameNestingConnector) {
		return decorate(super.setNameNestingConnector(nameNestingConnector));
	}

	@Override
	public T setNameNestingDepth(int nameNestingDepth) {
		return decorate(super.setNameNestingDepth(nameNestingDepth));
	}

	@Override
	public T withEntitysAfter(Function<? super ObjectRelational<S>, ? extends ObjectRelational<S>> processor) {
		return decorate(super.withEntitysAfter(processor));
	}

	@Override
	public <E extends Throwable> T withEntitys() {
		return decorate(super.withEntitys());
	}
}
