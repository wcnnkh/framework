package io.basc.framework.convert.strings;

import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.config.support.DefaultReversibleConverterRegistry;
import io.basc.framework.convert.lang.ResourceToString;
import io.basc.framework.util.Optional;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StringConverter extends DefaultReversibleConverterRegistry<String, ConversionException> {
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

	private ReaderConverter readerConverter = new ReaderConverter();
	private EnumConverter enumConverter = new EnumConverter();
	private CharsetConverter charsetConverter = new CharsetConverter();
	private ClassConverter classConverter = new ClassConverter();
	private TimeZoneConverter timeZoneConverter = new TimeZoneConverter();
	private CurrencyConverter currencyConverter = new CurrencyConverter();
	private LocaleConverter localeConverter = new LocaleConverter();
	private BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();
	private BigIntegerConverter bigIntegerConverter = new BigIntegerConverter();

	// 基本类型
	private CharacterConverter characterConverter = new CharacterConverter();
	private BooleanConverter booleanConverter = new BooleanConverter();
	private DoubleConverter doubleConverter = new DoubleConverter();
	private ByteConverter byteConverter = new ByteConverter();
	private FloatConverter floatConverter = new FloatConverter();
	private ShortConverter shortConverter = new ShortConverter();
	private IntegerConverter integerConverter = new IntegerConverter();
	private LongConverter longConverter = new LongConverter();

	private ResourceToString resourceToString = ResourceToString.DEFAULT;

	public StringConverter() {
		registerReversibleConverter(Reader.class, readerConverter);
		registerReversibleConverter(Enum.class, enumConverter);
		registerReversibleConverter(Charset.class, charsetConverter);
		registerReversibleConverter(Class.class, classConverter);
		registerReversibleConverter(TimeZone.class, timeZoneConverter);
		registerReversibleConverter(Currency.class, currencyConverter);
		registerReversibleConverter(Locale.class, localeConverter);
		registerReversibleConverter(BigDecimal.class, bigDecimalConverter);
		registerReversibleConverter(BigInteger.class, bigIntegerConverter);

		registerReversibleConverter(char.class, characterConverter);
		registerReversibleConverter(Character.class, characterConverter);
		registerReversibleConverter(boolean.class, booleanConverter);
		registerReversibleConverter(Boolean.class, booleanConverter);
		registerReversibleConverter(double.class, doubleConverter);
		registerReversibleConverter(Double.class, doubleConverter);
		registerReversibleConverter(byte.class, byteConverter);
		registerReversibleConverter(Byte.class, byteConverter);
		registerReversibleConverter(float.class, floatConverter);
		registerReversibleConverter(Float.class, floatConverter);
		registerReversibleConverter(short.class, shortConverter);
		registerReversibleConverter(Short.class, shortConverter);
		registerReversibleConverter(int.class, integerConverter);
		registerReversibleConverter(Integer.class, integerConverter);
		registerReversibleConverter(long.class, longConverter);
		registerReversibleConverter(Long.class, longConverter);
	}
}
