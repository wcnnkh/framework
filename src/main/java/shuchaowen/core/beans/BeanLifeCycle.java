package shuchaowen.core.beans;

/**
 * 一个实体类的生命周期
 * @author shuchaowen
 *
 */
public interface BeanLifeCycle {
	void autowrite(Object bean) throws Exception;
	
	void init(Object bean) throws Exception;
	
	void destroy(Object bean) throws Exception;
}
