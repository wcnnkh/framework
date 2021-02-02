package scw.env;

import scw.event.Observable;
import scw.util.placeholder.PlaceholderReplacer;
import scw.util.placeholder.PropertyResolver;
import scw.value.factory.ConvertibleObservableValueFactory;
import scw.value.factory.ObservablePropertyFactory;

public interface BasicEnvironment extends ObservablePropertyFactory,
		ConvertibleObservableValueFactory<String>, PropertyResolver, PlaceholderReplacer {
	public static final String WORK_PATH_PROPERTY = "work.path";
	
	String getWorkPath();
	
	Observable<String> getObservableWorkPath();
}
