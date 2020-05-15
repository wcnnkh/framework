package scw.net.http.server.mvc.output;

import java.io.IOException;
import java.util.LinkedList;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.output.Output;

public class MultiOutput extends LinkedList<Output> implements Output {
	private static final long serialVersionUID = 1L;
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	public boolean canWrite(HttpChannel httpChannel, Object body) {
		for (Output output : this) {
			if (output.canWrite(httpChannel, body)) {
				return true;
			}
		}
		return false;
	}

	public void write(HttpChannel httpChannel, Object body) throws IOException{
		for (Output output : this) {
			if (output.canWrite(httpChannel, body)) {
				if (logger.isTraceEnabled()) {
					logger.trace("{} output adapter is body:{}, channel:{}",
							output, body, httpChannel.toString());
				}
				output.write(httpChannel, body);
				return;
			}
		}
		
		if(body != null){
			if (logger.isDebugEnabled()) {
				logger.debug("not support output body:{}, channel:{}", body,
						httpChannel.toString());
			}
		}
	}

}
