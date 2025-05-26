package run.soeasy.framework.core.domain;

import java.util.Iterator;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.math.NumberValue;

/**
 * 这是一个抽象的版本，意味的是可以进行比较的
 * 
 * @author shuchaowen
 *
 */
public interface Version extends Value, Comparable<Value> {
	@Data
	public static class JoinVersion implements Version {
		@NonNull
		private final Elements<Version> elements;
		private final CharSequence delimiter;

		@Override
		public int compareTo(Value other) {
			if (other.isMultiple()) {
				Elements<? extends Value> otherElements = other.getAsElements().toList();
				int compare = compareTo(otherElements);
				if (compare != 0) {
					return compare;
				}

				long count = elements.count();
				long otherCount = otherElements.count();
				return Long.compare(count, otherCount);
			} else {
				if (elements.isEmpty()) {
					return -1;
				}

				int value = elements.first().compareTo(other);
				return value == 0 ? (elements.count() > 1 ? 1 : 0) : value;
			}
		}

		private int compareTo(Elements<? extends Value> otherElements) {
			Iterator<? extends Version> iterator = elements.iterator();
			Iterator<? extends Value> otherIterator = otherElements.iterator();
			while (iterator.hasNext() && otherIterator.hasNext()) {
				Version version = iterator.next();
				Value other = otherIterator.next();
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

	public static interface VersionWrapper<W extends Version> extends Version, ValueWrapper<W> {

		@Override
		default int compareTo(@NonNull Value other) {
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
	default int compareTo(@NonNull Value other) {
		return getAsString().compareTo(other.getAsString());
	}

	default Version join(@NonNull Version version) {
		return new JoinVersion(Elements.forArray(this, version), null);
	}
}
