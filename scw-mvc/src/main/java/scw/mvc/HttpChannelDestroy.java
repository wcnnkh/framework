package scw.mvc;

import java.io.IOException;

import scw.beans.BeanUtils;
import scw.beans.Destroy;
import scw.core.GlobalPropertyFactory;
import scw.event.support.DynamicValue;
import scw.http.server.ServerHttpAsyncEvent;
import scw.http.server.ServerHttpAsyncListener;
import scw.io.IOUtils;
import scw.json.JSONUtils;
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

	public HttpChannelDestroy(HttpChannel httpChannel) {
		this.httpChannel = httpChannel;
	}

	public Object getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(Object responseBody) {
		this.responseBody = responseBody;
	}

	public void setExecuteWarnTime(Long executeWarnTime) {
		this.executeWarnTime = executeWarnTime;
	}

	protected long getExecuteWarnTime() {
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
			if (responseBody == null) {
				if (logger.isLogEnable(level)) {
					logger.log(level, "Execution {}ms of {}", useTime, httpChannel.toString());
				}
			} else {
				if (responseBody instanceof BaseResult && ((BaseResult) responseBody).isError()) {
					level = Level.ERROR;
				}

				if (logger.isLogEnable(level)) {
					String text = responseBody instanceof String ? (String) responseBody
							: JSONUtils.toJSONString(responseBody);
					logger.log(level,
							"Execution {}ms of {}" + IOUtils.LINE_SEPARATOR + "{}" + IOUtils.LINE_SEPARATOR + "{}"
									+ IOUtils.LINE_SEPARATOR + "{}",
							useTime, httpChannel.toString(),
							new SplitLineAppend("response text begin " + httpChannel.getResponse().getContentType()),
							text, new SplitLineAppend("response text end"));
				}
			}
		}
		httpChannel.getResponse().close();
	}

	public void onComplete(ServerHttpAsyncEvent event) throws IOException {
		destroy();
	}

	public void onTimeout(ServerHttpAsyncEvent event) throws IOException {
		// TODO Auto-generated method stub

	}

	public void onError(ServerHttpAsyncEvent event) throws IOException {
		// TODO Auto-generated method stub

	}

	public void onStartAsync(ServerHttpAsyncEvent event) throws IOException {
		// TODO Auto-generated method stub

	}

}
