package io.basc.framework.convert.strings;

import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.factory.support.DefaultReversibleConverterFactory;
import io.basc.framework.convert.lang.ResourceToString;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public class StringConverter extends
		DefaultReversibleConverterFactory<String, ConversionException, ReversibleConverter<String, ? extends Object, ? extends ConversionException>> {
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
	@NonNull
	private ResourceToString resourceToString = ResourceToString.DEFAULT;

	public StringConverter() {
		registerConverter(Reader.class, readerConverter);
		registerConverter(Enum.class, enumConverter);
		registerConverter(Charset.class, charsetConverter);
		registerConverter(Class.class, classConverter);
		registerConverter(TimeZone.class, timeZoneConverter);
		registerConverter(Currency.class, currencyConverter);
		registerConverter(Locale.class, localeConverter);
		registerConverter(BigDecimal.class, bigDecimalConverter);
		registerConverter(BigInteger.class, bigIntegerConverter);

		registerConverter(char.class, characterConverter);
		registerConverter(Character.class, characterConverter);
		registerConverter(boolean.class, booleanConverter);
		registerConverter(Boolean.class, booleanConverter);
		registerConverter(double.class, doubleConverter);
		registerConverter(Double.class, doubleConverter);
		registerConverter(byte.class, byteConverter);
		registerConverter(Byte.class, byteConverter);
		registerConverter(float.class, floatConverter);
		registerConverter(Float.class, floatConverter);
		registerConverter(short.class, shortConverter);
		registerConverter(Short.class, shortConverter);
		registerConverter(int.class, integerConverter);
		registerConverter(Integer.class, integerConverter);
		registerConverter(long.class, longConverter);
		registerConverter(Long.class, longConverter);
	}
}
