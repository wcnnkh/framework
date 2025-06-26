package run.soeasy.framework.messaging.convert.support;

import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

import lombok.NonNull;
import run.soeasy.framework.beans.BeanFormat;
import run.soeasy.framework.codec.format.URLCodec;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class QueryStringFormat extends BeanFormat {
	private static ConcurrentHashMap<String, QueryStringFormat> formatCacheMap = new ConcurrentHashMap<>();

	public QueryStringFormat(Charset charset) {
		this(new URLCodec(charset));
	}

	public QueryStringFormat(URLCodec urlCodec) {
		super("&", "=", urlCodec, urlCodec);
	}

	public static QueryStringFormat getFormat(@NonNull String name, @NonNull Charset charset) {
		QueryStringFormat format = formatCacheMap.get(name);
		if (format == null) {
			format = formatCacheMap.computeIfAbsent(name, (e) -> new QueryStringFormat(charset));
		}
		return format;
	}

	public static QueryStringFormat getFormat(@NonNull Charset charset) {
		return getFormat(charset.name(), charset);
	}

	public static String format(@NonNull Charset charset, @NonNull Object source) {
		return getFormat(charset).format(source, TypeDescriptor.forObject(source));
	}

	public static Object parse(@NonNull Charset charset, String source, @NonNull TypeDescriptor targetTypeDescriptor) {
		return getFormat(charset).parse(source, targetTypeDescriptor);
	}
}
