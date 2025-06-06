package run.soeasy.framework.core.transform.collection;

import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.IdentityTransformer;

@RequiredArgsConstructor
@Getter
@Setter
public class MapTransformer implements IdentityTransformer<Map<Object, Object>> {
	@NonNull
	private ConversionService keyConversionService;
	@NonNull
	private ConversionService valueConversionService;

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return sourceTypeDescriptor.isMap() && targetTypeDescriptor.isMap();
	}

	@Override
	public boolean transform(@NonNull Map<Object, Object> source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Map<Object, Object> target, @NonNull TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		TypeDescriptor sourceKeyTypeDescriptor = sourceTypeDescriptor.getMapKeyTypeDescriptor();
		TypeDescriptor targetKeyTypeDescriptor = targetTypeDescriptor.getMapKeyTypeDescriptor();
		TypeDescriptor sourceValueTypeDescriptor = sourceTypeDescriptor.getMapValueTypeDescriptor();
		TypeDescriptor targetValueTypeDescriptor = targetTypeDescriptor.getMapValueTypeDescriptor();
		for (Entry<Object, Object> entry : source.entrySet()) {
			Object key = keyConversionService.convert(entry.getKey(), sourceKeyTypeDescriptor, targetKeyTypeDescriptor);
			Object value = valueConversionService.convert(entry.getValue(), sourceValueTypeDescriptor,
					targetValueTypeDescriptor);
			target.put(key, value);
		}
		return true;
	}

}
