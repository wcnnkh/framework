package scw.servlet.parameter;

import java.math.BigDecimal;
import java.math.BigInteger;

import scw.servlet.ParameterDefinition;
import scw.servlet.ParameterFilter;
import scw.servlet.ParameterFilterChain;
import scw.servlet.Request;
import scw.servlet.parameter.annotation.Parameter;

public final class FirstParameterFilter implements ParameterFilter {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object filter(Request request, ParameterDefinition parameterDefinition, ParameterFilterChain chain)
			throws Exception {
		String name = parameterDefinition.getName();
		Parameter parameter = parameterDefinition.getAnnotation(Parameter.class);
		if(parameter != null){
			name = parameter.value();
		}
		
		Class<?> type = parameterDefinition.getType();
		if (String.class.isAssignableFrom(type)) {
			return request.getParameter(name);
		} else if (int.class.isAssignableFrom(type)) {
			return request.getIntValue(name);
		} else if (Integer.class.isAssignableFrom(type)) {
			return request.getInteger(name);
		} else if (long.class.isAssignableFrom(type)) {
			return request.getLongValue(name);
		} else if (Long.class.isAssignableFrom(type)) {
			return request.getLong(name);
		} else if (float.class.isAssignableFrom(type)) {
			return request.getFloatValue(name);
		} else if (Float.class.isAssignableFrom(type)) {
			return request.getFloat(name);
		} else if (short.class.isAssignableFrom(type)) {
			return request.getShortValue(name);
		} else if (Short.class.isAssignableFrom(type)) {
			return request.getShort(name);
		} else if (boolean.class.isAssignableFrom(type)) {
			return request.getBooleanValue(name);
		} else if (Boolean.class.isAssignableFrom(type)) {
			return request.getBoolean(name);
		} else if (byte.class.isAssignableFrom(type)) {
			return request.getByteValue(name);
		} else if (Byte.class.isAssignableFrom(type)) {
			return request.getByte(name);
		} else if (char.class.isAssignableFrom(type)) {
			return request.getChar(name);
		} else if (Character.class.isAssignableFrom(type)) {
			return request.getCharacter(name);
		} else if (BigDecimal.class.isAssignableFrom(type)) {
			return request.getBigDecimal(name);
		} else if (BigInteger.class.isAssignableFrom(type)) {
			return request.getBigInteger(name);
		} else if (Class.class.isAssignableFrom(type)) {
			return request.getClass(name);
		} else if (type.isEnum()) {
			return request.getEnum(name, (Class<? extends Enum>) type);
		} else {
			return chain.doFilter(request, parameterDefinition);
		}
	}

}
