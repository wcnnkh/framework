package scw.util;

import scw.value.AnyValue;
import scw.value.Value;

public class JavaVersion extends Version {
	private static final long serialVersionUID = 1L;

	public static final JavaVersion INSTANCE;

	static {
		Version version = new Version(System.getProperty("java.version"));
		if (version.length() > 1) {
			Value fragment = version.get(0).getFragment();
			if (fragment.isNumber() && fragment.getAsIntValue() > 1) {// java9以上
				VersionFragment[] fragments = new VersionFragment[version.length() + 1];
				fragments[0] = new DefaultVersionFragment(new AnyValue(1));
				System.arraycopy(version.getFragments(), 0, fragments, 1, version.length());
				version = new Version(fragments, version.getDividers());
			}
		}
		INSTANCE = new JavaVersion(version.getFragments(), version.getDividers());
	}

	JavaVersion(VersionFragment[] fragments, char dividers) {
		super(fragments, dividers);
	}

	/**
	 * 获取主版本号
	 * 
	 * @return
	 */
	public int getMasterVersion() {
		return getFragments()[1].getFragment().getAsNumber().intValue();
	}

	public boolean isJava5() {
		return getMasterVersion() == 5;
	}

	public boolean isJava6() {
		return getMasterVersion() == 6;
	}

	public boolean isJava7() {
		return getMasterVersion() == 7;
	}

	public boolean isJava8() {
		return getMasterVersion() == 8;
	}
}
