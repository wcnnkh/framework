package run.soeasy.framework.core.reflect;

import java.lang.reflect.Member;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;

public class MemberProvider<T extends Member> extends ReflectionProvider<T> {
	private static final long serialVersionUID = 1L;
	private volatile transient Map<String, Provider<T>> groupMap;

	public MemberProvider(@NonNull Class<?> declaringClass, @NonNull Function<? super Class<?>, ? extends T[]> loader) {
		super(declaringClass, loader);
	}

	@Override
	public boolean reload(boolean force) {
		if (super.reload(force)) {
			if (groupMap == null || force) {
				List<String> names = map((e) -> e.getName()).distinct().toList();
				if (names.isEmpty()) {
					groupMap = Collections.emptyMap();
				} else {
					Map<String, Provider<T>> map = new LinkedHashMap<>(names.size(), 1);
					for (String name : map((e) -> e.getName()).toSet()) {
						Provider<T> group = filter((e) -> e.getName().equals(name)).cacheable();
						map.put(name, group);
					}
					groupMap = map;
				}
				return true;
			}
		}
		return false;
	}

	public Provider<T> getProvider(String name) {
		reload(false);
		return groupMap.get(name);
	}
}
