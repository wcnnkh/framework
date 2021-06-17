package scw.util.stream;



public interface StreamProcessor<T, E extends Throwable> extends CallableProcessor<T, E>{
	T process() throws E;
	
	<S> StreamProcessor<S, E> stream(Processor<T, S, E> processor);
	
	StreamProcessor<T, E> onClose(CallbackProcessor<E> closeProcessor);
	
	void close() throws E;
}
