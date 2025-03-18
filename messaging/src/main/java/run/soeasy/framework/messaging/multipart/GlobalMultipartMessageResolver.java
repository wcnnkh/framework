package run.soeasy.framework.messaging.multipart;

public class GlobalMultipartMessageResolver extends ConfigurableMultipartMessageResolver {
	private static volatile GlobalMultipartMessageResolver instance;

	public static GlobalMultipartMessageResolver getInstance() {
		if (instance == null) {
			synchronized (GlobalMultipartMessageResolver.class) {
				if (instance == null) {
					instance = new GlobalMultipartMessageResolver();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}
}
