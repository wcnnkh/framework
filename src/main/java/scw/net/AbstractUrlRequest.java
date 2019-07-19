package scw.net;

import java.net.Proxy;
import java.net.URL;

public abstract class AbstractUrlRequest extends AbstractRequest {

	public abstract URL getURL();

	public abstract Proxy getProxy();

}
