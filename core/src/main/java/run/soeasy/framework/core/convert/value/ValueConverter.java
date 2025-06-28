package run.soeasy.framework.core.convert.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import run.soeasy.framework.core.convert.AbstractConditionalConverter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.Value;
import run.soeasy.framework.core.domain.Version;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ValueConverter extends AbstractConditionalConverter implements ReversibleConverter<Object, Value> {
	public static final ValueConverter DEFAULT = new ValueConverter();
	private static Map<Class<?>, BiFunction<? super Value, ? super TypeDescriptor, ? extends Object>> typeMap = new HashMap<>();

	static {
		typeMap.put(String.class, (a, b) -> a.getAsString());
		typeMap.put(BigInteger.class, (a, b) -> a.getAsBigInteger());
		typeMap.put(BigDecimal.class, (a, b) -> a.getAsBigDecimal());
		typeMap.put(Boolean.class, (a, b) -> a.getAsBoolean());
		typeMap.put(Character.class, (a, b) -> a.getAsChar());
		typeMap.put(Enum.class, (a, b) -> a.getAsEnum((Class<Enum>)b.getType()));
		typeMap.put(Version.class, (a, b) -> a.getAsVersion());
		typeMap.put(Number.class, (a, b) -> a.getAsNumber());
		typeMap.put(Byte.class, (a, b) -> a.getAsByte());
		typeMap.put(Short.class, (a, b) -> a.getAsShort());
		typeMap.put(Integer.class, (a, b) -> a.getAsInt());
		typeMap.put(Long.class, (a, b) -> a.getAsLong());
		typeMap.put(Float.class, (a, b) -> a.getAsFloat());
		typeMap.put(Double.class, (a, b) -> a.getAsDouble());
	}

	public static boolean isValueType(Class<?> type) {
		return typeMap.containsKey(type);
	}

	@Override
	public Value to(Object source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		if(source instanceof Value) {
			return (Value) source;
		}
		
		CustomizeTypedValueAccessor accessor = new CustomizeTypedValueAccessor();
		accessor.setTypeDescriptor(sourceTypeDescriptor);
		accessor.setValue(source);
		return accessor.map(targetTypeDescriptor, getConverter());
	}

	@Override
	public Object from(Value source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		BiFunction<? super Value, ? super TypeDescriptor, ? extends Object> getter = typeMap
				.get(targetTypeDescriptor.getType());
		if (getter != null) {
			return getter.apply(source, targetTypeDescriptor);
		}

		if (source instanceof TypedValue) {
			return ((TypedValue) source).map(targetTypeDescriptor, getConverter()).get();
		}
		throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
	}

}
