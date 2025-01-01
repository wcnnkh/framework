package io.basc.framework.util;

import java.io.Serializable;

import io.basc.framework.lang.RequiredJavaVersion;
import io.basc.framework.util.Version.JoinVersion;
import io.basc.framework.util.math.IntValue;

public class JavaVersion extends JoinVersion implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final JavaVersion INSTANCE;

	static {
		CharSequenceTemplate versionTemplate = new CharSequenceTemplate(System.getProperty("java.version"), ".");
		Version[] array = versionTemplate.getAsElements().toArray(Version[]::new);
		if (array.length > 1) {
			Version fragment = array[0];
			if (fragment.isNumber() && fragment.getAsInt() > 1) {// java9以上
				Version[] fragments = new Version[array.length + 1];
				fragments[0] = new IntValue(1);
				System.arraycopy(array, 0, fragments, 1, array.length);
				array = fragments;
			}
		}
		INSTANCE = new JavaVersion(Elements.forArray(array), versionTemplate.getDelimiter(), array[1]);
	}

	private final Version master;

	JavaVersion(Elements<Version> elements, CharSequence delimiter, Version master) {
		super(elements, delimiter);
		this.master = master;
	}

	public Version getMaster() {
		return master;
	}

	public boolean isJava5() {
		return getMaster().getAsInt() == 5;
	}

	public boolean isJava6() {
		return getMaster().getAsInt() == 6;
	}

	public boolean isJava7() {
		return getMaster().getAsInt() == 7;
	}

	public boolean isJava8() {
		return getMaster().getAsInt() == 8;
	}

	public boolean isSupported(int version) {
		return version >= getMaster().getAsInt();
	}

	public static boolean isSupported(Class<?> clazz) {
		RequiredJavaVersion requiredJavaVersion = clazz.getAnnotation(RequiredJavaVersion.class);
		if (requiredJavaVersion != null) {
			if (!INSTANCE.isSupported(requiredJavaVersion.value())) {
				return false;
			}
		}

		for (Class<?> interfaceClass : clazz.getInterfaces()) {
			if (!isSupported(interfaceClass)) {
				return false;
			}
		}
		return true;
	}
}
