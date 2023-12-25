package io.basc.framework.io.scan;

import java.io.IOException;
import java.util.function.Supplier;

import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.ServiceLoader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class LocationPatternResources implements ServiceLoader<Resource> {
	private static Logger logger = LoggerFactory.getLogger(LocationPatternResources.class);
	@NonNull
	private final ResourcePatternResolver resourcePatternResolver;
	@NonNull
	private final Supplier<String> locationPatternSupplier;

	public LocationPatternResources(ResourcePatternResolver resourcePatternResolver, String locationPattern) {
		this(resourcePatternResolver,
				Assert.requiredArgument(locationPattern != null, "locationPattern", () -> locationPattern));
	}

	@Override
	public void reload() {
	}

	@Override
	public Elements<Resource> getServices() {
		String locationPattern = locationPatternSupplier.get();
		Resource[] resources;
		try {
			long t = System.currentTimeMillis();
			resources = resourcePatternResolver.getResources(locationPattern);
			t = System.currentTimeMillis() - t;
			logger.info("Scanning {} takes time {}ms", locationPattern, t);
			return Elements.forArray(resources);
		} catch (IOException e) {
			logger.error(e, "Scan {} exception", locationPattern);
			return Elements.empty();
		}
	}

}
