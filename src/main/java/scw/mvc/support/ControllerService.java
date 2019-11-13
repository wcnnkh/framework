package scw.mvc.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.io.IOUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.Channel;
import scw.mvc.ExceptionHandler;
import scw.mvc.Filter;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.net.mime.MimeTypeConstants;
import scw.rpc.RpcService;

public final class ControllerService {
	private static Logger logger = LoggerFactory.getLogger(ControllerService.class);
	private final Collection<Filter> filters;
	private final long warnExecuteTime;
	private final Collection<ExceptionHandler> exceptionHandlers;
	private Map<String, CrossDomainDefinition> crossDomainDefinitionMap = new HashMap<String, CrossDomainDefinition>();
	private CrossDomainDefinition defaultCrossDomainDefinition;// 默认的跨域方式
	private final String rpcPath;
	private final RpcService rpcService;
	private final String sourceRoot;
	private final String[] sourcePath;

	public ControllerService(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Throwable {
		this.filters = MVCUtils.getFilters(beanFactory, propertyFactory);
		this.warnExecuteTime = MVCUtils.getWarnExecuteTime(propertyFactory);
		this.exceptionHandlers = MVCUtils.getExceptionHandlers(beanFactory, propertyFactory);
		this.rpcService = MVCUtils.getRpcService(propertyFactory, beanFactory);
		this.rpcPath = MVCUtils.getRPCPath(propertyFactory);
		this.sourceRoot = MVCUtils.getSourceRoot(propertyFactory);
		this.sourcePath = MVCUtils.getSourcePath(propertyFactory);
		if (!StringUtils.isEmpty(sourceRoot) && !ArrayUtils.isEmpty(sourcePath)) {
			logger.info("sourceRoot:{}", sourceRoot);
			logger.info("sourcePath:{}", Arrays.toString(sourcePath));
		}
	}

	public void service(Channel channel) {
		if (channel instanceof HttpChannel) {
			if (defaultFilter((HttpChannel) channel, ((HttpChannel) channel).getRequest(),
					((HttpChannel) channel).getResponse())) {
				return;
			}
		}
		MVCUtils.service(channel, filters, warnExecuteTime, exceptionHandlers);
	}

	public boolean defaultFilter(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse) {
		CrossDomainDefinition crossDomainDefinition = getCrossDomainDefinition(httpRequest.getRequestPath());
		if (crossDomainDefinition == null) {
			if (defaultCrossDomainDefinition != null) {
				MVCUtils.responseCrossDomain(defaultCrossDomainDefinition, httpResponse);
			}
		} else {
			MVCUtils.responseCrossDomain(crossDomainDefinition, httpResponse);
		}

		if (checkRPCEnable(httpRequest)) {
			channel.getResponse().setContentType(MimeTypeConstants.APPLICATION_OCTET_STREAM_VALUE);
			try {
				rpcService.service(httpRequest.getInputStream(), httpResponse.getOutputStream());
			} catch (IOException e) {
				logger.error(e, channel.toString());
			}
			return true;
		}

		if (checkResourcePath(httpRequest)) {
			File file = new File(sourceRoot + httpRequest.getRequestPath());
			if (file != null && file.exists() && file.isFile()) {
				outputFile(file, httpResponse);
				return true;
			}
		}
		return false;
	}

	public void register(String matchPath, String origin, String methods, int maxAge, String headers,
			boolean credentials) {
		register(matchPath, new CrossDomainDefinition(origin, headers, methods, credentials, maxAge));
	}

	public synchronized void register(String matchPath, CrossDomainDefinition crossDomainDefinition) {
		crossDomainDefinitionMap.put(matchPath, crossDomainDefinition);
	}

	protected final boolean checkRPCEnable(HttpRequest request) {
		if (rpcService == null) {
			return false;
		}

		if (!"POST".equals(request.getMethod())) {
			return false;
		}

		if (!request.getRequestPath().equals(rpcPath)) {
			return false;
		}

		return StringUtils.startsWith(request.getContentType(), MimeTypeConstants.APPLICATION_OCTET_STREAM_VALUE, true);
	}

	private CrossDomainDefinition getCrossDomainDefinition(String requestPath) {
		if (crossDomainDefinitionMap.isEmpty()) {
			return null;
		}

		CrossDomainDefinition crossDomainDefinition = crossDomainDefinitionMap.get(requestPath);
		if (crossDomainDefinition == null) {
			for (Entry<String, CrossDomainDefinition> entry : crossDomainDefinitionMap.entrySet()) {
				if (StringUtils.test(requestPath, entry.getKey())) {
					crossDomainDefinition = entry.getValue();
					break;
				}
			}
		}
		return crossDomainDefinition;
	}

	private boolean checkResourcePath(HttpRequest httpRequest) {
		if (!"GET".equals(httpRequest.getMethod())) {
			return false;
		}

		if (ArrayUtils.isEmpty(sourcePath)) {
			return false;
		}

		for (String p : sourcePath) {
			if (p.equals(sourcePath) || StringUtils.test(httpRequest.getRequestPath(), p)) {
				return true;
			}
		}
		return false;
	}

	private void outputFile(File file, HttpResponse httpResponse) {
		OutputStream output = null;
		FileInputStream fis = null;
		try {
			output = httpResponse.getOutputStream();
			fis = new FileInputStream(file);
			IOUtils.write(fis, output, 1024 * 8);
		} catch (IOException e) {
			logger.error(e, file.getPath());
		} finally {
			IOUtils.close(fis, output);
		}
	}
}
