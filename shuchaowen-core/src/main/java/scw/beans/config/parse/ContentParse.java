package scw.beans.config.parse;

import java.util.List;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractValueFormat;
import scw.core.reflect.FieldDefinition;
import scw.io.resource.ResourceUtils;
import scw.util.value.property.PropertyFactory;

public final class ContentParse extends AbstractValueFormat implements ConfigParse{

	public Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldDefinition fieldDefinition, String filePath, String charset) throws Exception{
		List<String> list = ResourceUtils.getResourceOperations().getFileContentLineList(filePath, charset);
		if(String.class.isAssignableFrom(fieldDefinition.getField().getType())){
			StringBuilder sb = new StringBuilder();
			if(list != null){
				for(String str : list){
					sb.append(str);
				}
			}
			return sb.toString();
		}else if(List.class.isAssignableFrom(fieldDefinition.getField().getType())){
			return list;
		}
		return null;
	}

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldDefinition field, String name)
			throws Exception {
		return parse(beanFactory, propertyFactory, field, name, getCharsetName());
	}
}

