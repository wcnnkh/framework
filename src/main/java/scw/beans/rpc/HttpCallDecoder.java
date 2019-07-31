package scw.beans.rpc;

import java.net.HttpURLConnection;

public interface HttpCallDecoder {
	Object decode(HttpURLConnection httpURLConnection);
}
