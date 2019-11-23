package scw.core.utils;

import scw.core.Verification;

/**
 * 如果不忽略返回true
 * 
 * @author asus1
 *
 */
public class IgnoreClassNameVerification implements Verification<String> {
	private static final String[] IGNORE_PACKAGE_PREFIX = SystemPropertyUtils
			.getArrayProperty(String.class, "ignore.package.prefix",
					new String[] {});

	public boolean verification(String name) {
		for (String prefix : IGNORE_PACKAGE_PREFIX) {
			if (name.startsWith(prefix)) {
				return false;
			}
		}

		if (name.startsWith("java.") || name.startsWith("javax.")
				|| name.indexOf(".") == -1 || name.startsWith("scw.")) {
			return false;
		}

		return ClassUtils.IGNORE_COMMON_THIRD_PARTIES_CLASS_NAME_VERIFICATION
				.verification(name);
	}
}
