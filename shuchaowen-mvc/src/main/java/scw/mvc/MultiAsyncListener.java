package scw.mvc;

import java.io.IOException;
import java.util.LinkedList;

public class MultiAsyncListener extends LinkedList<AsyncListener> implements
		AsyncListener {
	private static final long serialVersionUID = 1L;

	public void onComplete(AsyncEvent event) throws IOException {
		for (AsyncListener listener : this) {
			listener.onComplete(event);
		}
	}

	public void onTimeout(AsyncEvent event) throws IOException {
		for (AsyncListener listener : this) {
			listener.onTimeout(event);
		}
	}

	public void onError(AsyncEvent event) throws IOException {
		for (AsyncListener listener : this) {
			listener.onError(event);
		}
	}

	public void onStartAsync(AsyncEvent event) throws IOException {
		for (AsyncListener listener : this) {
			listener.onStartAsync(event);
		}
	}
}
