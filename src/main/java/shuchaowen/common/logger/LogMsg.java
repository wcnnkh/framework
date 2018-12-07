package shuchaowen.common.logger;

import shuchaowen.common.utils.XTime;

public class LogMsg {
	private long cts;
	private String ymd;
	private Level level;
	private String msg;
	private Throwable throwable;
	
	public LogMsg(Level level, String msg, Throwable throwable){
		this.cts = System.currentTimeMillis();
		this.level = level;
		this.msg = msg;
		this.throwable = throwable;
	}
	
	public long getCts() {
		return cts;
	}
	
	public String getYMD(){
		if(ymd == null){
			ymd = XTime.format(cts, "yyyy-MM-dd");
		}
		return ymd;
	}

	public Level getLevel() {
		return level;
	}

	public String getMsg() {
		return msg;
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
