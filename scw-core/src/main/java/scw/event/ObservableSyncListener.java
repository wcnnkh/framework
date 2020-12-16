package scw.event;

public class ObservableSyncListener<O, T> implements EventListener<ChangeEvent<O>> {
	private final EventListener<ChangeEvent<T>> eventListener;
	private final Observable<T> observable;
	private final boolean ignoreRegistered;

	public ObservableSyncListener(Observable<T> observable,
			boolean ignoreRegistered,
			EventListener<ChangeEvent<T>> eventListener) {
		this.ignoreRegistered = ignoreRegistered;
		this.observable = observable;
		this.eventListener = eventListener;
	}

	public void onEvent(ChangeEvent<O> event) {
		if (ignoreRegistered && observable.isRegistered()) {
			return;
		}

		eventListener.onEvent(new ChangeEvent<T>(event, observable.forceGet()));
	}
}