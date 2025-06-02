package run.soeasy.framework.core.transform.lang;

import java.util.Collection;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;

@RequiredArgsConstructor
@Getter
@Setter
public class CollectionTransformer extends IdentityTransformer<Collection<Object>> {
	@NonNull
	private ConversionService elementConversionService;

	@Override
	public boolean transform(@NonNull Collection<Object> source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Collection<Object> target, @NonNull TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		TypeDescriptor sourceElementTypeDescriptor = sourceTypeDescriptor.getElementTypeDescriptor();
		TypeDescriptor targetElementTypeDescriptor = targetTypeDescriptor.getElementTypeDescriptor();
		for (Object element : source) {
			Object value = elementConversionService.convert(element, sourceElementTypeDescriptor,
					targetElementTypeDescriptor);
			target.add(value);
		}
		return true;
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return super.canTransform(sourceTypeDescriptor, targetTypeDescriptor) && sourceTypeDescriptor.isCollection()
				&& targetTypeDescriptor.isCollection() && sourceTypeDescriptor.getName().startsWith("java.");
	}
}
