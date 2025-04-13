package run.soeasy.framework.util.reflect;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.util.collection.Provider;
import run.soeasy.framework.util.math.IntValue;
import run.soeasy.framework.util.math.NumberValue;

@RequiredArgsConstructor
public class ReflectionProvider<T> implements Provider<T>, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	@Getter
	private final Class<?> declaringClass;
	@NonNull
	private final Function<? super Class<?>, ? extends T[]> loader;
	private volatile transient T[] cacheArray;
	private volatile IntValue count;

	@Override
	public final Iterator<T> iterator() {
		return Arrays.asList(cacheArray).iterator();
	}

	@Override
	public NumberValue count() {
		reload(false);
		return count;
	}

	@Override
	public boolean isEmpty() {
		reload(false);
		return cacheArray.length != 0;
	}

	@Override
	public boolean isUnique() {
		reload(false);
		return cacheArray.length == 1;
	}

	public boolean reload(boolean force) {
		if (cacheArray == null || force) {
			synchronized (this) {
				if (cacheArray == null || force) {
					this.cacheArray = loader.apply(declaringClass);
					this.count = new IntValue(cacheArray.length);
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
}
