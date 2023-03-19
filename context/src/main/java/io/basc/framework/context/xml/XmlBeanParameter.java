package io.basc.framework.context.xml;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Node;

import io.basc.framework.core.annotation.EmptyAnnotatedElement;
import io.basc.framework.env.Environment;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeansException;
import io.basc.framework.lang.NotFoundException;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.Value;

public final class XmlBeanParameter extends EmptyAnnotatedElement
		implements Cloneable, ParameterDescriptor, Serializable {
	private static final long serialVersionUID = 1L;
	private final XmlParameterType parameterType;
	private Class<?> type;
	private final String name;// 可能为空
	private final XmlValue xmlValue;

	public XmlBeanParameter(XmlParameterType parameterType, Class<?> type, String name, String value, Node node) {
		this.parameterType = parameterType;
		this.type = type;
		this.name = name;
		this.xmlValue = new XmlValue(value, node);
	}

	@Override
	public String toString() {
		return "[parameterType=" + parameterType + ", type=" + type + ", name=" + name + ", xmlValue=" + xmlValue + "]";
	}

	@Override
	public XmlBeanParameter clone() {
		try {
			return (XmlBeanParameter) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public XmlParameterType getParameterType() {
		return parameterType;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Type getGenericType() {
		return type;
	}

	public boolean isAccept(ParameterDescriptor parameterDescriptor, Environment environment) {
		switch (parameterType) {
		case ref:
			BeanDefinition definition = environment.getDefinition(xmlValue.formatValue(environment));
			if (definition == null) {
				return false;
			}

			return parameterDescriptor.getType().isAssignableFrom(definition.getTypeDescriptor().getType())
					&& definition.isInstance();
		case property:
			return environment.getProperties().containsKey(xmlValue.getValue());
		default:
			break;
		}
		return true;
	}

	public Object parseValue(ParameterDescriptor parameterDescriptor, Environment environment) {
		Object value = null;
		switch (parameterType) {
		case value:
			String text = xmlValue.formatValue(environment);
			if (text != null) {
				value = formatStringValue(Value.of(text), parameterDescriptor);
			}
			break;
		case ref:
			value = environment.getInstance(xmlValue.formatValue(environment));
			break;
		case property:
			Value v = environment.getProperties().get(xmlValue.getValue());
			if (v.isPresent()) {
				value = formatStringValue(v, parameterDescriptor);
			}
			break;
		default:
			break;
		}

		if (xmlValue.isRequire() && value == null) {
			throw new NotFoundException(xmlValue.getValue());
		}
		return value;
	}

	private Object formatStringValue(Value value, ParameterDescriptor parameterDescriptor) {
		if (value == null) {
			return null;
		}

		if (Date.class == parameterDescriptor.getType()) {
			if (value.isNumber()) {
				return new Date(value.getAsLong());
			} else {
				String dateFormat = xmlValue.getNodeAttributeValue("date-format");
				if (StringUtils.isEmpty(dateFormat)) {
					throw new NotFoundException("data-format [" + value + "]");
				}

				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				try {
					return sdf.parse(value.getAsString());
				} catch (ParseException e) {
					throw new BeansException(value.getAsString(), e);
				}
			}
		}

		return value.getAsObject(parameterDescriptor.getGenericType());
	}

	public boolean isNullable() {
		return type == null ? true : !type.isPrimitive();
	}
}
