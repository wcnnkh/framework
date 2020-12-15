package scw.io.event;

import java.util.List;
import java.util.Properties;

import scw.event.Observables;
import scw.event.Observable;
import scw.io.ResourceUtils;

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
	
	public Observable<Properties> loadProperties(String resource){
		Observable<Properties> observable = ResourceUtils.getResourceOperations().getProperties(resource);
		addObservable(observable);
		return observable;
	}
	
	public Observable<Properties> loadProperties(String resource, String charsetName){
		Observable<Properties> observable = ResourceUtils.getResourceOperations().getProperties(resource, charsetName);
		addObservable(observable);
		return observable;
	}
}
