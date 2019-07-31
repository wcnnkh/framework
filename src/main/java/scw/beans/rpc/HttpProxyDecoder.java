package scw.beans.rpc;

import java.net.HttpURLConnection;

public interface HttpProxyDecoder {
	Object decode(HttpURLConnection httpURLConnection);
}
