package scw.core.utils;

import scw.core.Verification;

/**
 * 常见的第三方名句忽略
 * 如果忽略返回false
 * @author shuchaowen
 *
 */
public class IgnoreCommonThirdPartiesClassNameVerification implements
		Verification<String> {
	private static final String[] IGNORE_PACKAGE_PREFIX = SystemPropertyUtils
			.getArrayProperty(String.class, "ignore.common.third.parties.package.prefix",
					new String[] {});

	/**
	 * 如果匹配返回false
	 */
	public boolean verification(String name) {
		for (String prefix : IGNORE_PACKAGE_PREFIX) {
			if (name.startsWith(prefix)) {
				return false;
			}
		}

		return !(name.startsWith("org.apache.")
				|| name.startsWith("freemarker.")
				|| name.startsWith("com.alibaba.")
				|| name.startsWith("com.caucho.")
				|| name.startsWith("redis.clients.")
				|| name.startsWith("com.google.")
				|| name.startsWith("net.rubyeye.")
				|| name.startsWith("com.rabbitmq.")
				|| name.startsWith("com.esotericsoftware.")
				|| name.startsWith("com.zaxxer.")
				|| name.startsWith("support.")
				|| name.startsWith("com.oracle.")
				|| name.startsWith("com.sun.") || name.startsWith("jdk.")
				|| name.startsWith("org.w3c.") || name.startsWith("org.omg.")
				|| name.startsWith("org.xml.") || name.startsWith("org.jcp.")
				|| name.startsWith("org.ietf.") || name.startsWith("sun.")
				|| name.startsWith("oracle.") || name.startsWith("netscape.")
				|| name.startsWith("junit.") || name.startsWith("com.aliyun.")
				|| name.startsWith("mozilla.") || name.startsWith("org.jdom")
				|| name.startsWith("org.codehaus.")
				|| name.startsWith("com.aliyuncs.")
				|| name.startsWith("org.json") || name.startsWith("javassist.")
				|| name.startsWith("org.jboss")
				|| name.startsWith("org.I0Itec.") || name.startsWith("common.")
				|| name.startsWith("jxl.") || name.startsWith("com.mysql.")
				|| name.startsWith("google.")
				|| name.startsWith("com.corundumstudio.")
				|| name.startsWith("io.netty.")
				|| name.startsWith("org.slf4j.")
				|| name.startsWith("org.fasterxml.")
				|| name.startsWith("com.fasterxml")
				|| name.startsWith("org.objectweb.")
				|| name.startsWith("lombok.")
				|| name.startsWith("com.zwitserloot.") || name
					.startsWith("org.eclipse."));
	}
}