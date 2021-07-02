package scw.boot;

/**
 * 会在应用初始化成功后全局调用此类的方法
 * 
 * @author shuchaowen
 *
 */
@FunctionalInterface
public interface ApplicationPostProcessor {
	void postProcessApplication(ConfigurableApplication application) throws Throwable;
}
