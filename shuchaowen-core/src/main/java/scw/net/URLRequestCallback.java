package scw.net;

import java.net.Proxy;
import java.net.URL;

public interface URLRequestCallback extends URLConnectionRequestCallback {
	URL getURL();

	Proxy getProxy();
}
