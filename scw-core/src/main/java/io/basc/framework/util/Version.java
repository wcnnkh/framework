package io.basc.framework.util;

import io.basc.framework.core.Assert;
import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.value.StringValue;

import java.io.Serializable;

public class Version implements Serializable, Comparable<Version> {
	private static final long serialVersionUID = 1L;
	/**
	 * 默认的版本分割符
	 */
	public static final char DIVIDEERS = '.';

	private final VersionFragment[] fragments;
	private final char dividers;

	public Version(String version) {
		this(version, DIVIDEERS);
	}

	public Version(String version, char dividers) {
		Assert.requiredArgument(version != null, "version");
		this.dividers = dividers;
		String[] arr = StringUtils.split(version, dividers);
		fragments = new VersionFragment[arr.length];
		for (int i = 0; i < arr.length; i++) {
			this.fragments[i] = new DefaultVersionFragment(new StringValue(arr[i]));
		}
	}

	public Version(VersionFragment[] fragments, char dividers) {
		this.fragments = fragments;
		this.dividers = dividers;
	}

	public char getDividers() {
		return dividers;
	}

	public VersionFragment[] getFragments() {
		return fragments.clone();
	}

	public int length() {
		return fragments.length;
	}

	public VersionFragment get(int index) {
		return fragments[index];
	}

	/**
	 * 只比较部分,大于0就说明left>right
	 * @param fragments
	 * @return
	 */
	public int compareTo(VersionFragment[] fragments) {
		for (int i = 0; i < fragments.length && i < this.fragments.length; i++) {
			VersionFragment fragment1 = this.fragments[i];
			VersionFragment fragment2 = fragments[i];
			int compare = fragment1.compareTo(fragment2);
			if (compare == 0) {
				continue;
			}

			return compare;
		}
		return 0;
	}

	/**
	 * 大于0就说明left>right
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

			sb.append(fragments[i].getFragment().getAsString());
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
}
