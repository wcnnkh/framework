package run.soeasy.framework.core.convert.number;

import java.util.EnumSet;
import java.util.NoSuchElementException;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NumberToEnumConverter implements ReversibleConverter<Enum, Number> {
	public static final NumberToEnumConverter DEFAULT = new NumberToEnumConverter();

	@Override
	public Number to(Enum source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return source == null ? null : source.ordinal();
	}

	public static <T extends Enum<T>> T ordinal(Class<T> enumType, int ordinal) {
		EnumSet<T> enumSet = EnumSet.noneOf(enumType);
		for (T e : enumSet) {
			if (e.ordinal() == ordinal) {
				return e;
			}
		}
		throw new NoSuchElementException(enumType + "[" + ordinal + "]");
	}

	@Override
	public Enum from(Number source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		if (source == null) {
			return null;
		}

		Class<Enum> enumType = (Class<Enum>) targetTypeDescriptor.getType();
		return ordinal(enumType, source.intValue());
	}
}
