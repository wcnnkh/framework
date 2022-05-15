package io.basc.framework.util;

import io.basc.framework.env.BascObject;
import io.basc.framework.lang.LinkedThreadLocal;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.stream.StreamProcessorSupport;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Keywords extends BascObject implements Predicate<String>,
		Iterable<String>, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	private static final LinkedThreadLocal<Object> NESTED = new LinkedThreadLocal<Object>(
			Keywords.class.getName());

	/**
	 * 驼峰命名策略
	 */
	public static final KeywordStrategy HUMP = new HumpKeywordStrategy(true);
	/**
	 * 原始的策略，不做任何处理
	 */
	public static final KeywordStrategy ORIGINAL = new OriginalKeywordStrategy(
			false);

	private final KeywordStrategy strategy;
	private final LinkedList<String> keywords = new LinkedList<>();
	private final Keywords parent;
	private Predicate<String> predicate;

	public Keywords(KeywordStrategy strategy) {
		this(null, strategy);
	}

	public Keywords(Keywords parent, KeywordStrategy strategy) {
		this(parent, strategy, new String[0]);
	}

	public Keywords(KeywordStrategy strategy, String... keywords) {
		this(null, strategy, keywords);
	}

	public Keywords(Keywords parent, KeywordStrategy strategy,
			String... keywords) {
		this.strategy = strategy;
		this.parent = parent;
		if (keywords != null && keywords.length != 0) {
			for (String s : keywords) {
				addLast(s);
			}
		}
	}

	@Override
	public Keywords clone() {
		return clone(false);
	}

	public Keywords clone(boolean cloneParent) {
		Keywords keywords = new Keywords(cloneParent ? parent.clone() : parent,
				strategy);
		keywords.keywords.addAll(this.keywords);
		keywords.predicate = this.predicate;
		return keywords;
	}

	@Override
	public Iterator<String> iterator() {
		return keywords.iterator();
	}

	public Stream<String> stream() {
		return keywords.stream();
	}

	public Stream<String> streamAll() {
		if (parent == null) {
			return stream();
		}
		return Stream.concat(stream(), parent.streamAll());
	}

	@Nullable
	public Keywords getParent() {
		return parent;
	}

	public Keywords clearAll() {
		Keywords keyword = this;
		while (keyword != null) {
			keyword.clear();
			keyword = keyword.parent;
		}
		return this;
	}

	public Keywords clear() {
		synchronized (this.keywords) {
			this.keywords.clear();
		}
		return this;
	}

	public Keywords addFirst(String key) {
		String keyUse = strategy.format(key);
		Assert.requiredArgument(test(keyUse), "key");
		synchronized (this.keywords) {
			this.keywords.addFirst(keyUse);
		}
		return this;
	}

	public Keywords addLast(String key) {
		String keyUse = strategy.format(key);
		Assert.requiredArgument(test(keyUse), "key");
		synchronized (this.keywords) {
			this.keywords.addLast(keyUse);
		}
		return this;
	}

	public Predicate<String> getPredicate() {
		return predicate;
	}

	public Keywords setPredicate(Predicate<String> predicate) {
		this.predicate = predicate;
		return this;
	}

	@Override
	public boolean test(String key) {
		if (this.keywords.stream()
				.filter((e) -> strategy.test(e, key) || strategy.test(key, e))
				.findAny().isPresent()) {
			return false;
		}

		if (predicate != null) {
			if (!NESTED.exists(predicate) && !NESTED.exists(this)) {
				try {
					NESTED.set(predicate);
					NESTED.set(this);
					if (!predicate.test(key)) {
						return false;
					}
				} finally {
					NESTED.remove(this);
					NESTED.remove(predicate);
				}
			}
		}

		return parent == null || parent.test(key);
	}

	public boolean exists(String key) {
		return (parent != null && parent.exists(key))
				&& this.keywords.stream()
						.filter((e) -> strategy.exists(e, key)).findAny()
						.isPresent();
	}

	public Pair<String, Integer> indexOf(String express) {
		Pair<String, Integer> index = StreamProcessorSupport.process(
				this.keywords, (e) -> express.indexOf(e),
				(e) -> e.getValue() != -1).orElse(null);
		if (index == null && parent != null) {
			return parent.indexOf(express);
		}
		return index;
	}

	public String getFirst() {
		return this.keywords.getFirst();
	}

	public String getLast() {
		return this.keywords.getLast();
	}

	public Keywords removeFirst() {
		synchronized (this.keywords) {
			this.keywords.removeFirst();
		}

		return this;
	}

	public Keywords removeLast() {
		synchronized (this.keywords) {
			this.keywords.removeLast();
		}
		return this;
	}

	private static class HumpKeywordStrategy extends AbstractKeywordStrategy {
		private static final long serialVersionUID = 1L;

		public HumpKeywordStrategy(boolean ignoreCase) {
			super(ignoreCase);
		}

		@Override
		public String format(String key) {
			return StringUtils.toUpperCase(key, 0, 1);
		}
	}

	private static class OriginalKeywordStrategy extends
			AbstractKeywordStrategy {
		private static final long serialVersionUID = 1L;

		public OriginalKeywordStrategy(boolean ignoreCase) {
			super(ignoreCase);
		}

		@Override
		public String format(String key) {
			return key;
		}

		@Override
		public boolean test(String left, String right) {
			return StringUtils.equals(left, right, isIgnoreCase());
		}
	}

	public static abstract class AbstractKeywordStrategy implements
			KeywordStrategy, Serializable {
		private static final long serialVersionUID = 1L;
		private final boolean ignoreCase;

		public AbstractKeywordStrategy(boolean ignoreCase) {
			this.ignoreCase = ignoreCase;
		}

		public boolean isIgnoreCase() {
			return ignoreCase;
		}

		@Override
		public boolean test(String left, String right) {
			return StringUtils.contains(left, right, isIgnoreCase());
		}

		@Override
		public boolean exists(String left, String right) {
			return StringUtils.equals(left, right, isIgnoreCase());
		}
	}

	public static interface KeywordStrategy {
		String format(String key);

		boolean test(String left, String right);

		boolean exists(String left, String right);
	}
}
