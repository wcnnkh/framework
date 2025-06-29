package run.soeasy.framework.core.exchange.future;

import run.soeasy.framework.core.exchange.Registration;

/**
 * 用来对未来的行为确认操作
 * 
 * @author soeasy.run
 *
 */
public interface Confirm extends Registration {
	/**
	 * 检查操作是否成功
	 * 
	 * @return 若操作成功则返回true，否则返回false
	 */
	boolean isSuccess();

	/**
	 * 尝试确认成功
	 * 
	 * @return 若尝试成功则返回true，否则返回false
	 */
	boolean trySuccess();

	/**
	 * 取消操作
	 * 
	 * @return 若取消成功则返回true，否则返回false
	 */
	@Override
	boolean cancel();
}