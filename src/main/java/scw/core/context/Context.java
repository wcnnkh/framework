package scw.core.context;

public interface Context {
	boolean isNew();
	
	boolean isActive();
	
	Object bindResource(Object name, Object value);
	
	Object getResource(Object name);
	
	void addContextLifeCycle(ContextLifeCycle contextLifeCycle);
	
	boolean isRelease();
}
