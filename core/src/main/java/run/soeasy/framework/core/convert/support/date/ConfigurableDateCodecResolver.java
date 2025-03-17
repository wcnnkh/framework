package run.soeasy.framework.core.convert.support.date;

import java.util.Date;

import lombok.NonNull;
import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.util.codec.Codec;
import run.soeasy.framework.util.spi.ConfigurableServices;

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
