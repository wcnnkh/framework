package shuchaowen.core.beans;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import shuchaowen.core.exception.NotSupportException;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.FileUtils;
import shuchaowen.core.util.StringUtils;

public final class BeanParameter implements Cloneable, Serializable{
	private static final String FILE_URL_PREFIX = "file://";
	private static final String CHARSET_ATTR_KEY = "charset";
	
	private static final long serialVersionUID = 1L;
	private EParameterType type;
	private Class<?> parameterType;
	private String name;//可能为空
	private String value;
	private Map<String, String> attrMap;
	
	public BeanParameter(EParameterType type, Class<?> parameterType, String name, String value, Map<String, String> attrMap){
		this.type = type;
		this.parameterType = parameterType;
		this.name = name;
		this.value = value;
		this.attrMap = attrMap;
	}
	
	/**
	 * 用于序列化
	 */
	public BeanParameter(){};
	
	@Override
	public BeanParameter clone(){
		try {
			return (BeanParameter) super.clone();
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
	public String getValue() {
		return value;
	}

	public Class<?> getParameterType() {
		return parameterType;
	}

	public void setParameterType(Class<?> parameterType) {
		this.parameterType = parameterType;
	}

	public void setType(EParameterType type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Map<String, String> getAttrMap() {
		return attrMap;
	}

	public void setAttrMap(Map<String, String> attrMap) {
		this.attrMap = attrMap;
	}
	
	public Object parseValue(BeanFactory beanFactory, PropertiesFactory propertiesFactory) throws Exception{
		return parseValue(beanFactory, propertiesFactory, this.parameterType);
	}
	
	public Object parseValue(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Class<?> parameterType) throws Exception{
		switch (type) {
		case value:
			return StringUtils.conversion(value, parameterType);
		case ref:
			return beanFactory.get(value);
		case property:
			return propertiesFactory.getProperties(value, parameterType);
		case url:
			String charsetName = attrMap == null? null: attrMap.get(CHARSET_ATTR_KEY);
			if(StringUtils.isNull(charsetName)){
				charsetName = "utf-8";
			}
			
			if(value.startsWith(FILE_URL_PREFIX)){
				String path = value.substring(FILE_URL_PREFIX.length());
				File file = ConfigUtils.getFile(path);
				return FileUtils.readerFileContent(file, charsetName);
			}
			throw new NotSupportException(value);
		default:
			break;
		}
		return null;
	}
}
