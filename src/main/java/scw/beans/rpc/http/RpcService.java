package scw.beans.rpc.http;

import java.io.InputStream;
import java.io.OutputStream;

public interface RpcService {
	/**
	 * 服务者
	 * @param serviceClass
	 */
	public void service(InputStream in, OutputStream os) throws Throwable;
}
