package scw.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.application.CommonApplication;
import scw.beans.rpc.http.Message;
import scw.common.Logger;
import scw.common.exception.BeansException;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.io.decoder.JavaObjectDecoder;
import scw.common.io.encoder.JavaObjectEncoder;
import scw.common.reflect.Invoker;
import scw.common.reflect.ReflectInvoker;
import scw.common.utils.SignUtils;
import scw.common.utils.StringUtils;
import scw.net.http.enums.Method;
import scw.servlet.action.Action;
import scw.servlet.action.DefaultSearchAction;
import scw.servlet.action.SearchAction;
import scw.servlet.bean.CommonRequestBeanFactory;
import scw.servlet.bean.RequestBeanFactory;
import scw.servlet.request.DefaultRequestFactory;
import scw.servlet.request.RequestFactory;

public class HttpServerApplication extends CommonApplication {
	private static final String SHUCHAOWEN_CONFIG = "shuchaowen";
	private static final String INIT_STATIC = "init-static";
	private static final String CHARSET_NAME = "charsetName";
	private static final String RPC_SIGN = "rpc-sign";
	private static final String RPC_PATH = "rpc-path";
	private static final String REQUEST_FACTORY = "request-factory";
	private static final String SEARCH_ACTION = "search-action";

	private static final String DEFAULT_ACTION_KEY = "actionKey";

	private SearchAction searchAction;
	private String rpcSignStr;
	private String rpcServletPath;// 远程代理调用的servletpath，只使用post方法
	private Charset charset;
	private final Map<String, Invoker> invokerRPCMap = new HashMap<String, Invoker>();
	private final HttpServerConfigFactory httpServerConfigFactory;
	private boolean debug;
	private boolean rpcEnabled;
	private final RequestBeanFactory requestBeanFactory;
	private RequestFactory requestFactory;

	public HttpServerApplication(HttpServerConfigFactory httpServerConfigFactory) {
		super(httpServerConfigFactory.getConfig(SHUCHAOWEN_CONFIG), StringUtils
				.isNull(httpServerConfigFactory.getConfig(INIT_STATIC)) ? false
				: Boolean.parseBoolean(httpServerConfigFactory
						.getConfig(INIT_STATIC)));
		this.httpServerConfigFactory = httpServerConfigFactory;
		try {
			this.requestBeanFactory = new CommonRequestBeanFactory(
					getBeanFactory(), getPropertiesFactory(),
					httpServerConfigFactory.getConfig(SHUCHAOWEN_CONFIG));
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}

	public void setSearchAction(SearchAction searchAction) {
		this.searchAction = searchAction;
	}

	public Charset getCharset() {
		return charset;
	}

	public RequestBeanFactory getRequestBeanFactory() {
		return requestBeanFactory;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
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

	/**
	 * RPC权限验证
	 * 
	 * @param message
	 */
	private boolean rpcAuthorize(Message message) {
		if (!isRpcEnabled()) {
			throw new ShuChaoWenRuntimeException("RPC not opened");
		}

		if (StringUtils.isNull(rpcSignStr)) {// 不校验签名
			Logger.warn("RPC", "Signature verification not opened(未开启签名验证)");
			return true;
		}

		long t = (Long) message.getAttribute("t");
		String checkSign = SignUtils.md5Str(t + rpcSignStr, charset.name());
		if (t < System.currentTimeMillis() - 10000) {// 如果超过10秒失效
			return false;
		}

		String sign = (String) message.getAttribute("sign");
		if (!checkSign.equals(sign)) {
			return false;
		}
		return true;
	}

	public Invoker getRPCInvoker(final Message message)
			throws NoSuchMethodException, SecurityException {
		Invoker invoker = invokerRPCMap.get(message.getMessageKey());
		if (invoker == null) {
			synchronized (invokerRPCMap) {
				invoker = invokerRPCMap.get(message.getMessageKey());
				if (invoker == null) {
					invoker = new ReflectInvoker(getBeanFactory(),
							message.getClz(), message.getMethod());
					if (invoker != null) {
						invokerRPCMap.put(message.getMessageKey(), invoker);
					}
				}
			}
		}
		return invoker;
	}

	public void rpc(InputStream inputStream, OutputStream outputStream)
			throws Throwable {
		Message message = (Message) JavaObjectDecoder.DECODER
				.decode(inputStream);
		if (!rpcAuthorize(message)) {
			throw new ShuChaoWenRuntimeException("RPC验证失败");
		}

		Invoker invoker = getRPCInvoker(message);
		if (invoker == null) {
			throw new ShuChaoWenRuntimeException("not found service:"
					+ message.getMessageKey());
		}

		Object obj = invoker.invoke(message.getArgs());
		JavaObjectEncoder.ENCODER.encode(outputStream, obj);
	}

	public void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Throwable {
		if (checkRPCRequest(httpServletRequest)) {
			rpc(httpServletRequest.getInputStream(),
					httpServletResponse.getOutputStream());
		} else {
			service(formatRequest(httpServletRequest, httpServletResponse));
		}
	}

	public boolean checkRPCRequest(HttpServletRequest httpServletRequest) {
		return Method.POST.name().equals(httpServletRequest.getMethod())
				&& isRpcEnabled()
				&& httpServletRequest.getServletPath().equals(
						getRpcServletPath());
	}

	public Request formatRequest(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws IOException {
		return requestFactory.format(requestBeanFactory, httpServletRequest,
				httpServletResponse);
	}

	public void service(Request request) throws Throwable {
		try {
			Action action = searchAction.getAction(request);
			if (action == null) {
				sendError(request, request.getResponse(), 404, "not found action");
				return;
			}

			action.doAction(request, request.getResponse());
		} finally {
			if (request != null) {
				request.destroy();
			}
		}
	}

	public void sendError(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, int code, String msg) {
		if (!httpServletResponse.isCommitted()) {
			try {
				httpServletResponse.sendError(code, msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void init() {
		super.init();

		if (charset == null) {
			String charsetName = httpServerConfigFactory
					.getConfig(CHARSET_NAME);
			charset = Charset.forName(StringUtils.isNull(charsetName) ? "UTF-8"
					: charsetName);
		}

		if (StringUtils.isNull(rpcSignStr)) {
			this.rpcSignStr = httpServerConfigFactory.getConfig(RPC_SIGN);
		}

		if (!StringUtils.isNull(rpcSignStr)) {
			rpcEnabled = true;
		}

		if (StringUtils.isNull(rpcServletPath)) {
			rpcServletPath = httpServerConfigFactory.getConfig(RPC_PATH);
			if (StringUtils.isNull(rpcServletPath)) {
				rpcServletPath = "/rpc";
			}
		}

		String debugStr = httpServerConfigFactory.getConfig("debug");
		if (!StringUtils.isNull(debugStr)) {
			debug = Boolean.parseBoolean(debugStr);
		}

		if (searchAction == null) {
			String searchAction = httpServerConfigFactory
					.getConfig(SEARCH_ACTION);
			if (!StringUtils.isNull(searchAction)) {
				this.searchAction = getBeanFactory().get(searchAction);
			}
		}

		if (searchAction == null) {
			String actionKey = httpServerConfigFactory
					.getConfig(DEFAULT_ACTION_KEY);
			actionKey = StringUtils.isNull(actionKey) ? "action" : actionKey;
			searchAction = new DefaultSearchAction(getBeanFactory(), true,
					actionKey);
		}

		try {
			searchAction.init(getClasses());
		} catch (Throwable e) {
			throw new ShuChaoWenRuntimeException(e);
		}

		if (requestFactory == null) {
			String requestFactory = httpServerConfigFactory
					.getConfig(REQUEST_FACTORY);
			if (StringUtils.isNull(requestFactory)) {
				this.requestFactory = new DefaultRequestFactory(debug);
			} else {
				this.requestFactory = getBeanFactory().get(requestFactory);
			}
		}
	}

	public HttpServerConfigFactory getHttpServerConfigFactory() {
		return httpServerConfigFactory;
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}