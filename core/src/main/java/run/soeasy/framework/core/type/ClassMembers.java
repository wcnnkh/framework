package run.soeasy.framework.core.type;

import java.io.Serializable;
import java.util.Collections;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.ReloadableElementsWrapper;

@RequiredArgsConstructor
public class ClassMembers<E> implements ReloadableElementsWrapper<E, Elements<E>>, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	@Getter
	private final Class<?> declaringClass;
	@NonNull
	@Getter
	private final Function<? super Class<?>, ? extends Elements<E>> loader;
	private volatile transient Elements<E> source;

	@Override
	public Elements<E> getSource() {
		reload(false);
		return source;
	}

	public boolean reload(boolean force) {
		if (source == null || force) {
			synchronized (this) {
				if (source == null || force) {
					this.source = loader.apply(declaringClass);
					this.source = source == null ? Elements.empty() : source;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void reload() {
		reload(true);
	}

	public ClassMembers<E> getSuperclass() {
		Class<?> superclass = declaringClass.getSuperclass();
		return superclass == null ? null : new ClassMembers<>(superclass, loader);
	}

	public Elements<ClassMembers<E>> getInterfaces() {
		return Elements.of(() -> {
			Class<?>[] interfaces = declaringClass.getInterfaces();
			if (interfaces == null) {
				return Collections.emptyIterator();
			}

			return Elements.forArray(interfaces).map((e) -> new ClassMembers<>(e, loader)).iterator();
		});
	}
}
