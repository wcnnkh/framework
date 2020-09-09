package scw.mvc;

import java.io.IOException;

import scw.beans.BeanUtils;
import scw.beans.Destroy;
import scw.core.GlobalPropertyFactory;
import scw.event.support.DynamicValue;
import scw.http.HttpStatus;
import scw.http.server.ServerHttpAsyncEvent;
import scw.http.server.ServerHttpAsyncListener;
import scw.io.IOUtils;
import scw.logger.Level;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.logger.SplitLineAppend;
import scw.result.BaseResult;

public class HttpChannelDestroy implements Destroy, ServerHttpAsyncListener {
	private static final DynamicValue<Long> WARN_TIMEOUT = GlobalPropertyFactory.getInstance()
			.getDynamicValue("mvc.warn-execute-time", Long.class, 100L);
	private static Logger logger = LoggerFactory.getLogger(HttpChannelDestroy.class);

	private final HttpChannel httpChannel;
	private Long executeWarnTime;
	private Object responseBody;
	private Throwable error;
	private Level disableLevel = Level.ALL;

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

	public void setExecuteWarnTime(Long executeWarnTime) {
		this.executeWarnTime = executeWarnTime;
	}

	public final Level getDisableLevel() {
		return disableLevel;
	}

	public void setDisableLevel(Level disableLevel) {
		this.disableLevel = disableLevel;
	}

	protected final long getExecuteWarnTime() {
		return executeWarnTime == null ? WARN_TIMEOUT.getValue() : executeWarnTime;
	}

	public void destroy() throws IOException {
		if (!httpChannel.isCompleted()) {
			try {
				BeanUtils.destroy(httpChannel);
			} catch (Exception e) {
				logger.error(e, "destroy channel error: " + httpChannel.toString());
			}

			long useTime = System.currentTimeMillis() - httpChannel.getCreateTime();
			Level level = useTime > getExecuteWarnTime() ? Level.WARN : Level.DEBUG;
			if (error != null || (responseBody != null && responseBody instanceof BaseResult
					&& ((BaseResult) responseBody).isError())) {
				level = Level.ERROR;
			}

			if (level != Level.ERROR) {
				HttpStatus status = HttpStatus.valueOf(httpChannel.getResponse().getStatus());
				if (status != null && status.isError()) {
					level = Level.ERROR;
				}
			}

			// 禁用指定级别级别以下的日志
			if (!level.isGreaterOrEqual(disableLevel)) {
				return;
			}

			if (logger.isLogEnable(level)) {
				logger.log(level, error, "Execution {}ms of {}", useTime, this);
			}
		}
		httpChannel.getResponse().close();
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
		if (responseBody == null) {
			return httpChannel.toString();
		}

		StringBuilder sb = new StringBuilder();
		sb.append(httpChannel.toString());
		sb.append(IOUtils.LINE_SEPARATOR);
		sb.append(new SplitLineAppend("response (" + responseBody.getClass().getName() + ") Content-Type ("
				+ httpChannel.getResponse().getContentType() + ") begin "));
		sb.append(IOUtils.LINE_SEPARATOR);
		sb.append(responseBody);
		sb.append(IOUtils.LINE_SEPARATOR);
		sb.append(new SplitLineAppend("response end"));
		return sb.toString();
	}
}
