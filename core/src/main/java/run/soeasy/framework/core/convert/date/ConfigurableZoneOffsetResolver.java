package run.soeasy.framework.core.convert.date;

import java.time.ZoneOffset;

import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.util.spi.ConfigurableServices;

public class ConfigurableZoneOffsetResolver extends ConfigurableServices<ZoneOffsetResolver>
		implements ZoneOffsetResolver {
	private static volatile ConfigurableZoneOffsetResolver instance;

	public static ConfigurableZoneOffsetResolver getInstance() {
		if (instance == null) {
			synchronized (ConfigurableZoneOffsetResolver.class) {
				if (instance == null) {
					instance = new ConfigurableZoneOffsetResolver();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	public ConfigurableZoneOffsetResolver() {
		setServiceClass(ZoneOffsetResolver.class);
	}

	@Override
	public ZoneOffset resolveZoneOffset(SourceDescriptor valueDescriptor) {
		for (ZoneOffsetResolver resolver : this) {
			ZoneOffset zoneOffset = resolver.resolveZoneOffset(valueDescriptor);
			if (zoneOffset != null) {
				return zoneOffset;
			}
		}
		return null;
	}

}
