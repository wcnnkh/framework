package run.soeasy.framework.core.transform.templates;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 对值进行映射
 * 
 * @author soeasy.run
 *
 * @param <K>
 * @param <V>
 * @param <T>
 */
@Getter
@Setter
public class ValueMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> implements Mapper<K, V, T> {
	@NonNull
	private Converter converter = Converter.assignable();

	public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext) {
		if (!(sourceContext.hasKeyValue() && targetContext.hasKeyValue())) {
			return false;
		}

		TypedValueAccessor sourceAccessor = sourceContext.getKeyValue().getValue();
		TypedValueAccessor targetAccessor = targetContext.getKeyValue().getValue();
		if (!converter.canConvert(sourceAccessor.getReturnTypeDescriptor(),
				targetAccessor.getRequiredTypeDescriptor())) {
			return false;
		}

		Object value = converter.convert(sourceAccessor.get(), sourceAccessor.getReturnTypeDescriptor(),
				targetAccessor.getRequiredTypeDescriptor());
		if (value == null && targetAccessor.isRequired()) {
			return false;
		}

		targetAccessor.set(value);
		return true;
	}
}
