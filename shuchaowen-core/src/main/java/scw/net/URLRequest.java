package scw.net;

import java.net.Proxy;
import java.net.URL;

public interface URLRequest extends Request {
	URL getURL();

	Proxy getProxy();
}
