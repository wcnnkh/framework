package shuchaowen.core.application;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.http.rpc.Message;
import shuchaowen.core.http.rpc.serialization.JavaObjectSerializer;
import shuchaowen.core.http.rpc.serialization.Serializer;
import shuchaowen.core.http.server.Action;
import shuchaowen.core.http.server.Request;
import shuchaowen.core.http.server.Response;
import shuchaowen.core.http.server.SearchAction;
import shuchaowen.core.http.server.search.DefaultSearchAction;
import shuchaowen.core.invoke.Invoker;
import shuchaowen.core.invoke.ReflectInvoker;
import shuchaowen.core.util.LazyMap;
import shuchaowen.core.util.SignHelp;

public class HttpServerApplication extends CommonApplication {
	private SearchAction searchAction;
	private String rpcSignStr;
	private Charset charset;
	private final LazyMap<String, Invoker> invokerRPCMap = new LazyMap<String, Invoker>();
	private Serializer rpcSerializer = new JavaObjectSerializer();
	
	public HttpServerApplication(String config){
		super(config);
		this.searchAction = new DefaultSearchAction(getBeanFactory(), true, "action");
	}

	public void setSearchAction(SearchAction searchAction) {
		this.searchAction = searchAction;
	}
	
	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public void setRpcSerializer(Serializer rpcSerializer) {
		this.rpcSerializer = rpcSerializer;
	}

	public String getRpcSignStr() {
		return rpcSignStr;
	}

	public void setRpcSignStr(String rpcSignStr) {
		this.rpcSignStr = rpcSignStr;
	}

	/**
	 * RPC权限验证
	 * 
	 * @param message
	 */
	public boolean rpcAuthorize(Message message) {
		long t = (Long) message.getAttribute("t");
		String checkSign = SignHelp.md5Str(t + rpcSignStr, charset.name());
		if (t < System.currentTimeMillis() - 10000) {// 如果超过10秒失效
			return false;
		}

		String sign = (String) message.getAttribute("sign");
		if (!checkSign.equals(sign)) {
			return false;
		}
		return true;
	}

	public Invoker getRPCInvoker(final Message message) {
		return invokerRPCMap.get(message.getMessageKey(), new Callable<Invoker>() {
			
			public Invoker call() throws Exception {
				return new ReflectInvoker(getBeanFactory(), message.getClz(), message.getMethod());
			}
		});
	}

	public void rpc(InputStream inputStream, OutputStream outputStream) throws Throwable {
		Message message;
		Object obj;
		message = rpcSerializer.decode(inputStream, Message.class);
		if (message == null) {
			throw new ShuChaoWenRuntimeException("序列化失败");
		}

		if (!rpcAuthorize(message)) {
			throw new ShuChaoWenRuntimeException("RPC验证失败");
		}

		Invoker invoker = getRPCInvoker(message);
		if (invoker == null) {
			throw new ShuChaoWenRuntimeException("not found service:" + message.getMessageKey());
		}

		obj = invoker.invoke(message.getArgs());
		rpcSerializer.encode(outputStream, obj);
	}

	public boolean service(Request request, Response response) throws Throwable {
		Action action = searchAction.getAction(request);
		if (action == null) {
			return false;
		}

		action.doAction(request, response);
		return true;
	}

	@Override
	public void init() {
		super.init();
		try {
			if(searchAction != null){
				searchAction.init(getClasses());
			}
		} catch (Throwable e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
}
