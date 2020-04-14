package scw.mvc.output;

import java.util.LinkedList;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.Channel;

public class MultiOutput extends LinkedList<Output> implements Output {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerUtils.getLogger(getClass());

	public boolean canWrite(Channel channel, Object body) {
		for (Output output : this) {
			if (output.canWrite(channel, body)) {
				if (logger.isTraceEnabled()) {
					logger.trace("{} output adpater is body:{}, channel:{}",
							output, body, channel.toString());
				}
				return true;
			}
		}
		return false;
	}

	public void write(Channel channel, Object body) throws Throwable{
		for (Output output : this) {
			if (output.canWrite(channel, body)) {
				if (logger.isTraceEnabled()) {
					logger.trace("{} output adapter is body:{}, channel:{}",
							output, body, channel.toString());
				}
				output.write(channel, body);
				return;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("not support output body:{}, channel:{}", body,
					channel.toString());
		}
	}

}
