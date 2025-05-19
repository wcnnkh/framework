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
import run.soeasy.framework.core.convert.service.ConverterRegistry;

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
		registerConverter(String.class, Enum.class, enumConverter);
		registerConverter(String.class, Charset.class, charsetConverter);
		registerConverter(String.class, Class.class, classConverter);
		registerConverter(String.class, TimeZone.class, timeZoneConverter);
		registerConverter(String.class, Currency.class, currencyConverter);
		registerConverter(String.class, Locale.class, localeConverter);
		registerConverter(String.class, BigDecimal.class, bigDecimalConverter);
		registerConverter(String.class, BigInteger.class, bigIntegerConverter);

		registerConverter(String.class, char.class, characterConverter);
		registerConverter(String.class, Character.class, characterConverter);
		registerConverter(String.class, boolean.class, booleanConverter);
		registerConverter(String.class, Boolean.class, booleanConverter);
		registerConverter(String.class, double.class, doubleConverter);
		registerConverter(String.class, Double.class, doubleConverter);
		registerConverter(String.class, byte.class, byteConverter);
		registerConverter(String.class, Byte.class, byteConverter);
		registerConverter(String.class, float.class, floatConverter);
		registerConverter(String.class, Float.class, floatConverter);
		registerConverter(String.class, short.class, shortConverter);
		registerConverter(String.class, Short.class, shortConverter);
		registerConverter(String.class, int.class, integerConverter);
		registerConverter(String.class, Integer.class, integerConverter);
		registerConverter(String.class, long.class, longConverter);
		registerConverter(String.class, Long.class, longConverter);
	}
}
