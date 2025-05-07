package run.soeasy.framework.core.convert.transform;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypedValueAccessor;

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
	public static enum Mode {
		ITERATIVE, MAP
	}

	@NonNull
	private ConversionService conversionService = ConversionService.identity();
	@NonNull
	private Mode mode = Mode.MAP;

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

		Object value = conversionService.apply(sourceContext.getKeyValue().getValue(),
				targetContext.getKeyValue().getValue());
		if (value == null && targetContext.getKeyValue().getValue().isRequired()) {
			return false;
		}

		targetContext.getKeyValue().getValue().set(value);
		return true;
	}

	@Override
	public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext) {
		if (sourceContext.hasKeyValue() && targetContext.hasKeyValue()) {
			return setValue(sourceContext, targetContext);
		} else if (sourceContext.hasMapping() && targetContext.hasMapping()) {
			if (mode == Mode.ITERATIVE) {
				return iterativeMode(sourceContext, targetContext) > 0;
			} else if (mode == Mode.MAP) {
				return mapMode(sourceContext, targetContext) > 0;
			}
		}
		return false;
	}
}
