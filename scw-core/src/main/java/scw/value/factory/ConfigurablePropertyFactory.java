package scw.value.factory;


public interface ConfigurablePropertyFactory extends PropertyFactory, ConfigurableValueFactory<String>{
	boolean put(String key, Object value);
	
	boolean putIfAbsent(String key, Object value);
}
