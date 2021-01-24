package scw.env;

import scw.util.PropertyResolver;
import scw.value.factory.ConvertibleObservableValueFactory;
import scw.value.factory.ObservablePropertyFactory;

public interface BasicEnvironment extends ObservablePropertyFactory,
		ConvertibleObservableValueFactory<String>, PropertyResolver {
	String getWorkPath();
}
