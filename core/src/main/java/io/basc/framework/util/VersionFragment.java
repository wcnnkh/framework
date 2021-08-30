package io.basc.framework.util;

import io.basc.framework.value.Value;

import java.io.Serializable;

public interface VersionFragment extends Comparable<VersionFragment>, Serializable {
	Value getFragment();
}
