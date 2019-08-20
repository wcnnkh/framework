package scw.servlet.parameter;

import java.math.BigDecimal;
import java.math.BigInteger;

import scw.core.utils.StringUtils;
import scw.json.JSONParseSupport;
import scw.json.JSONUtils;
import scw.servlet.ParameterDefinition;
import scw.servlet.ParameterFilter;
import scw.servlet.ParameterFilterChain;
import scw.servlet.Request;
import scw.servlet.http.HttpRequest;
import scw.servlet.parameter.annotation.Json;
import scw.servlet.parameter.annotation.Parameter;

public final class FirstParameterFilter implements ParameterFilter {
	private final JSONParseSupport jsonParseSupport;

	public FirstParameterFilter() {
		this(JSONUtils.DEFAULT_JSON_SUPPORT);
	}

	public FirstParameterFilter(JSONParseSupport jsonParseSupport) {
		this.jsonParseSupport = jsonParseSupport;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object filter(Request request,
			ParameterDefinition parameterDefinition, ParameterFilterChain chain)
			throws Exception {
		String name = parameterDefinition.getName();
		Json json = parameterDefinition.getAnnotation(Json.class);
		if (json != null) {
			name = json.value();
			if (StringUtils.isEmpty(name)) {
				if ((request instanceof HttpRequest)
						&& "GET".equals(((HttpRequest) request).getMethod())) {
					return request.getObject(parameterDefinition.getType());
				} else {
					return jsonParseSupport.parseObject(
							request.getBean(Body.class).getBody(),
							parameterDefinition.getGenericType());
				}
			} else {
				return jsonParseSupport.parseObject(request.getString(name),
						parameterDefinition.getGenericType());
			}
		}

		Parameter parameter = parameterDefinition
				.getAnnotation(Parameter.class);
		if (parameter != null) {
			name = parameter.value();
		}

		if (String.class.isAssignableFrom(parameterDefinition.getType())) {
			return request.getParameter(name);
		} else if (int.class.isAssignableFrom(parameterDefinition.getType())) {
			return request.getIntValue(name);
		} else if (Integer.class
				.isAssignableFrom(parameterDefinition.getType())) {
			return request.getInteger(name);
		} else if (long.class.isAssignableFrom(parameterDefinition.getType())) {
			return request.getLongValue(name);
		} else if (Long.class.isAssignableFrom(parameterDefinition.getType())) {
			return request.getLong(name);
		} else if (float.class.isAssignableFrom(parameterDefinition.getType())) {
			return request.getFloatValue(name);
		} else if (Float.class.isAssignableFrom(parameterDefinition.getType())) {
			return request.getFloat(name);
		} else if (short.class.isAssignableFrom(parameterDefinition.getType())) {
			return request.getShortValue(name);
		} else if (Short.class.isAssignableFrom(parameterDefinition.getType())) {
			return request.getShort(name);
		} else if (boolean.class
				.isAssignableFrom(parameterDefinition.getType())) {
			return request.getBooleanValue(name);
		} else if (Boolean.class
				.isAssignableFrom(parameterDefinition.getType())) {
			return request.getBoolean(name);
		} else if (byte.class.isAssignableFrom(parameterDefinition.getType())) {
			return request.getByteValue(name);
		} else if (Byte.class.isAssignableFrom(parameterDefinition.getType())) {
			return request.getByte(name);
		} else if (char.class.isAssignableFrom(parameterDefinition.getType())) {
			return request.getChar(name);
		} else if (Character.class.isAssignableFrom(parameterDefinition
				.getType())) {
			return request.getCharacter(name);
		} else if (BigDecimal.class.isAssignableFrom(parameterDefinition
				.getType())) {
			return request.getBigDecimal(name);
		} else if (BigInteger.class.isAssignableFrom(parameterDefinition
				.getType())) {
			return request.getBigInteger(name);
		} else if (Class.class.isAssignableFrom(parameterDefinition.getType())) {
			return request.getClass(name);
		} else if (parameterDefinition.getType().isEnum()) {
			return request.getEnum(name,
					(Class<? extends Enum>) parameterDefinition.getType());
		} else {
			return chain.doFilter(request, parameterDefinition);
		}
	}
}
