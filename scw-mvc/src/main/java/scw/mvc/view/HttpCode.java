package scw.mvc.view;

import java.io.IOException;

import scw.http.HttpStatus;
import scw.logger.Level;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.HttpChannel;

public class HttpCode implements View {
	private static Logger logger = LoggerUtils.getLogger(HttpCode.class);
	private int status;
	private String msg;

	public HttpCode(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		if (httpChannel.getResponse().getContentType() == null) {
			httpChannel.getResponse()
					.setContentType("text/html;charset=" + httpChannel.getResponse().getCharacterEncoding());
		}

		HttpStatus status = HttpStatus.valueOf(this.status);
		Level level = status.isError() ? Level.WARN : Level.DEBUG;
		if (logger.isLogEnable(level)) {
			logger.log(level, "{} -> {} {}", httpChannel.toString(), this.status, msg);
		}
		httpChannel.getResponse().sendError(this.status, msg);
	}
}
