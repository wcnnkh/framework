package run.soeasy.framework.core.convert.date;

import java.util.Date;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.convert.value.Readable;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class ConfigurableDateCodecResolver extends ConfigurableServices<DateCodecResolver>
		implements DateCodecResolver {
	private static volatile ConfigurableDateCodecResolver instance;

	public static ConfigurableDateCodecResolver getInstance() {
		if (instance == null) {
			synchronized (ConfigurableDateCodecResolver.class) {
				if (instance == null) {
					instance = new ConfigurableDateCodecResolver();
					instance.configure();
				}
			}
		}
		return instance;
	}

	public ConfigurableDateCodecResolver() {
		setServiceClass(DateCodecResolver.class);
	}

	@Override
	public Codec<Date, String> resolveDateCodec(@NonNull Readable valueDescriptor) {
		for (DateCodecResolver resolver : this) {
			Codec<Date, String> codec = resolver.resolveDateCodec(valueDescriptor);
			if (codec != null) {
				return codec;
			}
		}
		return null;
	}

}
