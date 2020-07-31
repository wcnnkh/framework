package scw.event;

public interface EventRegistration {
	
	static final EventRegistration EMPTY = new EventRegistration() {

		public void unregister() {
			// ignore
		}
	};

	void unregister();
}
