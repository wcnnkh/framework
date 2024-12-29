package io.basc.framework.core.convert.date;

import java.time.ZoneOffset;

import io.basc.framework.core.convert.ValueDescriptor;
import io.basc.framework.util.spi.ConfigurableServices;

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
	public ZoneOffset resolveZoneOffset(ValueDescriptor valueDescriptor) {
		for (ZoneOffsetResolver resolver : this) {
			ZoneOffset zoneOffset = resolver.resolveZoneOffset(valueDescriptor);
			if (zoneOffset != null) {
				return zoneOffset;
			}
		}
		return null;
	}

}
