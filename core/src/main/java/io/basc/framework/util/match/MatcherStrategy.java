package io.basc.framework.util.match;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import io.basc.framework.lang.Nullable;

public final class MatcherStrategy<T> {
	private TreeMap<String, T> strategyMap;
	private final StringMatcher matcher;
	private final MatcherStrategy<T> parent;

	public MatcherStrategy(StringMatcher matcher) {
		this(matcher, null);
	}

	public MatcherStrategy(StringMatcher matcher, @Nullable MatcherStrategy<T> parent) {
		this.matcher = matcher;
		this.parent = parent;
	}

	public StringMatcher getMatcher() {
		return this.matcher;
	}

	public void register(String pattern, T value) {
		if (this.strategyMap == null) {
			this.strategyMap = new TreeMap<String, T>(this.matcher);
		}
		this.strategyMap.put(pattern, value);
	}

	public void unregister(String pattern) {
		if (this.strategyMap == null) {
			return;
		}
		this.strategyMap.remove(pattern);
	}

	public Optional<T> get(String pattern) {
		if (this.strategyMap == null) {
			return this.parent == null ? Optional.empty() : this.parent.get(pattern);
		}

		if (this.matcher != null && this.matcher.isPattern(pattern)) {
			for (Entry<String, T> entry : this.strategyMap.entrySet()) {
				if (matcher.match(entry.getKey(), pattern) && entry.getValue() != null) {
					return Optional.of(entry.getValue());
				}
			}
		}
		T value = this.strategyMap.get(pattern);
		if (value != null) {
			return Optional.of(value);
		}

		return this.parent == null ? Optional.empty() : this.parent.get(pattern);
	}

	public boolean isEmpty() {
		return (this.strategyMap == null || this.strategyMap.isEmpty())
				&& (this.parent == null || this.parent.isEmpty());
	}
}
