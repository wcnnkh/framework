package scw.beans.auto;


public interface AutoBean {
	boolean isReference();
	
	Class<?> getTargetClass();
	
	boolean isInstance();
	
	Object create(AutoBeanConfig config) throws Exception;

	Object create(AutoBeanConfig config, Object... params) throws Exception;

	Object create(AutoBeanConfig config, Class<?>[] parameterTypes,
			Object... params) throws Exception;
}
