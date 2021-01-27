package scw.value.factory;

import java.util.Properties;

import scw.value.AnyValue;
import scw.value.Value;

public class PropertiesValueFactory<K> implements ValueFactory<K>{
	protected final Properties properties;
	
	public PropertiesValueFactory(Properties properties){
		this.properties = properties;
	}
	
	public Value getValue(K key) {
		if(properties == null){
			return null;
		}
		
		Object value = properties.get(key);
		return value == null? null:createValue(value);
	}

	protected Value createValue(Object value){
		return new AnyValue(value);
	}
}
