package scw.rpc;

import java.io.InputStream;
import java.io.OutputStream;

import scw.beans.annotation.AutoImpl;
import scw.rpc.object.DefaultObjectRpcService;

@AutoImpl({ DefaultObjectRpcService.class })
public interface RpcService {
	/**
	 * 服务者
	 * 
	 * @param serviceClass
	 */
	public void service(InputStream in, OutputStream os) throws Throwable;
}
