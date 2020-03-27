package scw.util.value.property;

import scw.util.value.DefaultValueDefinition;
import scw.util.value.StringValue;
import scw.util.value.Value;


public abstract class StringValuePropertyFactory extends AbstractPropertyFactory{

	public final Value get(String key) {
		String value = getValue(key);
		if(value == null){
			return null;
		}
		
		return new StringValue(value, DefaultValueDefinition.DEFAULT_VALUE_DEFINITION);
	}
	
	protected abstract String getValue(String key);
}
