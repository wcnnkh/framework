package run.soeasy.framework.core;

import java.io.Serializable;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.CharSequenceTemplate;
import run.soeasy.framework.core.domain.Version;
import run.soeasy.framework.core.domain.Version.JoinVersion;
import run.soeasy.framework.core.math.IntValue;

public final class JavaVersion extends JoinVersion implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final JavaVersion INSTANCE = parse(System.getProperty("java.version"));

	public static JavaVersion parse(String version) {
		CharSequenceTemplate versionTemplate = new CharSequenceTemplate(version, ".");
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
		return new JavaVersion(Elements.forArray(array), versionTemplate.getDelimiter(), array[1]);
	}

	private final Version master;

	private JavaVersion(Elements<Version> elements, CharSequence delimiter, Version master) {
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

	public boolean isSupported(@NonNull Class<?> clazz) {
		Package pkg = clazz.getPackage();
		if (pkg == null) {
			// 未知的情况返回true
			return true;
		}

		String implementationVersion = pkg.getImplementationVersion();
		if (StringUtils.isNotEmpty(implementationVersion)) {
			JavaVersion version = parse(implementationVersion);
			if (version.getMaster().compareTo(this.master) > 0) {
				return false;
			}
		}

		String specificationVersion = pkg.getSpecificationVersion();
		if (StringUtils.isNotEmpty(specificationVersion)) {
			JavaVersion version = parse(specificationVersion);
			if (version.getMaster().compareTo(this.master) > 0) {
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
