package scw.beans.xml;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Node;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.instance.InstanceFactory;
import scw.core.parameter.AbstractParameterDescriptor;
import scw.core.utils.StringUtils;
import scw.lang.NotFoundException;
import scw.util.value.StringValue;
import scw.util.value.Value;
import scw.util.value.property.PropertyFactory;

public final class XmlBeanParameter extends AbstractParameterDescriptor implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private final EParameterType parameterType;
	private Class<?> type;
	private final String name;// 可能为空
	private final XmlValue xmlValue;

	public XmlBeanParameter(EParameterType parameterType, Class<?> type, String name, String value, Node node) {
		this.parameterType = parameterType;
		this.type = type;
		this.name = name;
		this.xmlValue = new XmlValue(value, node);
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

	public EParameterType getParameterType() {
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

	public AnnotatedElement getAnnotatedElement() {
		return AnnotatedElementUtils.EMPTY_ANNOTATED_ELEMENT;
	}

	public Object parseValue(InstanceFactory instanceFactory, PropertyFactory propertyFactory) throws Exception {
		return parseValue(instanceFactory, propertyFactory, this.type);
	}

	public Object parseValue(InstanceFactory instanceFactory, PropertyFactory propertyFactory, Type type)
			throws Exception {
		Object value = null;
		switch (parameterType) {
		case value:
			String text = xmlValue.formatValue(propertyFactory);
			if (text != null) {
				value = formatStringValue(new StringValue(text), type);
			}
			break;
		case ref:
			value = instanceFactory.getInstance(xmlValue.formatValue(propertyFactory));
			break;
		case property:
			Value v = propertyFactory.get(xmlValue.getValue());
			if (v != null) {
				value = formatStringValue(v, type);
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

	private Object formatStringValue(Value value, Type parameterType) throws ClassNotFoundException, ParseException {
		if (value == null) {
			return null;
		}

		if (Date.class == parameterType) {
			if (StringUtils.isNumeric(value.getAsString())) {
				return new Date(Long.parseLong(value.getAsString()));
			} else {
				String dateFormat = xmlValue.getNodeAttributeValue("date-format");
				if (StringUtils.isNull(dateFormat)) {
					throw new NotFoundException("data-format [" + value + "]");
				}

				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				return sdf.parse(value.getAsString());
			}
		}
		return value.getAsObject(parameterType);
	}
}
