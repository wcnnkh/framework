package io.basc.framework.beans.factory.component;

import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.env.EnvironmentCapable;
import io.basc.framework.io.scan.ClassScanner;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationComponentRegistryPostProcessor extends ComponentScanRegistryPostProcessor {
	private final String location;

	public LocationComponentRegistryPostProcessor(String location) {
		Assert.requiredArgument(location != null, "location");
		this.location = location;
	}

	@Override
	protected Elements<AnnotationMetadata> scan(EnvironmentCapable context, ClassScanner classScanner) {
		return classScanner.scanLocation(location);
	}
}
