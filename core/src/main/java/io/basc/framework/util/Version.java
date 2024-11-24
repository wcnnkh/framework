package io.basc.framework.util;

import java.io.Serializable;
import java.util.Comparator;

import io.basc.framework.core.convert.ValueWrapper;

public class Version implements Serializable, Comparable<Version>, Comparator<ValueWrapper> {
	private static final long serialVersionUID = 1L;
	/**
	 * 默认的版本分割符
	 */
	public static final String DIVIDEERS = ".";

	private final ValueWrapper[] fragments;
	private final String dividers;

	public Version(String version) {
		this(version, DIVIDEERS);
	}

	public Version(String version, String dividers) {
		Assert.requiredArgument(version != null, "version");
		this.dividers = dividers;
		String[] arr = StringUtils.splitToArray(version, dividers);
		fragments = new ValueWrapper[arr.length];
		for (int i = 0; i < arr.length; i++) {
			this.fragments[i] = ValueWrapper.of(arr[i]);
		}
	}

	public Version(ValueWrapper[] fragments, String dividers) {
		this.fragments = fragments;
		this.dividers = dividers;
	}

	public String getDividers() {
		return dividers;
	}

	public ValueWrapper[] getFragments() {
		return fragments.clone();
	}

	public int length() {
		return fragments.length;
	}

	public ValueWrapper get(int index) {
		return fragments[index];
	}

	/**
	 * 只比较部分,大于0就说明left&gt;right
	 * 
	 * @param fragments
	 * @return
	 */
	public int compareTo(ValueWrapper[] fragments) {
		for (int i = 0; i < fragments.length && i < this.fragments.length; i++) {
			ValueWrapper fragment1 = this.fragments[i];
			ValueWrapper fragment2 = fragments[i];
			int compare = compare(fragment1, fragment2);
			if (compare == 0) {
				continue;
			}

			return compare;
		}
		return 0;
	}

	/**
	 * 大于0就说明left&gt;right
	 */
	public int compareTo(Version o) {
		int compare = compareTo(o.fragments);
		if (compare != 0) {
			return compare;
		}

		if (fragments.length == o.fragments.length) {
			return 0;// 相等
		} else if (fragments.length > o.fragments.length) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fragments.length; i++) {
			if (i != 0) {
				sb.append(dividers);
			}

			sb.append(fragments[i].getAsString());
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0; i < fragments.length; i++) {
			hash += fragments[i].hashCode();
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof Version) {
			if (((Version) obj).length() != length()) {
				return false;
			}

			for (int i = 0; i < fragments.length; i++) {
				if (!fragments[i].equals(((Version) obj).fragments[i])) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	public static Version valueOf(String version) {
		return new Version(version);
	}

	@Override
	public int compare(ValueWrapper v1, ValueWrapper v2) {
		if (v1.isNumber() && v2.isNumber()) {
			return Double.compare(v1.getAsDouble(), v2.getAsDouble());
		}

		return v1.getAsString().compareTo(v2.getAsString());
	}
}
