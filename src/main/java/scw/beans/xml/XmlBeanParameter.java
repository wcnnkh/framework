package scw.beans.xml;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Node;

import scw.beans.BeanFactory;
import scw.beans.EParameterType;
import scw.beans.property.PropertiesFactory;
import scw.core.exception.NotFoundException;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringParseUtils;
import scw.core.utils.StringUtils;

public final class XmlBeanParameter implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private final EParameterType type;
	private Class<?> parameterType;
	private final String name;// 可能为空
	private final XmlValue xmlValue;

	public XmlBeanParameter(EParameterType type, Class<?> parameterType, String name, String value, Node node) {
		this.type = type;
		this.parameterType = parameterType;
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

	public EParameterType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public Class<?> getParameterType() {
		return parameterType;
	}

	public void setParameterType(Class<?> parameterType) {
		this.parameterType = parameterType;
	}

	public Object parseValue(BeanFactory beanFactory, PropertiesFactory propertiesFactory) throws Exception {
		return parseValue(beanFactory, propertiesFactory, this.parameterType);
	}

	public Object parseValue(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Class<?> parameterType)
			throws Exception {
		switch (type) {
		case value:
			return formatStringValue(xmlValue.formatValue(propertiesFactory), parameterType);
		case ref:
			return beanFactory.get(xmlValue.formatValue(propertiesFactory));
		case property:
			String value = propertiesFactory.getValue(xmlValue.getValue());
			if (value == null) {
				throw new NotFoundException("not found property:" + xmlValue.getValue());
			}
			return formatStringValue(value, parameterType);
		default:
			break;
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object formatStringValue(String v, Class<?> parameterType) throws ClassNotFoundException, ParseException {
		if (v == null) {
			return null;
		}

		if (Class.class.isAssignableFrom(parameterType)) {
			return ClassUtils.forName(v);
		} else if (Date.class.isAssignableFrom(parameterType)) {
			if (StringUtils.isNumeric(v)) {
				return new Date(Long.parseLong(v));
			} else {
				String dateFormat = xmlValue.getNodeAttributeValue("date-format");
				if (StringUtils.isNull(dateFormat)) {
					throw new NotFoundException("data-format [" + v + "]");
				}

				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				return sdf.parse(v);
			}
		} else if (parameterType.isEnum()) {
			return Enum.valueOf((Class<? extends Enum>) parameterType, v);
		}
		return StringParseUtils.conversion(v, parameterType);
	}
}
