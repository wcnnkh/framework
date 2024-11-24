package io.basc.framework.beans.factory.component;

import io.basc.framework.core.env.EnvironmentCapable;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.io.scan.TypeScanner;
import io.basc.framework.util.Elements;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LocationPatternComponentRegistryPostProcessor extends ComponentScanRegistryPostProcessor {
	@NonNull
	private final Elements<String> locationPatterns;

	@Override
	protected Elements<AnnotationMetadata> scan(EnvironmentCapable context, TypeScanner typeScanner) {
		return locationPatterns.flatMap((locationPattern) -> typeScanner.scan(locationPattern))
				.map((e) -> e.getAnnotationMetadata());
	}
}
