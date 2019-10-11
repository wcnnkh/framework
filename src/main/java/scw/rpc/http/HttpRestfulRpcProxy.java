package scw.rpc.http;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import scw.core.Constants;
import scw.core.PropertyFactory;
import scw.core.annotation.NotRequire;
import scw.core.annotation.ParameterName;
import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.instance.InstanceFactory;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.io.serializer.Serializer;
import scw.net.header.HeadersConstants;
import scw.rpc.RpcConstants;
import scw.rpc.support.ObjectRpcMessageConvert;

public class HttpRestfulRpcProxy implements Filter, RpcConstants {
	private static final String[] DEFAULT_SHARE_HEADERS = new String[] { HeadersConstants.CONTENT_TYPE,
			HeadersConstants.COOKIE, HeadersConstants.X_FORWARDED_FOR };
	private HttpRpcProxy httpRpcProxy;

	public HttpRestfulRpcProxy(InstanceFactory instanceFactory, PropertyFactory propertyFactory,
			@NotRequire String host, Serializer serializer,
			@ParameterName(RPC_HTTP_CHARSET_NAME) @NotRequire String charsetName,
			@ParameterName(RPC_HTTP_MVC_SHARE_HEADERS) @NotRequire String[] shareHeaders,
			@ParameterName(RPC_HTTP_MVC_SHARE_APPEND_HEADERS) @NotRequire String[] appendShareHeaders) {
		String cName = StringUtils.isEmpty(charsetName) ? Constants.DEFAULT_CHARSET_NAME : charsetName;

		Set<String> shareHeaderSet = new HashSet<String>();
		if (ArrayUtils.isEmpty(shareHeaders)) {
			shareHeaderSet.addAll(Arrays.asList(DEFAULT_SHARE_HEADERS));
		} else {
			shareHeaderSet.addAll(Arrays.asList(shareHeaders));
		}

		if (!ArrayUtils.isEmpty(appendShareHeaders)) {
			shareHeaderSet.addAll(Arrays.asList(appendShareHeaders));
		}

		HttpRpcRequestFactory httpRpcRequestFactory = new HttpRestfulRpcRequestFactory(propertyFactory, host, cName,
				shareHeaderSet.toArray(new String[0]));
		this.httpRpcProxy = new HttpRpcProxy(propertyFactory, instanceFactory, httpRpcRequestFactory);
		this.httpRpcProxy.add(new ObjectRpcMessageConvert(serializer, cName));
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		return httpRpcProxy.filter(invoker, proxy, method, args, filterChain);
	}
}
