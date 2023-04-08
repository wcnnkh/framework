package io.basc.framework.convert.lang;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionFactory;
import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.io.Resource;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Optional;
import io.basc.framework.util.StringToBoolean;
import io.basc.framework.util.StringToByte;
import io.basc.framework.util.StringToCharacter;
import io.basc.framework.util.StringToCharset;
import io.basc.framework.util.StringToClass;
import io.basc.framework.util.StringToCurrency;
import io.basc.framework.util.StringToDouble;
import io.basc.framework.util.StringToFloat;
import io.basc.framework.util.StringToInteger;
import io.basc.framework.util.StringToLocale;
import io.basc.framework.util.StringToLong;
import io.basc.framework.util.StringToNumber;
import io.basc.framework.util.StringToShort;
import io.basc.framework.util.StringToTimeZone;

public class StringConverter extends ConversionFactory<String, ConversionException> {
	public static final StringConverter DEFAULT = new StringConverter();

	public static <T> Optional<T> parse(String source, Class<T> type) {
		T value = DEFAULT.convert(source, type);
		return Optional.of(value);
	}

	public static boolean parseBoolean(String source) {
		return DEFAULT.convert(source, boolean.class);
	}

	public static boolean parseBoolean(String source, boolean defaultValue) {
		Boolean value = DEFAULT.convert(source, Boolean.class);
		return value == null ? defaultValue : value.booleanValue();
	}

	public static byte parseByte(String source) {
		return DEFAULT.convert(source, byte.class);
	}

	public static byte parseByte(String source, byte defaultValue) {
		Byte value = DEFAULT.convert(source, Byte.class);
		return value == null ? defaultValue : value.byteValue();
	}

	public static char parseChar(String source) {
		return DEFAULT.convert(source, char.class);
	}

	public static double parseDouble(String source) {
		return DEFAULT.convert(source, double.class);
	}

	public static double parseDouble(String source, double defaultValue) {
		Double value = DEFAULT.convert(source, Double.class);
		return value == null ? defaultValue : value.doubleValue();
	}

	public static float parseFloat(String source) {
		return DEFAULT.convert(source, float.class);
	}

	public static float parseFloat(String source, float defaultValue) {
		Float value = DEFAULT.convert(source, Float.class);
		return value == null ? defaultValue : value.floatValue();
	}

	public static int parseInt(String source) {
		return DEFAULT.convert(source, int.class);
	}

	public static int parseInt(String source, int defaultValue) {
		Integer value = DEFAULT.convert(source, Integer.class);
		return value == null ? defaultValue : value.intValue();
	}

	public static long parseLong(String source) {
		return DEFAULT.convert(source, long.class);
	}

	public static long parseLong(String source, long defaultValue) {
		Long value = DEFAULT.convert(source, Long.class);
		return value == null ? defaultValue : value.longValue();
	}

	public static short parseShort(String source) {
		return DEFAULT.convert(source, short.class);
	}

	public static short parseShort(String source, short defaultValue) {
		Short value = DEFAULT.convert(source, Short.class);
		return value == null ? defaultValue : value.shortValue();
	}

	private ObjectToString objectToString = ObjectToString.DEFAULT;
	private ReaderToString readerToString = ReaderToString.DEFAULT;
	private ResourceToString resourceToString = ResourceToString.DEFAULT;

	private StringToBoolean stringToBoolean = StringToBoolean.DEFAULT;
	private StringToByte stringToByte = StringToByte.DEFAULT;
	private StringToCharacter stringToCharacter = StringToCharacter.DEFAULT;
	private StringToCharset stringToCharset = StringToCharset.DEFAULT;
	private StringToClass stringToClass = StringToClass.DEFAULT;
	private StringToCurrency stringToCurrency = StringToCurrency.DEFAULT;
	private StringToDouble stringToDouble = StringToDouble.DEFAULT;
	private StringToEnum stringToEnum = StringToEnum.DEFAULT;
	private StringToFloat stringToFloat = StringToFloat.DEFAULT;
	private StringToInteger stringToInteger = StringToInteger.DEFAULT;
	private StringToLocale stringToLocale = StringToLocale.DEFAULT;
	private StringToLong stringToLong = StringToLong.DEFAULT;
	private StringToNumber stringToNumber = StringToNumber.DEFAULT;
	private StringToShort stringToShort = StringToShort.DEFAULT;
	private StringToTimeZone stringToTimeZone = StringToTimeZone.DEFAULT;

	public StringConverter() {
		registerConverter(char.class, (source, sourceType, targetType) -> stringToCharacter.applyAsChar(source));
		registerConverter(Character.class, (source, sourceType, targetType) -> stringToCharacter.apply(source));
		registerConverter(boolean.class, (source, sourceType, targetType) -> stringToBoolean.applyAsBoolean(source));
		registerConverter(Boolean.class, (source, sourceType, targetType) -> stringToBoolean.apply(source));
		registerConverter(byte.class, (source, sourceType, targetType) -> stringToByte.applyAsByte(source));
		registerConverter(Byte.class, (source, sourceType, targetType) -> stringToByte.apply(source));
		registerConverter(short.class, (source, sourceType, targetType) -> stringToShort.applyAsShort(source));
		registerConverter(Short.class, (source, sourceType, targetType) -> stringToShort.apply(source));
		registerConverter(int.class, (source, sourceType, targetType) -> stringToInteger.applyAsInt(source));
		registerConverter(Integer.class, (source, sourceType, targetType) -> stringToInteger.apply(source));
		registerConverter(long.class, (source, sourceType, targetType) -> stringToLong.applyAsLong(source));
		registerConverter(Long.class, (source, sourceType, targetType) -> stringToLong.apply(source));
		registerConverter(float.class, (source, sourceType, targetType) -> stringToFloat.applyAsFloat(source));
		registerConverter(Float.class, (source, sourceType, targetType) -> stringToFloat.apply(source));
		registerConverter(double.class, (source, sourceType, targetType) -> stringToDouble.applyAsDouble(source));
		registerConverter(Double.class, (source, sourceType, targetType) -> stringToDouble.apply(source));
		registerConverter(Enum.class, stringToEnum);
		registerConverter(Number.class, (source, sourceType, targetType) -> stringToNumber.apply(source));
		registerConverter(Locale.class, (source, sourceType, targetType) -> stringToLocale.apply(source));
		registerConverter(Charset.class, (source, sourceType, targetType) -> stringToCharset.apply(source));
		registerConverter(Currency.class, (source, sourceType, targetType) -> stringToCurrency.apply(source));
		registerConverter(TimeZone.class, (source, sourceType, targetType) -> stringToTimeZone.apply(source));
		registerConverter(Class.class, (source, sourceType, targetType) -> stringToClass.apply(source));

		registerInverter(Reader.class, (source, sourceType, targetType) -> {
			try {
				return readerToString.convert(source, sourceType, targetType);
			} catch (IOException e) {
				throw new ConversionFailedException(sourceType, targetType, source, e);
			}
		});

		registerInverter(Resource.class, (source, sourceType, targetType) -> {
			try {
				return resourceToString.convert(source, sourceType, targetType);
			} catch (IOException e) {
				throw new ConversionFailedException(sourceType, targetType, source, e);
			}
		});

		registerInverter(Object.class, objectToString::convert);
	}

	public ObjectToString getObjectToString() {
		return objectToString;
	}

	public ReaderToString getReaderToString() {
		return readerToString;
	}

	public ResourceToString getResourceToString() {
		return resourceToString;
	}

	public StringToBoolean getStringToBoolean() {
		return stringToBoolean;
	}

	public StringToByte getStringToByte() {
		return stringToByte;
	}

	public StringToCharacter getStringToCharacter() {
		return stringToCharacter;
	}

	public StringToCharset getStringToCharset() {
		return stringToCharset;
	}

	public StringToClass getStringToClass() {
		return stringToClass;
	}

	public StringToCurrency getStringToCurrency() {
		return stringToCurrency;
	}

	public StringToDouble getStringToDouble() {
		return stringToDouble;
	}

	public StringToEnum getStringToEnum() {
		return stringToEnum;
	}

	public StringToFloat getStringToFloat() {
		return stringToFloat;
	}

	public StringToInteger getStringToInteger() {
		return stringToInteger;
	}

	public StringToLocale getStringToLocale() {
		return stringToLocale;
	}

	public StringToLong getStringToLong() {
		return stringToLong;
	}

	public StringToNumber getStringToNumber() {
		return stringToNumber;
	}

	public StringToShort getStringToShort() {
		return stringToShort;
	}

	public StringToTimeZone getStringToTimeZone() {
		return stringToTimeZone;
	}

	public void setObjectToString(ObjectToString objectToString) {
		Assert.requiredArgument(objectToString != null, "objectToString");
		this.objectToString = objectToString;
	}

	public void setReaderToString(ReaderToString readerToString) {
		Assert.requiredArgument(readerToString != null, "readerToString");
		this.readerToString = readerToString;
	}

	public void setResourceToString(ResourceToString resourceToString) {
		Assert.requiredArgument(resourceToString != null, "resourceToString");
		this.resourceToString = resourceToString;
	}

	public void setStringToBoolean(StringToBoolean stringToBoolean) {
		Assert.requiredArgument(stringToBoolean != null, "stringToBoolean");
		this.stringToBoolean = stringToBoolean;
	}

	public void setStringToByte(StringToByte stringToByte) {
		Assert.requiredArgument(stringToByte != null, "stringToByte");
		this.stringToByte = stringToByte;
	}

	public void setStringToCharacter(StringToCharacter stringToCharacter) {
		Assert.requiredArgument(stringToCharacter != null, "stringToCharacter");
		this.stringToCharacter = stringToCharacter;
	}

	public void setStringToCharset(StringToCharset stringToCharset) {
		Assert.requiredArgument(stringToCharset != null, "stringToCharset");
		this.stringToCharset = stringToCharset;
	}

	public void setStringToClass(StringToClass stringToClass) {
		Assert.requiredArgument(stringToClass != null, "stringToClass");
		this.stringToClass = stringToClass;
	}

	public void setStringToCurrency(StringToCurrency stringToCurrency) {
		Assert.requiredArgument(stringToCurrency != null, "stringToCurrency");
		this.stringToCurrency = stringToCurrency;
	}

	public void setStringToDouble(StringToDouble stringToDouble) {
		Assert.requiredArgument(stringToDouble != null, "stringToDouble");
		this.stringToDouble = stringToDouble;
	}

	public void setStringToEnum(StringToEnum stringToEnum) {
		Assert.requiredArgument(stringToEnum != null, "stringToEnum");
		this.stringToEnum = stringToEnum;
	}

	public void setStringToFloat(StringToFloat stringToFloat) {
		Assert.requiredArgument(stringToFloat != null, "stringToFloat");
		this.stringToFloat = stringToFloat;
	}

	public void setStringToInteger(StringToInteger stringToInteger) {
		Assert.requiredArgument(stringToInteger != null, "stringToInteger");
		this.stringToInteger = stringToInteger;
	}

	public void setStringToLocale(StringToLocale stringToLocale) {
		Assert.requiredArgument(stringToLocale != null, "stringToLocale");
		this.stringToLocale = stringToLocale;
	}

	public void setStringToLong(StringToLong stringToLong) {
		Assert.requiredArgument(stringToLong != null, "stringToLong");
		this.stringToLong = stringToLong;
	}

	public void setStringToNumber(StringToNumber stringToNumber) {
		Assert.requiredArgument(stringToNumber != null, "stringToNumber");
		this.stringToNumber = stringToNumber;
	}

	public void setStringToShort(StringToShort stringToShort) {
		Assert.requiredArgument(stringToShort != null, "stringToShort");
		this.stringToShort = stringToShort;
	}

	public void setStringToTimeZone(StringToTimeZone stringToTimeZone) {
		Assert.requiredArgument(stringToTimeZone != null, "stringToTimeZone");
		this.stringToTimeZone = stringToTimeZone;
	}
}
