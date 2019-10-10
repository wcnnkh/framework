package scw.rpc.http;

import java.lang.reflect.Method;

import scw.core.Constants;
import scw.core.PropertyFactory;
import scw.core.annotation.NotRequire;
import scw.core.annotation.ParameterName;
import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.header.HeadersConstants;
import scw.core.instance.InstanceFactory;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.io.serializer.Serializer;
import scw.rpc.RpcConstants;
import scw.rpc.support.ObjectRpcMessageConvert;

public class HttpRestfulRpcProxy implements Filter, RpcConstants {
	private static final String[] DEFAULT_SHARE_HEADERS = new String[] { HeadersConstants.CONTENT_TYPE,
			HeadersConstants.COOKIE};
	private HttpRpcProxy httpRpcProxy;

	public HttpRestfulRpcProxy(InstanceFactory instanceFactory, PropertyFactory propertyFactory,
			@NotRequire String host, Serializer serializer,
			@ParameterName(RPC_HTTP_CHARSET_NAME) @NotRequire String charsetName,
			@ParameterName(RPC_HTTP_MVC_SHARE_HEADERS) @NotRequire String[] shareHeaders) {
		String cName = StringUtils.isEmpty(charsetName) ? Constants.DEFAULT_CHARSET_NAME : charsetName;
		HttpRpcRequestFactory httpRpcRequestFactory;
		httpRpcRequestFactory = new HttpRestfulRpcRequestFactory(propertyFactory, host, cName,
				ArrayUtils.isEmpty(shareHeaders) ? DEFAULT_SHARE_HEADERS : shareHeaders);
		this.httpRpcProxy = new HttpRpcProxy(propertyFactory, instanceFactory, httpRpcRequestFactory);
		this.httpRpcProxy.add(new ObjectRpcMessageConvert(serializer, cName));
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		return httpRpcProxy.filter(invoker, proxy, method, args, filterChain);
	}
}
