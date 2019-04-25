package scw.core;

/**
 * 对Runnable的静态代理
 * 
 * @author shuchaowen
 *
 */
public abstract class RunnableStaticProxy implements Runnable {
	protected final Runnable command;

	public RunnableStaticProxy(Runnable command) {
		this.command = command;
	}

	/**
	 * 在之前运行
	 * 
	 * @return 如果返回false则不在执行后续操作
	 */
	protected abstract boolean before();

	/**
	 * 在之后运行
	 */
	protected abstract void after();

	public void run() {
		if (before()) {
			try {
				command.run();
			} finally {
				after();
			}
		}
	}
}
