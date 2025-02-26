package io.basc.framework.core.convert.service;

import io.basc.framework.util.Assert;
import io.basc.framework.util.comparator.TypeComparator;

public class ConvertiblePair extends TypeComparator implements Comparable<ConvertiblePair> {

	private final Class<?> sourceType;

	private final Class<?> targetType;

	/**
	 * Create a new source-to-target pair.
	 * 
	 * @param sourceType the source type
	 * @param targetType the target type
	 */
	public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
		Assert.notNull(sourceType, "Source type must not be null");
		Assert.notNull(targetType, "Target type must not be null");
		this.sourceType = sourceType;
		this.targetType = targetType;
	}

	public Class<?> getSourceType() {
		return this.sourceType;
	}

	public Class<?> getTargetType() {
		return this.targetType;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || other.getClass() != ConvertiblePair.class) {
			return false;
		}
		ConvertiblePair otherPair = (ConvertiblePair) other;
		return (this.sourceType == otherPair.sourceType && this.targetType == otherPair.targetType);
	}

	@Override
	public int hashCode() {
		return (this.sourceType.hashCode() * 31 + this.targetType.hashCode());
	}

	@Override
	public String toString() {
		return (this.sourceType.getName() + " -> " + this.targetType.getName());
	}

	public int compareTo(ConvertiblePair o) {
		// 目的是为了让小类型排在前
		int v = compare(sourceType, o.sourceType);
		int ov = compare(targetType, o.targetType);
		if (v == 0) {
			return ov;
		} else if (ov == 0) {
			return v;
		} else {
			if (ov >= 0) {
				return v;
			}
			return 0;
		}
	}
}
