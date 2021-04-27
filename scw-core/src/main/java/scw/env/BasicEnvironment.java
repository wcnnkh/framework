package scw.env;

import scw.event.Observable;
import scw.util.placeholder.PlaceholderReplacer;
import scw.util.placeholder.PropertyResolver;
import scw.value.ListenablePropertyFactory;

public interface BasicEnvironment extends ListenablePropertyFactory, PropertyResolver, PlaceholderReplacer {
	public static final String WORK_PATH_PROPERTY = "work.path";
	
	String getWorkPath();
	
	Observable<String> getObservableWorkPath();
}
