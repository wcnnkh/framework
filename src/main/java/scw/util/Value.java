package scw.util;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface Value {
	<T> T parseObject(Class<? extends T> type);

	Object parseObject(Type type);

	String parseString();

	Byte parseByte();

	byte parseByteValue();

	Short parseShort();

	short parseShortValue();

	Integer parseInteger();

	int parseIntValue();

	Long parseLong();

	long parseLongValue();

	Boolean parseBoolean();

	boolean parseBooleanValue();

	Float parseFloat();

	float parseFloatValue();

	Double parseDouble();

	double parseDoubleValue();

	char parseChar();

	Character parseCharacter();

	BigInteger parseBigInteger();

	BigDecimal parseBigDecimal();

	Number parseNumber();

	Class<?> parseClass();

	Enum<?> parseEnum(Class<?> enumType);
}
