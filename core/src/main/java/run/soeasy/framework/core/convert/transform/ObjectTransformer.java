package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypedValueAccessor;

public class ObjectTransformer<K, V extends TypedValueAccessor, T extends Mapping<K, V>>
		extends DefaultTransformer<K, V, T, Object, Object> {
	private final MappingRegistry<Object, K, V, T> objectMappingRegistry = new MappingRegistry<>();

	@Override
	protected T getSourceMapping(@NonNull Object source, @NonNull TypeDescriptor sourceType) {
		T template = objectMappingRegistry.getMapping(source, sourceType);
		if (template == null) {
			template = super.getSourceMapping(source, sourceType);
		}
		return template;
	}

	@Override
	protected T getTargetMapping(@NonNull Object target, @NonNull TypeDescriptor targetType) {
		T template = objectMappingRegistry.getMapping(target, targetType);
		if (template == null) {
			template = super.getTargetMapping(target, targetType);
		}
		return template;
	}

	@Override
	protected boolean hasSourceMapping(@NonNull TypeDescriptor requiredType) {
		return objectMappingRegistry.hasMapping(requiredType) || super.hasSourceMapping(requiredType);
	}

	@Override
	protected boolean hasTargetMapping(@NonNull TypeDescriptor requiredType) {
		return objectMappingRegistry.hasMapping(requiredType) || super.hasTargetMapping(requiredType);
	}
}
