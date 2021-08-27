package io.basc.framework.logger;

import io.basc.framework.core.utils.XTime;
import io.basc.framework.util.FormatUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomFormatter extends Formatter {
	private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	@Override
	public String format(LogRecord record) {
		String message = formatMessage(record);
		message = FormatUtils.formatPlaceholder(message, null, record.getParameters());
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(XTime.format(record.getMillis(), TIME_FORMAT));
        sb.append(" ");
        sb.append(record.getLevel().getName());
        String name = record.getLoggerName();
        if(name != null) {
        	sb.append(" ");
        	sb.append("[").append(name).append("]");
        }
        
        sb.append(" - ");
        sb.append(message);
        sb.append(throwable);
        sb.append(LINE_SEPARATOR);
        return sb.toString();
	}

}
