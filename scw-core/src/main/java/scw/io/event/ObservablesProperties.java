package scw.io.event;

import java.util.List;
import java.util.Properties;

import scw.event.Observables;

public class ObservablesProperties extends Observables<Properties>{

	public ObservablesProperties(boolean concurrent) {
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
