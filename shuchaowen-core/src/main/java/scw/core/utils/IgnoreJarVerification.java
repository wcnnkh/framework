package scw.core.utils;

import scw.core.Verification;

public class IgnoreJarVerification implements Verification<String> {

	public boolean verification(String name) {
		if (name.startsWith("plexus-") || name.startsWith("junit-") || name.startsWith("aliyun-")
				|| name.startsWith("httpclient-") || name.startsWith("httpcore-") || name.startsWith("commons-")
				|| name.startsWith("jdom-") || name.startsWith("jersey-") || name.startsWith("jettison-")
				|| name.startsWith("stax-") || name.startsWith("jaxb-") || name.startsWith("stax-")
				|| name.startsWith("activation-") || name.startsWith("jackson-") || name.startsWith("json-")
				|| name.startsWith("HikariCP-") || name.startsWith("freemarker-") || name.startsWith("dubbo-")
				|| name.startsWith("javassit-") || name.startsWith("netty-") || name.startsWith("zkclient-")
				|| name.startsWith("zookeeper-") || name.startsWith("log4j-") || name.startsWith("jxl-")
				|| name.startsWith("jedis-") || name.startsWith("xmemcached-") || name.startsWith("fastjson-")
				|| name.startsWith("amqp-") || name.startsWith("druid-") || name.startsWith("mysql-")
				|| name.startsWith("protobuf-") || name.startsWith("javax.") || name.startsWith("jstl-")
				|| name.startsWith("jsp-") || name.startsWith("slf4j-") || name.startsWith("javassist-")
				|| name.startsWith("tomcat-") || name.startsWith("lombok-") || name.startsWith("resources.jar")
				|| name.startsWith("rt.jar") || name.startsWith("jsse.jar") || name.startsWith("jce.jar")
				|| name.startsWith("charsets.jar") || name.startsWith("jfr.jar") || name.startsWith("access-bridge-")
				|| name.startsWith("cldrdata.jar") || name.startsWith("dnsns.jar") || name.startsWith("jaccess.jar")
				|| name.startsWith("jfxrt.jar") || name.startsWith("localedate.jar") || name.startsWith("nashorn.jar")
				|| name.startsWith("sunec.jar") || name.startsWith("zipfs.jar") || name.startsWith("sunpkcs11.jar")
				|| name.startsWith("sunmscapi.jar") || name.startsWith("sunjce_provider.jar")) {
			return false;
		}
		return true;
	}
}
