package run.soeasy.framework.core.exchange;

public interface ListenableSignal<T extends Signal> extends Signal, Listenable<T> {
	Signal registerSuccessListener(Listener<T> listener);

	Signal registerFailureListener(Listener<T> listener);
}
