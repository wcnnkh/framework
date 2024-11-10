package io.basc.framework.util;

import java.util.Map;

import io.basc.framework.convert.lang.ValueWrapper;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.lang.RequiredJavaVersion;

public class JavaVersion extends Version {
	private static final long serialVersionUID = 1L;

	public static final JavaVersion INSTANCE;

	static {
		Version version = new Version(System.getProperty("java.version"));
		if (version.length() > 1) {
			ValueWrapper fragment = version.get(0);
			if (fragment.isNumber() && fragment.getAsInt() > 1) {// java9以上
				ValueWrapper[] fragments = new ValueWrapper[version.length() + 1];
				fragments[0] = ValueWrapper.of(1);
				System.arraycopy(version.getFragments(), 0, fragments, 1, version.length());
				version = new Version(fragments, version.getDividers());
			}
		}
		INSTANCE = new JavaVersion(version.getFragments(), version.getDividers());
	}

	JavaVersion(ValueWrapper[] fragments, String dividers) {
		super(fragments, dividers);
	}

	/**
	 * 获取主版本号
	 * 
	 * @return
	 */
	public int getMasterVersion() {
		return getFragments()[1].getAsNumber().intValue();
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

	public boolean isSupported(int version) {
		return version >= getMasterVersion();
	}

	public static boolean isSupported(AnnotationMetadata annotationMetadata) {
		Map<String, Object> map = annotationMetadata.getAnnotationAttributes(RequiredJavaVersion.class.getName());
		if (!CollectionUtils.isEmpty(map)) {
			int version = (Integer) map.get("value");
			if (!INSTANCE.isSupported(version)) {
				return false;
			}
		}
		return true;
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
