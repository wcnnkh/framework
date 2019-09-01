package scw.beans.auto;


public interface AutoBean {
	boolean initEnable();
	
	boolean destroyEnable();
	
	boolean autowriedEnable();
	
	Object create(AutoBeanConfig config) throws Exception;

	Object create(AutoBeanConfig config, Object... params) throws Exception;

	Object create(AutoBeanConfig config, Class<?>[] parameterTypes,
			Object... params) throws Exception;
}
