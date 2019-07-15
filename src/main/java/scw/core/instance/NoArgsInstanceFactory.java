package scw.core.instance;

public interface NoArgsInstanceFactory {
	<T> T getInstance(Class<T> type);
	
	<T> T getInstance(String name);
}
