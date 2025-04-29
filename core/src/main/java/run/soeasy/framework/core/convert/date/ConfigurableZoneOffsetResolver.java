package run.soeasy.framework.core.convert.date;

import java.time.ZoneOffset;

import run.soeasy.framework.core.convert.Readable;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class ConfigurableZoneOffsetResolver extends ConfigurableServices<ZoneOffsetResolver>
		implements ZoneOffsetResolver {
	private static volatile ConfigurableZoneOffsetResolver instance;

	public static ConfigurableZoneOffsetResolver getInstance() {
		if (instance == null) {
			synchronized (ConfigurableZoneOffsetResolver.class) {
				if (instance == null) {
					instance = new ConfigurableZoneOffsetResolver();
					instance.configure();
				}
			}
		}
		return instance;
	}

	public ConfigurableZoneOffsetResolver() {
		setServiceClass(ZoneOffsetResolver.class);
	}

	@Override
	public ZoneOffset resolveZoneOffset(Readable valueDescriptor) {
		for (ZoneOffsetResolver resolver : this) {
			ZoneOffset zoneOffset = resolver.resolveZoneOffset(valueDescriptor);
			if (zoneOffset != null) {
				return zoneOffset;
			}
		}
		return null;
	}

}
