package scw.util;

import java.io.Serializable;

public class Result<T> implements Serializable{
	private static final long serialVersionUID = 1L;
	private final boolean active;
	private final java.util.function.Supplier<T> result;
	
	public Result(boolean active, java.util.function.Supplier<T> result){
		this.active = active;
		this.result = result;
	}
	
	public Result(boolean active, T result){
		this(active, new StaticSupplier<T>(result));
	}

	public boolean isActive() {
		return active;
	}

	public T getResult() {
		return result.get();
	}
}
