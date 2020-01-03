package scw.embed;

public final class TomcatApplication {
	public static void run(Class<?> clazz, String beanXml) {
		EmbeddedApplication.run(clazz, beanXml);
	}

	public static void run(Class<?> clazz) {
		EmbeddedApplication.run(clazz);
	}
}
