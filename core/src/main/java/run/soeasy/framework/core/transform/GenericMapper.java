package run.soeasy.framework.core.transform;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 基础的模板写入实现
 * 
 * @author soeasy.run
 *
 * @param <K>
 * @param <V>
 * @param <T>
 */
@Getter
@Setter
public class GenericMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> implements Mapper<K, V, T> {
	@NonNull
	private ConversionService conversionService = ConversionService.identity();

	protected int iterativeMode(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext) {
		List<KeyValue<K, V>> sourceList = sourceContext.getMapping().getElements().collect(Collectors.toList());
		if (sourceList.isEmpty()) {
			return 0;
		}

		int count = 0;
		for (KeyValue<K, V> target : targetContext.getMapping().getElements()) {
			Iterator<KeyValue<K, V>> sourceIterator = sourceList.iterator();
			while (sourceIterator.hasNext()) {
				KeyValue<K, V> source = sourceIterator.next();
				if (setValue(sourceContext.current(source), targetContext.nested(target))) {
					sourceIterator.remove();
					count++;
				}
			}
		}
		return count;
	}

	protected int mapMode(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext) {
		int count = 0;
		for (KeyValue<K, V> target : targetContext.getMapping().getElements()) {
			Elements<V> sourceElements = sourceContext.getMapping().getValues(target.getKey());
			for (V value : sourceElements) {
				if (setValue(sourceContext.current(KeyValue.of(target.getKey(), value)),
						targetContext.current(target))) {
					count++;
				}
			}
		}
		return count;
	}

	protected boolean setValue(MappingContext<K, V, T> sourceContext, MappingContext<K, V, T> targetContext) {
		if (!conversionService.canConvert(sourceContext.getKeyValue().getValue().getReturnTypeDescriptor(),
				targetContext.getKeyValue().getValue().getRequiredTypeDescriptor())) {
			return false;
		}

		TypedValueAccessor sourceAccessor = sourceContext.getKeyValue().getValue();
		TypedValueAccessor targetAccessor = targetContext.getKeyValue().getValue();
		Object value = conversionService.convert(sourceAccessor.get(), sourceAccessor.getReturnTypeDescriptor(),
				targetAccessor.getRequiredTypeDescriptor());
		if (value == null && targetAccessor.isRequired()) {
			return false;
		}

		targetAccessor.set(value);
		return true;
	}

	public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext) {
		if (sourceContext.hasKeyValue() && targetContext.hasKeyValue()) {
			return setValue(sourceContext, targetContext);
		} else if (sourceContext.hasMapping() && targetContext.hasMapping()) {
			if (sourceContext.getMapping().isRandomAccess()) {
				return mapMode(sourceContext, targetContext) > 0;
			} else {
				return iterativeMode(sourceContext, targetContext) > 0;
			}
		}
		return false;
	}
}
