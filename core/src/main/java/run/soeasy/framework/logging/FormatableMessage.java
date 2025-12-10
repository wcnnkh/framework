package run.soeasy.framework.logging;

import java.io.IOException;
import java.io.Serializable;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.io.Exportable;

/**
 * 可格式化消息类，支持带占位符的消息格式化和导出，
 * 实现{@link Exportable}、{@link Serializable}和{@link Supplier}接口，
 * 适用于日志消息、错误信息等需要动态填充参数的场景。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>占位符替换：支持自定义占位符（默认`{}`）的消息格式化</li>
 * <li>类型适配：自动处理{@link Exportable}类型参数的导出</li>
 * <li>链式调用：实现Supplier接口，支持函数式编程场景</li>
 * <li>序列化支持：实现Serializable接口，可用于远程日志传输</li>
 * </ul>
 * 
 * <p>
 * <b>格式化规则：</b>
 * <ul>
 * <li>占位符匹配：按顺序替换消息中的占位符（如`{}`或自定义占位符）</li>
 * <li>空值处理：null参数转换为"null"字符串</li>
 * <li>类型处理：{@link Exportable}类型参数调用{@link Exportable#export(Appendable)}</li>
 * <li>多余参数：忽略超过占位符数量的参数</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see Exportable
 * @see Supplier
 */
@Getter
public final class FormatableMessage implements Exportable, Serializable, Supplier<String> {
	private static final long serialVersionUID = 1L;
	/** 默认占位符（{}），用于标识参数替换位置 */
	private static final String PLACEHOLDER = "{}";

	/**
	 * 格式化消息字符串（静态工厂方法）。
	 * <p>
	 * 使用默认占位符({@link #PLACEHOLDER})将参数填充到消息中，
	 * 内部调用{@link #formatPlaceholder(Appendable, Object, String, Object...)}。
	 * 
	 * @param text 消息模板（如"操作{0}失败，原因：{1}"）
	 * @param args 替换参数
	 * @return 格式化后的消息字符串
	 */
	public static String formatPlaceholder(Object text, Object... args) {
		return formatPlaceholder(text, PLACEHOLDER, args);
	}

	/**
	 * 格式化消息字符串（支持自定义占位符）。
	 * <p>
	 * 使用指定占位符将参数填充到消息中， 内部通过StringBuilder实现高效字符串拼接。
	 * 
	 * @param text        消息模板
	 * @param placeholder 占位符（如"{}"或"[]"）
	 * @param args        替换参数
	 * @return 格式化后的消息字符串
	 */
	public static String formatPlaceholder(Object text, String placeholder, Object... args) {
		StringBuilder sb = new StringBuilder();
		try {
			formatPlaceholder(sb, text, placeholder, args);
		} catch (IOException e) {
			throw new IllegalStateException("Internal IO exception", e);
		}
		return sb.toString();
	}

	/**
	 * 格式化消息并写入Appendable（支持异常抛出）。
	 * <p>
	 * 按顺序替换消息中的占位符，支持{@link Exportable}类型参数的特殊处理。
	 * 
	 * @param appendable  目标写入器
	 * @param format      消息模板
	 * @param placeholder 占位符
	 * @param args        替换参数
	 * @throws IOException 当写入Appendable时发生IO错误
	 */
	public static void formatPlaceholder(@NonNull Appendable appendable, Object template, String placeholder,
			Object... args) throws IOException {
		String text = template == null ? null : template.toString();
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
			} else if (v instanceof Exportable) {
				((Exportable) v).export(appendable);
			} else {
				appendable.append(v.toString());
			}
			lastFind = index + findText.length();
		}

		if (lastFind == 0) {
			appendable.append(text);
		} else {
			appendable.append(text.substring(lastFind));
		}
	}

	/** 消息模板（支持占位符） */
	private final Object template;
	/** 替换参数数组 */
	private final Object[] args;
	/** 占位符字符串（默认{@link #PLACEHOLDER}） */
	private final String placeholder;

	/**
	 * 构造可格式化消息实例。
	 * 
	 * @param msg         消息模板（如"用户{0}访问{1}失败"）
	 * @param placeholder 占位符（如"{}"）
	 * @param args        替换参数（如new Object[]{"admin", "API"}）
	 */
	public FormatableMessage(Object template, String placeholder, Object[] args) {
		this.template = template;
		this.placeholder = placeholder;
		this.args = args;
	}

	/**
	 * 导出消息到Appendable（实现{@link Exportable}）。
	 * <p>
	 * 调用{@link #formatPlaceholder(Appendable, Object, String, Object...)}
	 * 实现消息格式化和写入。
	 * 
	 * @param target 目标写入器
	 * @throws IOException 当写入时发生IO错误
	 */
	@Override
	public void export(Appendable target) throws IOException {
		formatPlaceholder(target, template, placeholder, args);
	}

	/**
	 * 获取格式化后的消息字符串（实现{@link Supplier}）。
	 * <p>
	 * 等价于调用{@link #toString()}。
	 * 
	 * @return 格式化后的消息
	 */
	@Override
	public String get() {
		return toString();
	}

	/**
	 * 返回格式化后的消息字符串。
	 * 
	 * @return 格式化后的消息
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			export(sb);
		} catch (IOException e) {
			throw new IllegalStateException("Internal IO exception", e);
		}
		return sb.toString();
	}

	public static FormatableMessage create(Object template, Object... args) {
		return new FormatableMessage(template, PLACEHOLDER, args);
	}
}