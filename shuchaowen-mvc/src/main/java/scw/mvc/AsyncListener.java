package scw.mvc;

import java.io.IOException;

public interface AsyncListener {

	public void onComplete(AsyncEvent event) throws IOException;

	public void onTimeout(AsyncEvent event) throws IOException;

	public void onError(AsyncEvent event) throws IOException;

	public void onStartAsync(AsyncEvent event) throws IOException;
}
