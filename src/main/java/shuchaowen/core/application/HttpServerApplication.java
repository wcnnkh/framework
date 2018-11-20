package shuchaowen.core.application;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

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
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.SignHelp;
import shuchaowen.core.util.StringUtils;

public class HttpServerApplication extends CommonApplication {
	private static final String SHUCHAOWEN_CONFIG = "shuchaowen";
	private static final String INIT_STATIC = "init-static";
	private static final String CHARSET_NAME = "charsetName";
	private static final String RPC_SIGN = "rpc-sign";
	private static final String RPC_SERIALIZER = "rpc-serializer";
	private static final String RPC_PATH = "rpc-path";
	
	private static final String DEFAULT_ACTION_KEY = "actionKey";

	private SearchAction searchAction;
	private String rpcSignStr;
	private String rpcServletPath;// 远程代理调用的servletpath，只使用post方法
	private Charset charset;
	private final Map<String, Invoker> invokerRPCMap = new HashMap<String, Invoker>();
	private Serializer rpcSerializer;
	private final HttpServerConfigFactory httpServerConfigFactory;
	private boolean debug;
	private boolean rpcEnabled;

	public HttpServerApplication(HttpServerConfigFactory httpServerConfigFactory) {
		super(httpServerConfigFactory.getConfig(SHUCHAOWEN_CONFIG),
				StringUtils.isNull(httpServerConfigFactory.getConfig(INIT_STATIC)) ? false
						: Boolean.parseBoolean(httpServerConfigFactory.getConfig(INIT_STATIC)));
		this.httpServerConfigFactory = httpServerConfigFactory;
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
		this.rpcEnabled = true;
		this.rpcSignStr = rpcSignStr;
	}

	public String getRpcServletPath() {
		return rpcServletPath;
	}

	public void setRpcServletPath(String rpcServletPath) {
		this.rpcServletPath = rpcServletPath;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isRpcEnabled() {
		return rpcEnabled;
	}

	public void setRpcEnabled(boolean rpcEnabled) {
		this.rpcEnabled = rpcEnabled;
	}

	public Serializer getRpcSerializer() {
		return rpcSerializer;
	}

	/**
	 * RPC权限验证
	 * 
	 * @param message
	 */
	public boolean rpcAuthorize(Message message) {
		if(!isRpcEnabled()){
			throw new ShuChaoWenRuntimeException("RPC not opened");
		}
		
		if(StringUtils.isNull(rpcSignStr)){//不校验签名
			Logger.warn("RPC", "Signature verification not opened(未开启签名验证)");
			return true;
		}
		
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

	public Invoker getRPCInvoker(final Message message) throws NoSuchMethodException, SecurityException {
		Invoker invoker = invokerRPCMap.get(message.getMessageKey());
		if(invoker == null){
			synchronized (invokerRPCMap) {
				invoker = invokerRPCMap.get(message.getMessageKey());
				if(invoker == null){
					invoker = new ReflectInvoker(getBeanFactory(), message.getClz(), message.getMethod());
					if(invoker != null){
						invokerRPCMap.put(message.getMessageKey(), invoker);
					}
				}
			}
		}
		return invoker;
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
		
		if(charset == null){
			String charsetName = httpServerConfigFactory.getConfig(CHARSET_NAME);
			charset = Charset.forName(StringUtils.isNull(charsetName)? "UTF-8":charsetName);
		}
		
		this.rpcSignStr = httpServerConfigFactory.getConfig(RPC_SIGN);
		if(!StringUtils.isNull(rpcSignStr)){
			rpcEnabled = true;
		}
		
		if(rpcSerializer == null){
			String rpcSerializer = httpServerConfigFactory.getConfig(RPC_SERIALIZER);
			if(StringUtils.isNull(rpcSerializer)){
				this.rpcSerializer = new JavaObjectSerializer();
			}else{
				this.rpcSerializer = getBeanFactory().get(rpcSerializer);
			}
		}
		
		if (StringUtils.isNull(rpcServletPath)) {
			rpcServletPath = httpServerConfigFactory.getConfig(RPC_PATH);
			if(StringUtils.isNull(rpcServletPath)){
				rpcServletPath = "/rpc";
			}
		}
		
		if(searchAction == null){
			String actionKey = httpServerConfigFactory.getConfig(DEFAULT_ACTION_KEY);
			actionKey = StringUtils.isNull(actionKey)? "action":actionKey;
			searchAction = new DefaultSearchAction(getBeanFactory(), true, actionKey);
		}
		
		try {
			if (searchAction != null) {
				searchAction.init(getClasses());
			}
		} catch (Throwable e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public HttpServerConfigFactory getHttpServerConfigFactory() {
		return httpServerConfigFactory;
	}
}
