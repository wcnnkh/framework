package scw.io.event;

import java.util.List;
import java.util.Properties;

import scw.event.ConvertibleObservables;

public abstract class ConvertibleObservablesProperties<T> extends ConvertibleObservables<Properties, T>{

	public ConvertibleObservablesProperties(boolean concurrent) {
		super(concurrent);
	}

	@Override
	protected Properties merge(List<Properties> list) {
		Properties properties = new Properties();
		for(Properties p : list){
			if(p == null){
				continue;
			}
			properties.putAll(p);
		}
		return properties;
	}
}
