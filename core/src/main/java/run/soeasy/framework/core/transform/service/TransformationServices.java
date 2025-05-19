package run.soeasy.framework.core.transform.service;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.spi.ServiceProvider;

@Getter
@Setter
public class TransformationServices extends ServiceProvider<TransformationService, ConversionException>
		implements TransformationService {
	private TransformationService parentTransformationService;

	public TransformationServices() {
		setServiceClass(TransformationService.class);
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceType, @NonNull Object target,
			@NonNull TypeDescriptor targetType) throws ConversionException {
		TransformationService service = optional().filter((e) -> e.canTransform(sourceType, targetType)).orElse(null);
		if (service == null) {
			if (parentTransformationService != null
					&& parentTransformationService.canTransform(sourceType, targetType)) {
				return parentTransformationService.transform(source, sourceType, target, targetType);
			}
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		return service.transform(source, sourceType, target, targetType);
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return optional().filter((e) -> e.canTransform(sourceType, targetType)).isPresent()
				|| (parentTransformationService != null
						&& parentTransformationService.canTransform(sourceType, targetType));
	}

}
