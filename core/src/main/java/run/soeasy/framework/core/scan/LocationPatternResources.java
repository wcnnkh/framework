package run.soeasy.framework.core.scan;

import java.io.IOException;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.collections.ServiceLoader.ReloadableElementsWrapper;
import run.soeasy.framework.util.io.Resource;
import run.soeasy.framework.util.io.load.ResourcePatternResolver;
import run.soeasy.framework.util.logging.LogManager;
import run.soeasy.framework.util.logging.Logger;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class LocationPatternResources implements ReloadableElementsWrapper<Resource, Elements<Resource>> {
	private static Logger logger = LogManager.getLogger(LocationPatternResources.class);
	@NonNull
	private final ResourcePatternResolver resourcePatternResolver;
	@NonNull
	private final Supplier<String> locationPatternSupplier;
	private ResourceFilter resourceFilter;

	@Override
	public void reload() {
	}

	@Override
	public Elements<Resource> getSource() {
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
