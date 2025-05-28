package run.soeasy.framework.core.exchange;

/**
 * 可监听的
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Listenable<T> {
	default BatchListenable<T> batch() {
		return (FakeBatchListenable<T, Listenable<T>>) (() -> this);
	}

	/**
	 * 注册一个监听
	 * 
	 * @param listener
	 * @return
	 */
	Registration registerListener(Listener<T> listener);
}
