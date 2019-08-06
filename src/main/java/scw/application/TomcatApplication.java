package scw.application;

public final class TomcatApplication {
	public synchronized static void run(final Class<?> clazz, String beanXml) {
		EmbeddedApplication.run(clazz, beanXml);
	}

	public static void run(Class<?> clazz) {
		EmbeddedApplication.run(clazz);
	}

	public static void run() {
		EmbeddedApplication.run(null);
	}
}
