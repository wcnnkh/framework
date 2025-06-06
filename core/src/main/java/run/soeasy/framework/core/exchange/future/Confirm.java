package run.soeasy.framework.core.exchange.future;

import run.soeasy.framework.core.exchange.Registration;

/**
 * 用来对未来的行为确认操作
 * 
 * @author shuchaowen
 *
 */
public interface Confirm extends Registration {
	boolean isSuccess();

	/**
	 * 尝试确认成功
	 * 
	 * @return
	 */
	boolean trySuccess();

	/**
	 * 取消
	 */
	@Override
	boolean cancel();
}
