package io.basc.framework.io.scan;

import java.io.IOException;
import java.util.function.Supplier;

import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.ServiceLoader;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class LocationPatternResources implements ServiceLoader<Resource> {
	private static Logger logger = LoggerFactory.getLogger(LocationPatternResources.class);
	@NonNull
	private final ResourcePatternResolver resourcePatternResolver;
	@NonNull
	private final Supplier<String> locationPatternSupplier;
	@Nullable
	private ResourceFilter resourceFilter;

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
			Elements<Resource> elements = Elements.forArray(resources);
			return resourceFilter == null ? elements : elements.filter((e) -> {
				try {
					return resourceFilter.match(e, resourcePatternResolver);
				} catch (IOException er) {
					logger.error(er, "Filter match error resource {}", e);
					return false;
				}
			});
		} catch (IOException e) {
			logger.error(e, "Scan {} exception", locationPattern);
			return Elements.empty();
		}
	}

}
