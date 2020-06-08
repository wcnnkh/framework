package scw.util;

import java.io.Serializable;
import java.util.Comparator;

public class Version implements Serializable {
	private static final long serialVersionUID = 1L;
	private final VersionFragment[] fragments;

	public Version(VersionFragment[] fragments) {
		this.fragments = fragments;
	}

	public VersionFragment[] getFragments() {
		return fragments.clone();
	}

	public static interface VersionFragment extends Comparator<VersionFragment>, Serializable {
		String getFragment();
	}
}
