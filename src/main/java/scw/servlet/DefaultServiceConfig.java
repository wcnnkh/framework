package scw.servlet;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;

import scw.application.CommonApplication;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.property.PropertiesFactory;
import scw.common.Constants;
import scw.common.utils.StringParseUtils;
import scw.common.utils.StringUtils;
import scw.servlet.action.DefaultSearchAction;
import scw.servlet.action.SearchAction;
import scw.servlet.beans.CommonRequestBeanFactory;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.request.DefaultRequestFactory;
import scw.servlet.request.RequestFactory;
import scw.servlet.rpc.DefaultRPCServer;
import scw.servlet.rpc.RPCServer;

public class DefaultServiceConfig implements ServiceConfig {
	private static final String CHARSET_NAME = "servlet.charsetName";
	private static final String RPC_SIGN = "servlet.rpc-sign";
	private static final String RPC_SERVER = "servlet.rpc";
	private static final String REQUEST_FACTORY = "servlet.request-factory";
	private static final String SEARCH_ACTION = "servlet.search-action";
	private static final String DEFAULT_ACTION_KEY = "servlet.actionKey";
	private static final String DEFAULT_ACTION_FILTERS = "servlet.filters";
	private static final String DEBUG_KEY = "servlet.debug";

	private final Collection<Class<?>> classes;
	private final PropertiesFactory propertiesFactory;
	private final BeanFactory beanFactory;

	private LinkedList<String> rootFilters = new LinkedList<String>();
	private SearchAction searchAction;
	private String actionKey;

	private RPCServer RPCServer;
	private String rpcSignStr;
	private String rpcPath;
	private String rpcEnable;

	private RequestFactory requestFactory;
	private RequestBeanFactory requestBeanFactory;

	private Charset charset;
	private boolean debug;

	public DefaultServiceConfig(CommonApplication commonApplication) throws Throwable {
		this.beanFactory = commonApplication.getBeanFactory();
		this.propertiesFactory = commonApplication.getPropertiesFactory();
		this.classes = commonApplication.getClasses();
		this.requestBeanFactory = new CommonRequestBeanFactory(commonApplication.getBeanFactory(),
				commonApplication.getPropertiesFactory(), commonApplication.getConfigPath(),
				commonApplication.getBeanFactory().getFilterNames());
	}

	public DefaultServiceConfig(BeanFactory beanFactory, PropertiesFactory propertiesFactory,
			Collection<Class<?>> classes) {
		this.beanFactory = beanFactory;
		this.propertiesFactory = propertiesFactory;
		this.classes = classes;
	}

	public void addFilter(String filter) {
		if (StringUtils.isEmpty(filter)) {
			return;
		}

		rootFilters.add(filter);
	}

	public void setRPCServer(RPCServer rPCServer) {
		RPCServer = rPCServer;
	}

	public String getActionKey() {
		return actionKey;
	}

	public void setActionKey(String actionKey) {
		this.actionKey = actionKey;
	}

	public String getRpcSignStr() {
		return rpcSignStr;
	}

	public void setRpcSignStr(String rpcSignStr) {
		this.rpcSignStr = rpcSignStr;
	}

	public String getRpcPath() {
		return rpcPath;
	}

	public void setRpcPath(String rpcPath) {
		this.rpcPath = rpcPath;
	}

	public String getRpcEnable() {
		return rpcEnable;
	}

	public void setRpcEnable(String rpcEnable) {
		this.rpcEnable = rpcEnable;
	}

	public Charset getCharset() {
		if (charset == null) {
			String charsetName = getPropertiesFactory().getValue(CHARSET_NAME);
			setCharset(StringUtils.isNull(charsetName) ? Constants.DEFAULT_CHARSET : Charset.forName(charsetName));
		}
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	private boolean isDebugInit = false;

	public boolean isDebug() {
		if (!isDebugInit) {
			String v = propertiesFactory.getValue(DEBUG_KEY);
			if (!StringUtils.isEmpty(v)) {
				this.debug = StringParseUtils.parseBoolean(v);
			}
			isDebugInit = true;
		}
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public Collection<Class<?>> getClasses() {
		return classes;
	}

	public PropertiesFactory getPropertiesFactory() {
		return propertiesFactory;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setSearchAction(SearchAction searchAction) {
		this.searchAction = searchAction;
	}

	public void setRequestFactory(RequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	public RPCServer getRPCServer() {
		if (RPCServer == null) {
			String rpcServerBeanName = getPropertiesFactory().getValue(RPC_SERVER);
			if (StringUtils.isEmpty(rpcServerBeanName)) {
				if (StringUtils.isNull(getRpcSignStr())) {
					setRpcSignStr(getPropertiesFactory().getValue(RPC_SIGN));
				}

				setRPCServer(new DefaultRPCServer(getBeanFactory(),
						StringUtils.isEmpty(getRpcPath()) ? "/rpc" : getRpcPath(), getRpcSignStr(),
						!StringUtils.isNull(getRpcSignStr()), getCharset().name()));
			} else {
				RPCServer RPCServer = getBeanFactory().get(rpcServerBeanName);
				setRPCServer(RPCServer);
			}
		}
		return RPCServer;
	}

	public SearchAction getSearchAction() {
		if (searchAction == null) {
			String searchActionBeanName = getPropertiesFactory().getValue(SEARCH_ACTION);
			if (StringUtils.isNull(searchActionBeanName)) {
				if (StringUtils.isEmpty(getActionKey())) {
					String actionKey = getPropertiesFactory().getValue(DEFAULT_ACTION_KEY);
					setActionKey(StringUtils.isNull(actionKey) ? "action" : actionKey);
				}
				setSearchAction(new DefaultSearchAction(getBeanFactory(), getActionKey()));
			} else {
				SearchAction searchAction = getBeanFactory().get(searchActionBeanName);
				setSearchAction(searchAction);
			}

			try {
				getSearchAction().init(classes);
			} catch (Exception e) {
				throw new RuntimeException("初始化SearchAction异常", e);
			}
		}
		return searchAction;
	}

	public RequestFactory getRequestFactory() {
		if (requestFactory == null) {
			String requestFactoryBeanName = getPropertiesFactory().getValue(REQUEST_FACTORY);
			if (StringUtils.isNull(requestFactoryBeanName)) {
				setRequestFactory(new DefaultRequestFactory(isDebug()));
			} else {
				RequestFactory requestFactory = getBeanFactory().get(requestFactoryBeanName);
				setRequestFactory(requestFactory);
			}
		}
		return requestFactory;
	}

	private Collection<Filter> filters;

	public Collection<Filter> getFilters() {
		if (filters == null) {
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

			this.filters = BeanUtils.getBeanList(beanFactory, rootFilters);
		}
		return filters;
	}

	public RequestBeanFactory getRequestBeanFactory() {
		return requestBeanFactory;
	}

}
