package run.soeasy.framework.json;

import run.soeasy.framework.codec.CodecException;

/**
 * JSON解析异常（生产级），封装JSON解析过程中的所有错误（语法错误、转义错误、IO错误等）
 * <p>
 * 核心特性：
 * <ul>
 * <li>继承{@link CodecException}，融入编解码异常体系；
 * <li>封装「行号/列号」字段，支持程序级获取解析错误的精准位置；
 * <li>提供多套构造方法，覆盖所有使用场景（兼容原有代码）；
 * <li>重写{@link #getMessage()}，自动拼接行号/列号到异常消息，提升可读性。
 * </ul>
 * 
 * @author soeasy.run
 * @see CodecException
 */
public class JsonParseException extends CodecException {
	private static final long serialVersionUID = 1L;

	/** 解析错误所在行号（从1开始，-1表示未知） */
	private final int lineNumber;
	/** 解析错误所在列号（从1开始，-1表示未知） */
	private final int columnNumber;

	// ===================== 兼容原有构造方法（无位置信息） =====================
	public JsonParseException(String message) {
		this(message, -1, -1);
	}

	public JsonParseException(String message, Throwable cause) {
		this(message, cause, -1, -1);
	}

	// ===================== 新增：带位置信息的构造方法（生产级核心） =====================
	/**
	 * 构造带行号/列号的JSON解析异常
	 * 
	 * @param message      错误消息
	 * @param lineNumber   行号（从1开始）
	 * @param columnNumber 列号（从1开始）
	 */
	public JsonParseException(String message, int lineNumber, int columnNumber) {
		super(buildMessage(message, lineNumber, columnNumber));
		this.lineNumber = normalizePosition(lineNumber);
		this.columnNumber = normalizePosition(columnNumber);
	}

	/**
	 * 构造带行号/列号+根因的JSON解析异常
	 * 
	 * @param message      错误消息
	 * @param cause        根异常（如IO异常、转义异常）
	 * @param lineNumber   行号（从1开始）
	 * @param columnNumber 列号（从1开始）
	 */
	public JsonParseException(String message, Throwable cause, int lineNumber, int columnNumber) {
		super(buildMessage(message, lineNumber, columnNumber), cause);
		this.lineNumber = normalizePosition(lineNumber);
		this.columnNumber = normalizePosition(columnNumber);
	}

	/**
	 * 构造仅带根因的JSON解析异常（兼容标准异常设计）
	 * 
	 * @param cause 根异常
	 */
	public JsonParseException(Throwable cause) {
		super(cause);
		this.lineNumber = -1;
		this.columnNumber = -1;
	}

	// ===================== 工具方法：标准化位置/构建消息 =====================
	/**
	 * 标准化位置值（确保行号/列号≥-1，-1表示未知）
	 */
	private int normalizePosition(int position) {
		return position < -1 ? -1 : position;
	}

	/**
	 * 构建带位置信息的异常消息
	 */
	private static String buildMessage(String baseMessage, int lineNumber, int columnNumber) {
		if (lineNumber <= 0 && columnNumber <= 0) {
			return baseMessage; // 无位置信息，直接返回原始消息
		}
		StringBuilder sb = new StringBuilder(baseMessage);
		sb.append(" (");
		if (lineNumber > 0) {
			sb.append("line: ").append(lineNumber);
			if (columnNumber > 0) {
				sb.append(", column: ").append(columnNumber);
			}
		} else if (columnNumber > 0) {
			sb.append("column: ").append(columnNumber);
		}
		sb.append(")");
		return sb.toString();
	}

	// ===================== Getter：程序级获取位置信息 =====================
	/**
	 * 获取错误所在行号
	 * 
	 * @return 行号（从1开始），-1表示未知
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * 获取错误所在列号
	 * 
	 * @return 列号（从1开始），-1表示未知
	 */
	public int getColumnNumber() {
		return columnNumber;
	}

	// ===================== 重写getMessage：兼容原有逻辑 + 位置信息 =====================
	@Override
	public String getMessage() {
		// 若构造时已拼接位置信息，直接返回父类message；否则补充位置（兼容无参构造）
		String baseMsg = super.getMessage();
		if ((lineNumber > 0 || columnNumber > 0) && !baseMsg.contains("(line: ") && !baseMsg.contains("column: ")) {
			return buildMessage(baseMsg, lineNumber, columnNumber);
		}
		return baseMsg;
	}
}