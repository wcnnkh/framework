package io.basc.framework.util;

import java.util.TreeSet;
import java.util.function.Predicate;

public class PatternRegistry<T> implements Predicate<T> {
	private final TreeSet<T> map;
	private Matcher<T> matcher;

	public PatternRegistry(Matcher<T> matcher) {
		Assert.requiredArgument(matcher != null, "matcher");
		this.matcher = matcher;
	}

	public void register(T pattern) {
		if (map == null) {
			map = new TreeSet<>(matcher);
		}
		map.add(pattern);
	}

	public void unregister(T pattern) {
		map.remove(pattern);
	}

	public Elements<T> match(T pattern) {
		if(map == null) {
			return map;
		}
		return Elements.of(() -> map.stream().filter((e) -> matcher.match(e, pattern)));
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean test(T t) {
		return !match(t).isEmpty();
	}
}
