package scw.env;

import scw.event.Observable;
import scw.util.PropertyResolver;
import scw.value.factory.ConvertibleObservableValueFactory;
import scw.value.factory.ObservablePropertyFactory;

public interface BasicEnvironment extends ObservablePropertyFactory,
		ConvertibleObservableValueFactory<String>, PropertyResolver {
	public static final String WORK_PATH_PROPERTY = "work.path";
	
	String getWorkPath();
	
	Observable<String> getObservableWorkPath();
}
