package run.soeasy.framework.core.param;

public final class SystemParameterNameDiscoverer extends ConfigurableParameterNameDiscoverer {
	private SystemParameterNameDiscoverer() {
		register(new NativeParameterNameDiscoverer());
	}

	private static SystemParameterNameDiscoverer instance;

	public static SystemParameterNameDiscoverer getInstance() {
		if (instance == null) {
			synchronized (SystemParameterNameDiscoverer.class) {
				if (instance == null) {
					instance = new SystemParameterNameDiscoverer();
					instance.configure();
				}
			}
		}
		return instance;
	}
}
