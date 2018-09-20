package shuchaowen.core.multitask;

public class State<T>{
	private Throwable throwable;
	private T data;
	
	public State(T data, Throwable throwable){
		this.data = data;
		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public T getData() {
		return data;
	}
}
