package run.soeasy.framework.core.convert.strings;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConverterRegistry;

@Setter
@Getter
public class StringConverter extends ConverterRegistry {
	private static volatile StringConverter instance;

	public static StringConverter getInstance() {
		if (instance == null) {
			synchronized (StringConverter.class) {
				if (instance == null) {
					instance = new StringConverter();
				}
			}
		}
		return instance;
	}

	@NonNull
	private EnumConverter enumConverter = new EnumConverter();
	@NonNull
	private CharsetConverter charsetConverter = new CharsetConverter();
	@NonNull
	private ClassConverter classConverter = new ClassConverter();
	@NonNull
	private TimeZoneConverter timeZoneConverter = new TimeZoneConverter();
	@NonNull
	private CurrencyConverter currencyConverter = new CurrencyConverter();
	@NonNull
	private LocaleConverter localeConverter = new LocaleConverter();
	@NonNull
	private BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();
	@NonNull
	private BigIntegerConverter bigIntegerConverter = new BigIntegerConverter();

	// 基本类型
	@NonNull
	private CharacterConverter characterConverter = new CharacterConverter();
	@NonNull
	private BooleanConverter booleanConverter = new BooleanConverter();
	@NonNull
	private DoubleConverter doubleConverter = new DoubleConverter();
	@NonNull
	private ByteConverter byteConverter = new ByteConverter();
	@NonNull
	private FloatConverter floatConverter = new FloatConverter();
	@NonNull
	private ShortConverter shortConverter = new ShortConverter();
	@NonNull
	private IntegerConverter integerConverter = new IntegerConverter();
	@NonNull
	private LongConverter longConverter = new LongConverter();

	public StringConverter() {
		registerReversibleConverter(String.class, Enum.class, enumConverter);
		registerReversibleConverter(String.class, Charset.class, charsetConverter);
		registerReversibleConverter(String.class, Class.class, classConverter);
		registerReversibleConverter(String.class, TimeZone.class, timeZoneConverter);
		registerReversibleConverter(String.class, Currency.class, currencyConverter);
		registerReversibleConverter(String.class, Locale.class, localeConverter);
		registerReversibleConverter(String.class, BigDecimal.class, bigDecimalConverter);
		registerReversibleConverter(String.class, BigInteger.class, bigIntegerConverter);

		registerReversibleConverter(String.class, char.class, characterConverter);
		registerReversibleConverter(String.class, Character.class, characterConverter);
		registerReversibleConverter(String.class, boolean.class, booleanConverter);
		registerReversibleConverter(String.class, Boolean.class, booleanConverter);
		registerReversibleConverter(String.class, double.class, doubleConverter);
		registerReversibleConverter(String.class, Double.class, doubleConverter);
		registerReversibleConverter(String.class, byte.class, byteConverter);
		registerReversibleConverter(String.class, Byte.class, byteConverter);
		registerReversibleConverter(String.class, float.class, floatConverter);
		registerReversibleConverter(String.class, Float.class, floatConverter);
		registerReversibleConverter(String.class, short.class, shortConverter);
		registerReversibleConverter(String.class, Short.class, shortConverter);
		registerReversibleConverter(String.class, int.class, integerConverter);
		registerReversibleConverter(String.class, Integer.class, integerConverter);
		registerReversibleConverter(String.class, long.class, longConverter);
		registerReversibleConverter(String.class, Long.class, longConverter);
	}
}
