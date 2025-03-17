package run.soeasy.framework.beans.factory.component;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.env.EnvironmentCapable;
import run.soeasy.framework.core.scan.TypeScanner;
import run.soeasy.framework.core.type.AnnotationMetadata;
import run.soeasy.framework.util.collections.Elements;

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
