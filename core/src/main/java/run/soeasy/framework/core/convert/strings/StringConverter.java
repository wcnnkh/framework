package run.soeasy.framework.core.convert.strings;

import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.registry.DefaultReversibleConverterRegistry;

@Setter
@Getter
public class StringConverter extends DefaultReversibleConverterRegistry<String, ConversionException> {
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
	private ReaderConverter readerConverter = new ReaderConverter();
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
