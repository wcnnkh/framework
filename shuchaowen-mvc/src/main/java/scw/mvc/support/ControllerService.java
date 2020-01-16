package scw.mvc.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;

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
import scw.mvc.rpc.RpcService;
import scw.net.MimeTypeUtils;
import scw.net.http.Method;

public final class ControllerService {
	private static Logger logger = LoggerFactory.getLogger(ControllerService.class);
	private final Collection<Filter> filters;
	private final long warnExecuteTime;
	private final Collection<ExceptionHandler> exceptionHandlers;
	private CrossDomainDefinition defaultCrossDomainDefinition;// 默认的跨域方式
	private final String rpcPath;
	private final RpcService rpcService;
	private final String sourceRoot;
	private final String[] sourcePath;
	private final CrossDomainDefinitionFactory crossDomainDefinitionFactory;

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

		this.crossDomainDefinitionFactory = MVCUtils.getCrossDomainDefinitionFactory(beanFactory, propertyFactory);
		if (MVCUtils.isSupportCorssDomain(propertyFactory)) {
			defaultCrossDomainDefinition = new CrossDomainDefinition(propertyFactory);
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
		if (crossDomainDefinitionFactory == null) {
			if (defaultCrossDomainDefinition != null) {
				MVCUtils.responseCrossDomain(defaultCrossDomainDefinition, httpResponse);
			}
		} else {
			CrossDomainDefinition crossDomainDefinition = crossDomainDefinitionFactory
					.getCrossDomainDefinition(channel);
			if (crossDomainDefinition != null) {
				MVCUtils.responseCrossDomain(crossDomainDefinition, httpResponse);
			}
		}

		if (checkRPCEnable(httpRequest)) {
			channel.getResponse().setMimeType(MimeTypeUtils.APPLICATION_OCTET_STREAM);
			try {
				rpcService.service(httpRequest.getBody(), httpResponse.getOutputStream());
			} catch (IOException e) {
				logger.error(e, channel.toString());
			}
			return true;
		}

		if (checkResourcePath(httpRequest)) {
			File file = new File(sourceRoot + httpRequest.getControllerPath());
			if (file != null && file.exists() && file.isFile()) {
				outputFile(file, httpResponse);
				return true;
			}
		}
		return false;
	}

	protected final boolean checkRPCEnable(HttpRequest request) {
		if (rpcService == null) {
			return false;
		}

		if (Method.POST != request.getMethod()) {
			return false;
		}

		if (!request.getControllerPath().equals(rpcPath)) {
			return false;
		}

		return StringUtils.startsWith(request.getRawContentType(), MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE, true);
	}

	private boolean checkResourcePath(HttpRequest httpRequest) {
		if (Method.GET != httpRequest.getMethod()) {
			return false;
		}

		if (ArrayUtils.isEmpty(sourcePath)) {
			return false;
		}

		for (String p : sourcePath) {
			if (p.equals(sourcePath) || StringUtils.test(httpRequest.getControllerPath(), p)) {
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
