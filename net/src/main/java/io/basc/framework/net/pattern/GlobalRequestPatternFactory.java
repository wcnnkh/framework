package io.basc.framework.net.pattern;

public class GlobalRequestPatternFactory extends ConfigurableRequestPatternFactory {
	private static volatile GlobalRequestPatternFactory instance;

	public static GlobalRequestPatternFactory getInstance() {
		if (instance == null) {
			synchronized (GlobalRequestPatternFactory.class) {
				if (instance == null) {
					instance = new GlobalRequestPatternFactory();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	private GlobalRequestPatternFactory() {
	}
}
