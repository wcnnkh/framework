package io.basc.framework.redis;

import java.io.Serializable;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Supplier;

public class Tuple<V> implements Supplier<V>, Serializable {
	private static final long serialVersionUID = 1L;
	private final V value;
	private final double score;

	public Tuple(V value, double score) {
		this.value = value;
		this.score = score;
	}

	@Override
	public V get() {
		return value;
	}

	public V getValue() {
		return value;
	}

	public double getScore() {
		return score;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (o instanceof Tuple) {
			return ObjectUtils.equals(value, ((Tuple) o).value) && Double.compare(this.score, ((Tuple) o).score) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		long temp = Double.doubleToLongBits(score);
		int result = (int) (temp ^ (temp >>> 32));
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return value != null ? String.format("ScoredValue[%f, %s]", score, getValue())
				: String.format("ScoredValue[%f].empty", score);
	}
}