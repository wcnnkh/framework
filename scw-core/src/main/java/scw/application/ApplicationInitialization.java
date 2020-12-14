package scw.application;


/**
 * 会在应用初始化成功后全局调用此类的方法
 * 
 * @author shuchaowen
 *
 */
public interface ApplicationInitialization {
	void init(Application application) throws Throwable;
}
