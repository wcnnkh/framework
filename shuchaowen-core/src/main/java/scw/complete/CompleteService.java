package scw.complete;

import java.io.IOException;

import scw.beans.annotation.Bean;
import scw.core.Init;

/**
 * 一个确认服务，保证一定会执行,但不保证会重复执行
 * 
 * @author shuchaowen
 *
 */
@Bean(proxy = false)
public interface CompleteService extends Init{
	
	void init() throws Exception;
	
	/**
	 * 注册一个任务
	 * @see CompleteService#init() 这个任务如果在调用run之前没有取消那么在此方法调用时时尝试执行
	 * 
	 * @param completeTask
	 * @return
	 * @throws IOException
	 */
	Complete register(CompleteTask completeTask) throws Exception;
}
