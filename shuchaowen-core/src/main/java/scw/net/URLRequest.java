package scw.net;

import java.net.Proxy;
import java.net.URL;

public interface URLRequest extends RequestCallback {
	URL getURL();

	Proxy getProxy();
}
