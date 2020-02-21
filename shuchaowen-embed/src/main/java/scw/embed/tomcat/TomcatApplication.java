package scw.embed.tomcat;

import scw.embed.servlet.ServletEmbeddedApplication;

public final class TomcatApplication {
	public static void run(Class<?> clazz, String beanXml) {
		ServletEmbeddedApplication.run(clazz, beanXml);
	}

	public static void run(Class<?> clazz) {
		ServletEmbeddedApplication.run(clazz);
	}
}
