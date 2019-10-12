package scw.core.resource;

import scw.core.Verification;

public class IgnoreClassNameVerification implements Verification<String> {
	public boolean verification(String name) {
		return !((name.startsWith("java.") || name.startsWith("javax.") || name.indexOf(".") == -1
				|| name.startsWith("scw.") || name.startsWith("org.apache.") || name.startsWith("freemarker.")
				|| name.startsWith("com.alibaba.") || name.startsWith("com.caucho.")
				|| name.startsWith("redis.clients.") || name.startsWith("com.google.")
				|| name.startsWith("net.rubyeye.") || name.startsWith("com.rabbitmq.")
				|| name.startsWith("com.esotericsoftware.") || name.startsWith("com.zaxxer.")
				|| name.startsWith("support.") || name.startsWith("com.oracle.") || name.startsWith("com.sun.")
				|| name.startsWith("jdk.") || name.startsWith("org.w3c.") || name.startsWith("org.omg.")
				|| name.startsWith("org.xml.") || name.startsWith("org.jcp.") || name.startsWith("org.ietf.")
				|| name.startsWith("sun.") || name.startsWith("oracle.") || name.startsWith("netscape.")
				|| name.startsWith("junit.") || name.startsWith("com.aliyun.") || name.startsWith("mozilla.")
				|| name.startsWith("org.jdom") || name.startsWith("org.codehaus.") || name.startsWith("com.aliyuncs.")
				|| name.startsWith("org.json") || name.startsWith("javassist.") || name.startsWith("org.jboss")
				|| name.startsWith("org.I0Itec.") || name.startsWith("common.") || name.startsWith("jxl.")
				|| name.startsWith("com.mysql.") || name.startsWith("google.") || name.startsWith("com.corundumstudio.")
				|| name.startsWith("io.netty.") || name.startsWith("org.slf4j.") || name.startsWith("org.fasterxml.")
				|| name.startsWith("com.fasterxml") || name.startsWith("org.objectweb.") || name.startsWith("lombok.")
				|| name.startsWith("com.zwitserloot.") || name.startsWith("org.eclipse.")));
	}
}
