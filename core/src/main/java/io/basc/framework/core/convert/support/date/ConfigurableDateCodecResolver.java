package io.basc.framework.core.convert.support.date;

import java.util.Date;

import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.spi.ConfigurableServices;
import lombok.NonNull;

public class ConfigurableDateCodecResolver extends ConfigurableServices<DateCodecResolver>
		implements DateCodecResolver {
	private static volatile ConfigurableDateCodecResolver instance;

	public static ConfigurableDateCodecResolver getInstance() {
		if (instance == null) {
			synchronized (ConfigurableDateCodecResolver.class) {
				if (instance == null) {
					instance = new ConfigurableDateCodecResolver();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	public ConfigurableDateCodecResolver() {
		setServiceClass(DateCodecResolver.class);
	}

	@Override
	public Codec<Date, String> resolveDateCodec(@NonNull SourceDescriptor valueDescriptor) {
		for (DateCodecResolver resolver : this) {
			Codec<Date, String> codec = resolver.resolveDateCodec(valueDescriptor);
			if (codec != null) {
				return codec;
			}
		}
		return null;
	}

}
