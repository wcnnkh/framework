package scw.mvc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import scw.context.Destroy;
import scw.context.result.BaseResult;
import scw.context.support.LifecycleAuxiliary;
import scw.http.HttpStatus;
import scw.http.server.ServerHttpAsyncEvent;
import scw.http.server.ServerHttpAsyncListener;
import scw.io.IOUtils;
import scw.logger.CustomLevel;
import scw.logger.Level;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class HttpChannelDestroy implements Destroy, ServerHttpAsyncListener {
	private static Logger logger = LoggerFactory.getLogger(HttpChannelDestroy.class);

	private Long executeWarnTime;
	private java.util.logging.Level enableLevel = Level.ALL.getValue();
	private final HttpChannel httpChannel;
	private Object responseBody;
	private Throwable error;

	public HttpChannelDestroy(HttpChannel httpChannel) {
		this.httpChannel = httpChannel;
	}

	public final Object getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(Object responseBody) {
		this.responseBody = responseBody;
	}

	public final Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
	}

	public java.util.logging.Level getEnableLevel() {
		return enableLevel;
	}

	public void setEnableLevel(java.util.logging.Level enableLevel) {
		this.enableLevel = enableLevel;
	}

	public Long getExecuteWarnTime() {
		return executeWarnTime;
	}

	public void setExecuteWarnTime(Long executeWarnTime) {
		this.executeWarnTime = executeWarnTime;
	}

	public void destroy() throws IOException {
		if (!httpChannel.isCompleted()) {
			try {
				LifecycleAuxiliary.destroy(httpChannel);
			} catch (Throwable e) {
				logger.error(e, "destroy channel error: " + httpChannel.toString());
			}
			log();
		}
		httpChannel.getResponse().close();
	}

	protected void log() {
		long useTime = System.currentTimeMillis() - httpChannel.getCreateTime();
		Long executeWarnTime = getExecuteWarnTime();
		Level level = (executeWarnTime != null && useTime > executeWarnTime) ? Level.WARN : Level.DEBUG;
		if (error != null) {
			logger.error(error, "Execution {}ms of {}", useTime, this);
			return;
		}

		if ((responseBody != null && responseBody instanceof BaseResult && ((BaseResult) responseBody).isError())) {
			level = Level.ERROR;
		}

		if (!level.equals(Level.ERROR)) {
			HttpStatus status = HttpStatus.valueOf(httpChannel.getResponse().getStatus());
			if (status != null && status.isError()) {
				level = Level.ERROR;
			}
		}

		// 禁用指定级别级别以下的日志
		if (CustomLevel.isGreaterOrEqual(level.getValue(), getEnableLevel()) && logger.isLogEnable(level.getValue())) {
			Object messag = (logger.isDebugEnabled() || !level.equals(Level.WARN)) ? this : httpChannel.getRequest();
			logger.log(level.getValue(), "Execution {}ms of {}", useTime, messag);
		}
	}

	public void onComplete(ServerHttpAsyncEvent event) throws IOException {
		destroy();
	}

	public void onTimeout(ServerHttpAsyncEvent event) throws IOException {
		// ignore
	}

	public void onError(ServerHttpAsyncEvent event) throws IOException {
		// ignore
	}

	public void onStartAsync(ServerHttpAsyncEvent event) throws IOException {
		// ignore
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(httpChannel.getRequest());
		sb.append(IOUtils.LINE_SEPARATOR);
		Map<String, Object> responseMap = new HashMap<String, Object>(4, 1);
		responseMap.put("status", httpChannel.getResponse().getStatus());
		if (responseBody != null) {
			responseMap.put("Content-Type", httpChannel.getResponse().getContentType());
			responseMap.put("body", responseBody);
			responseMap.put("class", responseBody.getClass().getName());
		}
		sb.append(responseMap);
		return sb.toString();
	}
}
