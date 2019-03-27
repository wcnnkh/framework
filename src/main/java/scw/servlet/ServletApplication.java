package scw.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.application.Application;
import scw.application.CommonApplication;
import scw.beans.BeanFactory;
import scw.beans.property.PropertiesFactory;
import scw.beans.rpc.http.Message;
import scw.common.exception.NestedRuntimeException;
import scw.common.reflect.Invoker;
import scw.common.reflect.ReflectInvoker;
import scw.common.utils.IOUtils;
import scw.common.utils.SignUtils;
import scw.common.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.http.enums.Method;
import scw.servlet.action.DefaultSearchAction;
import scw.servlet.action.SearchAction;
import scw.servlet.beans.CommonRequestBeanFactory;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.request.DefaultRequestFactory;
import scw.servlet.request.RequestFactory;

/**
 * 所有以shuchaowen开头的属性都是系统属性
 * 
 * @author shuchaowen
 *
 */
public class ServletApplication implements Application {
	private static Logger logger = LoggerFactory.getLogger(ServletApplication.class);

	private static final String CHARSET_NAME = "servlet.charsetName";
	private static final String RPC_SIGN = "servlet.rpc-sign";
	private static final String RPC_PATH = "servlet.rpc-path";
	private static final String REQUEST_FACTORY = "servlet.request-factory";
	private static final String SEARCH_ACTION = "servlet.search-action";
	private static final String DEFAULT_ACTION_KEY = "servlet.actionKey";
	private static final String DEFAULT_ACTION_FILTERS = "servlet.filters";

	private final CommonApplication commonApplication;
	private SearchAction searchAction;
	private String rpcSignStr;
	private String rpcServletPath;// 远程代理调用的servletpath，只使用post方法
	private Charset charset;
	private final Map<String, Invoker> invokerRPCMap = new HashMap<String, Invoker>();
	private boolean debug;
	private boolean rpcEnabled;
	private final RequestBeanFactory requestBeanFactory;
	private RequestFactory requestFactory;
	private LinkedList<String> filters;

	public void addFilter(String filter) {
		if (filters == null) {
			filters = new LinkedList<String>();
			filters.add(filter);
		} else {
			for (String n : filters) {
				if (n.equals(filter)) {
					return;
				}
			}
			filters.add(filter);
		}
	}

	public void addFilter(Class<? extends Filter> filter) {
		addFilter(filter.getName());
	}

	public ServletApplication(ServletConfig servletConfig) throws Exception {
		ServletConfigPropertiesFactory propertiesFactory = new ServletConfigPropertiesFactory(servletConfig);
		String initStaticStr = propertiesFactory.getServletConfig("init-static");
		if (StringUtils.isNull(initStaticStr)) {
			this.commonApplication = new CommonApplication(propertiesFactory.getConfigXml(), false, propertiesFactory);
		} else {
			this.commonApplication = new CommonApplication(propertiesFactory.getConfigXml(),
					Boolean.parseBoolean(initStaticStr), propertiesFactory);
		}
		this.requestBeanFactory = new CommonRequestBeanFactory(commonApplication.getBeanFactory(), propertiesFactory,
				propertiesFactory.getConfigXml(), commonApplication.getBeanFactory().getFilterNames());
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
			throw new NestedRuntimeException("RPC not opened");
		}

		if (StringUtils.isNull(rpcSignStr)) {// 不校验签名
			logger.warn("RPC Signature verification not opened(未开启签名验证)");
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

	protected Invoker getRPCInvoker(final Message message) throws NoSuchMethodException, SecurityException {
		Invoker invoker = invokerRPCMap.get(message.getMessageKey());
		if (invoker == null) {
			synchronized (invokerRPCMap) {
				invoker = invokerRPCMap.get(message.getMessageKey());
				if (invoker == null) {
					invoker = new ReflectInvoker(getBeanFactory(), message.getClz(), message.getMethod());
					if (invoker != null) {
						invokerRPCMap.put(message.getMessageKey(), invoker);
					}
				}
			}
		}
		return invoker;
	}

	public void rpc(InputStream inputStream, OutputStream outputStream) throws Throwable {
		Message message = IOUtils.readJavaObject(inputStream);
		if (!rpcAuthorize(message)) {
			throw new NestedRuntimeException("RPC验证失败");
		}

		Invoker invoker = getRPCInvoker(message);
		if (invoker == null) {
			throw new NestedRuntimeException("not found service:" + message.getMessageKey());
		}

		Object obj = invoker.invoke(message.getArgs());
		ObjectOutputStream oos = new ObjectOutputStream(outputStream);
		oos.writeObject(obj);
		oos.flush();
	}

	public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws Throwable {
		if (checkRPCRequest(httpServletRequest)) {
			rpc(httpServletRequest.getInputStream(), httpServletResponse.getOutputStream());
		} else {
			service(formatRequest(httpServletRequest, httpServletResponse));
		}
	}

	protected boolean checkRPCRequest(HttpServletRequest httpServletRequest) {
		return Method.POST.name().equals(httpServletRequest.getMethod()) && isRpcEnabled()
				&& httpServletRequest.getServletPath().equals(getRpcServletPath());
	}

	public Request formatRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws IOException {
		return requestFactory.format(requestBeanFactory, httpServletRequest, httpServletResponse);
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

	public void sendError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, int code,
			String msg) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append("servletPath=").append(httpServletRequest.getServletPath());
			sb.append(",method=").append(httpServletRequest.getMethod());
			sb.append(",status=").append(code);
			sb.append(",msg=").append(msg);
			logger.debug(sb.toString());
		}

		if (!httpServletResponse.isCommitted()) {
			try {
				httpServletResponse.sendError(code, msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void init() {
		commonApplication.init();

		if (charset == null) {
			String charsetName = getPropertiesFactory().getValue(CHARSET_NAME);
			charset = Charset.forName(StringUtils.isNull(charsetName) ? "UTF-8" : charsetName);
		}

		if (StringUtils.isNull(rpcSignStr)) {
			this.rpcSignStr = getPropertiesFactory().getValue(RPC_SIGN);
		}

		if (!StringUtils.isNull(rpcSignStr)) {
			rpcEnabled = true;
		}

		if (StringUtils.isNull(rpcServletPath)) {
			rpcServletPath = getPropertiesFactory().getValue(RPC_PATH);
			if (StringUtils.isNull(rpcServletPath)) {
				rpcServletPath = "/rpc";
			}
		}

		if (searchAction == null) {
			String searchAction = getPropertiesFactory().getValue(SEARCH_ACTION);
			if (!StringUtils.isNull(searchAction)) {
				this.searchAction = getBeanFactory().get(searchAction);
			}
		}

		if (searchAction == null) {
			String actionKey = getPropertiesFactory().getValue(DEFAULT_ACTION_KEY);
			actionKey = StringUtils.isNull(actionKey) ? "action" : actionKey;
			searchAction = new DefaultSearchAction(getBeanFactory(), actionKey);
		}

		String filterNames = getPropertiesFactory().getValue(DEFAULT_ACTION_FILTERS);
		if (!StringUtils.isEmpty(filterNames)) {
			String[] filterNameArr = StringUtils.commonSplit(filterNames);
			for (String filter : filterNameArr) {
				if (StringUtils.isEmpty(filter)) {
					continue;
				}

				addFilter(filter);
			}
		}

		try {
			searchAction.init(commonApplication.getClasses());
		} catch (Throwable e) {
			throw new NestedRuntimeException(e);
		}

		if (requestFactory == null) {
			String requestFactory = getPropertiesFactory().getValue(REQUEST_FACTORY);
			if (StringUtils.isNull(requestFactory)) {
				this.requestFactory = new DefaultRequestFactory(debug);
			} else {
				this.requestFactory = getBeanFactory().get(requestFactory);
			}
		}
	}

	public void destroy() {
		commonApplication.destroy();
	}

	public BeanFactory getBeanFactory() {
		return commonApplication.getBeanFactory();
	}

	public PropertiesFactory getPropertiesFactory() {
		return commonApplication.getPropertiesFactory();
	}

	protected CommonApplication getCommonApplication() {
		return commonApplication;
	}
}