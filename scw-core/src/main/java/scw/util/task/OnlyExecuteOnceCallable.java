package scw.util.task;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class OnlyExecuteOnceCallable<V> implements Callable<V>, Serializable{
	private static final long serialVersionUID = 1L;
	private Callable<V> callable;
	private volatile V value;
	private volatile AtomicBoolean tag;

	public OnlyExecuteOnceCallable(Callable<V> callable) {
		this.callable = callable;
		this.tag = new AtomicBoolean(false);
	}

	public V call() throws Exception {
		if(tag.get()){
			return value;
		}
		
		if (tag.compareAndSet(false, true)) {
			this.value = callable.call();
		}
		return value;
	}

}
