package shuchaowen.common.logger;

import shuchaowen.common.utils.XTime;

public class ClassLogMsg extends LogMsg{
	private Class<?> clz;

	public ClassLogMsg(Class<?> clz, Level level, String msg, Throwable throwable) {
		super(level, msg, throwable);
		this.clz = clz;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(XTime.format(getCts(), "yyyy-MM-dd HH:mm:ss,SSS"));
		sb.append(" ").append(getLevel().toString());
		sb.append(" [").append(clz.getName()).append("]");
		sb.append(" - ").append(getMsg());
		return sb.toString();
	}
}
