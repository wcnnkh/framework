package scw.application;

public final class TomcatApplication {
	public synchronized static void run(final Class<?> clazz, String beanXml) {
		EmbeddedApplication.run(clazz, beanXml);
	}

	public static void run(Class<?> clazz) {
		EmbeddedApplication.run(clazz);
	}
	
	public static void run(String beanXml){
		EmbeddedApplication.run(null, beanXml);
	}

	/**
	 * 推荐使用run(java.lang.Class clazz)方法
	 */
	public static void run() {
		EmbeddedApplication.run(null);
	}
}
