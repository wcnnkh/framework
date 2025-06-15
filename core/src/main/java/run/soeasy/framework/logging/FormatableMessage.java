package run.soeasy.framework.logging;

import java.io.IOException;
import java.io.Serializable;
import java.util.function.Supplier;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.io.Exportable;

public final class FormatableMessage implements Exportable, Serializable, Supplier<String> {
	private static final long serialVersionUID = 1L;
	private static final String PLACEHOLDER = "{}";

	public static String formatPlaceholder(Object text, String placeholder, Object... args) {
		StringBuilder sb = new StringBuilder();
		try {
			formatPlaceholder(sb, text, placeholder, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public static void formatPlaceholder(@NonNull Appendable appendable, Object format, String placeholder,
			Object... args) throws IOException {
		String text = format == null ? null : format.toString();
		if (StringUtils.isEmpty(text) || ArrayUtils.isEmpty(args)) {
			appendable.append(text);
			return;
		}

		String findText = StringUtils.isEmpty(placeholder) ? PLACEHOLDER : placeholder;
		int lastFind = 0;
		for (int i = 0; i < args.length; i++) {
			int index = text.indexOf(findText, lastFind);
			if (index == -1) {
				break;
			}

			appendable.append(text.substring(lastFind, index));
			Object v = args[i];
			if (v == null) {
				appendable.append("null");
			} else {
				if (v instanceof Exportable) {
					Exportable printable = (Exportable) v;
					printable.export(appendable);
				} else {
					appendable.append(v.toString());
				}
			}
			lastFind = index + findText.length();
		}

		if (lastFind == 0) {
			appendable.append(text);
		} else {
			appendable.append(text.substring(lastFind));
		}
	}

	private final Object msg;
	private final Object[] args;
	private final String placeholder;

	public FormatableMessage(Object msg, String placeholder, Object[] args) {
		this.msg = msg;
		this.placeholder = placeholder;
		this.args = args;
	}

	public Object getMsg() {
		return msg;
	}

	public Object[] getArgs() {
		return args;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	@Override
	public void export(Appendable target) throws IOException {
		formatPlaceholder(target, msg, placeholder, args);
	}

	@Override
	public String get() {
		return toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			export(sb);
		} catch (IOException e) {
			// ignore
		}
		return sb.toString();
	}
}
