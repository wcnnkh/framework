package scw.util;

import scw.value.Value;

public class DefaultVersionFragment implements VersionFragment {
	private static final long serialVersionUID = 1L;
	private final Value fragment;

	public DefaultVersionFragment(Value fragment) {
		this.fragment = fragment;
	}

	public int compareTo(VersionFragment o) {
		Value v1 = getFragment();
		Value v2 = o.getFragment();
		if (v1.isNumber() && v2.isNumber()) {
			return v1.getAsDouble().compareTo(v2.getAsDouble());
		}

		return v1.getAsString().compareTo(v2.getAsString());
	}

	public Value getFragment() {
		return fragment;
	}

	@Override
	public int hashCode() {
		return fragment.isNumber() ? fragment.getAsNumber().intValue() : fragment.getAsString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof VersionFragment) {
			Value v1 = getFragment();
			Value v2 = ((VersionFragment) obj).getFragment();
			if (v1.isNumber() && v2.isNumber()) {
				return v1.getAsNumber().equals(v2.getAsNumber());
			}

			return v1.getAsString().equals(v2.getAsString());
		}

		return false;
	}
}