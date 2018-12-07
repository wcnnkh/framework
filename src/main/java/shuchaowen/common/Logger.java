package shuchaowen.common;

import shuchaowen.common.logger.Level;
import shuchaowen.common.logger.LogMsg;
import shuchaowen.common.logger.LoggerProcess;
import shuchaowen.common.utils.XTime;

public final class Logger {
	private static final AsyncLogger logger = new AsyncLogger();
	static{
		logger.start();
	}
	
	private Logger(){};
	
	public static void info(String msg) {
		logger.log(new TagLogMsg(Level.INFO, null, msg, null));
	}
	
	public static void info(String tag, String msg) {
		logger.log(new TagLogMsg(Level.INFO, tag, msg, null));
	}
	
	public static void warn(String msg) {
		logger.log(new TagLogMsg(Level.WARN, null, msg, null));
	}

	public static void warn(String tag, String msg) {
		logger.log(new TagLogMsg(Level.WARN, tag, msg, null));
	}
	
	public static void error(String msg) {
		logger.log(new TagLogMsg(Level.ERROR, null, msg, null));
	}
	
	public static void error(String tag, String msg) {
		logger.log(new TagLogMsg(Level.ERROR, tag, msg, null));
	}
	
	public static void error(String msg, Throwable e) {
		logger.log(new TagLogMsg(Level.ERROR, null, msg, e));
	}

	public static void error(String tag, String msg, Throwable e) {
		logger.log(new TagLogMsg(Level.ERROR, tag, msg, e));
	}
	
	public static void debug(String msg) {
		logger.log(new TagLogMsg(Level.DEBUG, null, msg, null));
	}

	public static void debug(String tag, String msg) {
		logger.log(new TagLogMsg(Level.DEBUG, tag, msg, null));
	}

	public static void debug(String tag, String msg, Throwable e) {
		logger.log(new TagLogMsg(Level.DEBUG, tag, msg, e));
	}
	
	public static void shutdown(){
		logger.shutdown();
	}
}

class AsyncLogger extends LoggerProcess {
	@Override
	public void console(LogMsg msg) throws Exception {
		switch (msg.getLevel()) {
		case ERROR:
		case WARN:
			System.err.println(msg.toString());
			break;
		default:
			System.out.println(msg.toString());
			break;
		}

		if (msg.getThrowable() != null) {
			msg.getThrowable().printStackTrace();
		}
	}
}

class TagLogMsg extends LogMsg{
	private String tag;

	public TagLogMsg(Level level, String tag, String msg, Throwable throwable) {
		super(level, msg, throwable);
		this.tag = tag;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(XTime.format(getCts(), "yyyy-MM-dd HH:mm:ss,SSS"));
		sb.append(" ").append(getLevel().toString());
		if(tag != null){
			sb.append(" [").append(tag).append("]");
		}
		sb.append(" - ").append(getMsg());
		return sb.toString();
	}
}