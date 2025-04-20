package run.soeasy.framework.core.type;

import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Listable;
import run.soeasy.framework.core.collection.Provider;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ClassMembersLoader<E> implements Provider<E>, Listable<ClassMembers<E>>, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private final ClassMembers<E> classMembers;
	private final ClassMembersLoader<E> superclass;
	private final Elements<ClassMembersLoader<E>> interfaces;

	public ClassMembersLoader(Class<?> declaringClass, Function<? super Class<?>, ? extends Elements<E>> loader) {
		this(new ClassMembers<>(declaringClass, loader));
	}

	public ClassMembersLoader(ClassMembers<E> provider) {
		this(provider, null, null);
	}

	@Override
	public boolean isEmpty() {
		return classMembers.isEmpty() && (interfaces == null || interfaces.allMatch((ClassMembersLoader::isEmpty)))
				&& (superclass == null || superclass.isEmpty());
	}

	@Override
	public Iterator<E> iterator() {
		MergedElements<E> elements = new MergedElements<>(classMembers,
				superclass == null ? Elements.empty() : superclass,
				interfaces == null ? Elements.empty() : interfaces.flatMap((e) -> e));
		return elements.iterator();
	}

	@Override
	public void reload() {
		classMembers.reload();
		if (superclass != null) {
			superclass.reload();
		}

		if (interfaces != null) {
			for (ClassMembersLoader<E> loader : interfaces) {
				loader.reload();
			}
		}
	}

	@Override
	public Elements<ClassMembers<E>> getElements() {
		return new MergedElements<>(Elements.singleton(classMembers),
				superclass == null ? Elements.empty() : superclass.getElements(),
				interfaces == null ? Elements.empty() : interfaces.flatMap((e) -> e.getElements()));
	}

	public ClassMembersLoader<E> withInterfaces() {
		Elements<ClassMembersLoader<E>> interfaces = classMembers.getInterfaces().map(ClassMembersLoader::new);
		return new ClassMembersLoader<>(classMembers, superclass == null ? superclass : superclass.withInterfaces(),
				interfaces.map(ClassMembersLoader::withInterfaces));
	}

	public ClassMembersLoader<E> withSuperclass() {
		ClassMembers<E> superclassProvider = classMembers.getSuperclass();
		if (superclassProvider == null) {
			return this;
		}

		ClassMembersLoader<E> superclass = new ClassMembersLoader<>(superclassProvider);
		return new ClassMembersLoader<>(classMembers, superclass.withSuperclass(), interfaces);
	}

	public ClassMembersLoader<E> withAll() {
		return withSuperclass().withInterfaces();
	}
}
