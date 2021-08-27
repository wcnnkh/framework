package io.basc.framework.util;

import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.utils.CollectionUtils;
import io.basc.framework.lang.RequiredJavaVersion;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;

import java.util.Map;

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

	public boolean isSupported(int version) {
		return version >= getMasterVersion();
	}
	
	public static boolean isSupported(AnnotationMetadata annotationMetadata){
		Map<String, Object> map = annotationMetadata.getAnnotationAttributes(RequiredJavaVersion.class.getName());
		if(!CollectionUtils.isEmpty(map)){
			int version = (Integer) map.get("value");
			if(!INSTANCE.isSupported(version)){
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
