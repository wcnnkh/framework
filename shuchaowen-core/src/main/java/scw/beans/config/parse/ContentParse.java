package scw.beans.config.parse;

import java.util.List;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractValueFormat;
import scw.io.ResourceUtils;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public final class ContentParse extends AbstractValueFormat implements ConfigParse{

	public Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String filePath, String charset) throws Exception{
		List<String> list = ResourceUtils.getResourceOperations().getLines(filePath, charset);
		if(String.class.isAssignableFrom(field.getSetter().getType())){
			StringBuilder sb = new StringBuilder();
			if(list != null){
				for(String str : list){
					sb.append(str);
				}
			}
			return sb.toString();
		}else if(List.class.isAssignableFrom(field.getSetter().getType())){
			return list;
		}
		return null;
	}

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String name)
			throws Exception {
		return parse(beanFactory, propertyFactory, field, name, getCharsetName());
	}
}

