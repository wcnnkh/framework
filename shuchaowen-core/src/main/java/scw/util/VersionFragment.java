package scw.util;

import java.io.Serializable;

import scw.value.Value;

public interface VersionFragment extends Comparable<VersionFragment>, Serializable {
	Value getFragment();
}
