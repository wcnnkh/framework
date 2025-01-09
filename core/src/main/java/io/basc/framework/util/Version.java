package io.basc.framework.util;

import java.util.Iterator;
import java.util.stream.Collectors;

import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.math.NumberValue;
import lombok.Data;
import lombok.NonNull;

/**
 * 这是一个抽象的版本，意味的是可以进行比较的
 * 
 * @author shuchaowen
 *
 */
public interface Version extends Any, Comparable<Any> {
	@Data
	public static class JoinVersion implements Version {
		@NonNull
		private final Elements<Version> elements;
		private final CharSequence delimiter;

		@Override
		public int compareTo(Any other) {
			if (other.isMultiple()) {
				Elements<? extends Any> otherElements = other.getAsElements().toList();
				int compare = compareTo(otherElements);
				if (compare != 0) {
					return compare;
				}

				long count = elements.count();
				long otherCount = otherElements.count();
				if (count == otherCount) {
					return 0;// 相等
				} else if (count > otherCount) {
					return 1;
				} else {
					return -1;
				}
			} else {
				if (elements.isEmpty()) {
					return -1;
				}

				int value = elements.first().compareTo(other);
				return value == 0 ? (elements.count() > 1 ? 1 : 0) : value;
			}
		}

		private int compareTo(Elements<? extends Any> otherElements) {
			Iterator<? extends Version> iterator = elements.iterator();
			Iterator<? extends Any> otherIterator = otherElements.iterator();
			while (iterator.hasNext() && otherIterator.hasNext()) {
				Version version = iterator.next();
				Any other = otherIterator.next();
				int v = version.compareTo(other);
				if (v == 0) {
					continue;
				}
				return v;
			}
			return 0;
		}

		@Override
		public String getAsString() {
			return delimiter == null ? elements.map((e) -> e.getAsString()).collect(Collectors.joining())
					: elements.map((e) -> e.getAsString()).collect(Collectors.joining(delimiter));
		}

		@Override
		public NumberValue getAsNumber() {
			throw new UnsupportedOperationException("Not a Number");
		}

		@Override
		public boolean isMultiple() {
			return true;
		}

		@Override
		public boolean isNumber() {
			return false;
		}

		@Override
		public Version getAsVersion() {
			return new CharSequenceTemplate(getAsString(), delimiter);
		}

		@Override
		public Version join(@NonNull Version version) {
			Elements<? extends Version> joinElements = Elements.singleton(version);
			return new JoinVersion(this.elements.concat(joinElements), delimiter);
		}
	}

	public static interface VersionWrapper<W extends Version> extends Version, AnyWrapper<W> {

		@Override
		default int compareTo(@NonNull Any other) {
			return getSource().compareTo(other);
		}

		@Override
		default Version join(@NonNull Version version) {
			return getSource().join(version);
		}
	}

	/**
	 * 默认使用字符串的方式比较，如果有更合理的方式请重写
	 */
	@Override
	default int compareTo(@NonNull Any other) {
		return getAsString().compareTo(other.getAsString());
	}

	default Version join(@NonNull Version version) {
		return new JoinVersion(Elements.forArray(this, version), null);
	}
}
