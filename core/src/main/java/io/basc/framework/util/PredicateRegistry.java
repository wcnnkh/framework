package io.basc.framework.util;

import java.util.function.Function;
import java.util.function.Predicate;

public class PredicateRegistry<T> implements Predicate<T> {
	private Predicate<T> predicate;

	public PredicateRegistry() {
		this(XUtils.alwaysTruePredicate());
	}

	public PredicateRegistry(Predicate<T> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		this.predicate = predicate;
	}

	@Override
	public boolean test(T t) {
		return predicate.test(t);
	}

	/**
	 * 返回自身，链式调用
	 */
	public PredicateRegistry<T> and(Predicate<? super T> predicate) {
		this.predicate = this.predicate.and(predicate);
		return this;
	}

	/**
	 * 返回自身，链式调用
	 */
	public PredicateRegistry<T> or(Predicate<? super T> predicate) {
		this.predicate = this.predicate.or(predicate);
		return this;
	}

	/**
	 * 返回自身，链式调用
	 */
	public PredicateRegistry<T> negate() {
		this.predicate = this.predicate.negate();
		return this;
	}

	/**
	 * 返回一个新的
	 * 
	 * @param <R>
	 * @param mapper
	 * @return
	 */
	public <R> PredicateRegistry<R> map(Function<? super Predicate<T>, ? extends Predicate<R>> mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		return new PredicateRegistry<>(mapper.apply(this.predicate));
	}
}
