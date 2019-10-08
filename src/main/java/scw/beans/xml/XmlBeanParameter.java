package scw.beans.xml;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Node;

import scw.beans.EParameterType;
import scw.core.PropertyFactory;
import scw.core.exception.NotFoundException;
import scw.core.instance.InstanceFactory;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;

public final class XmlBeanParameter implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private final EParameterType type;
	private Type parameterType;
	private final String name;// 可能为空
	private final XmlValue xmlValue;

	public XmlBeanParameter(EParameterType type, Type parameterType, String name, String value, Node node) {
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

	public Type getParameterType() {
		return parameterType;
	}

	public void setParameterType(Type parameterType) {
		this.parameterType = parameterType;
	}

	public Object parseValue(InstanceFactory instanceFactory, PropertyFactory propertyFactory) throws Exception {
		return parseValue(instanceFactory, propertyFactory, this.parameterType);
	}

	public Object parseValue(InstanceFactory instanceFactory, PropertyFactory propertyFactory, Type parameterType)
			throws Exception {
		Object value = null;
		switch (type) {
		case value:
			value = formatStringValue(xmlValue.formatValue(propertyFactory), parameterType);
			break;
		case ref:
			value = instanceFactory.getInstance(xmlValue.formatValue(propertyFactory));
			break;
		case property:
			String v = propertyFactory.getProperty(xmlValue.getValue());
			value = formatStringValue(v, parameterType);
			break;
		default:
			break;
		}

		if (xmlValue.isRequire() && value == null) {
			throw new NotFoundException(xmlValue.getValue());
		}
		return value;
	}

	private Object formatStringValue(String v, Type parameterType) throws ClassNotFoundException, ParseException {
		if (v == null) {
			return null;
		}

		if (Date.class == parameterType) {
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
		}
		return StringParse.defaultParse(v, parameterType);
	}
}
