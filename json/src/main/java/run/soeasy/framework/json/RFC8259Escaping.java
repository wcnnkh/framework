package run.soeasy.framework.json;

import run.soeasy.framework.codec.CodecException;

/**
 * 遵循RFC 8259 JSON规范的字符串转义/反转义编解码器，实现{@link JsonEscaping}接口，
 * 提供对称的encode（转义）和decode（反转义）能力，严格对齐<a href=
 * "https://datatracker.ietf.org/doc/html/rfc8259">RFC 8259 §7</a>规范要求。
 * 
 * <h3>核心特性</h3>
 * <ul>
 * <li>转义：将原始字符串转为符合JSON语法的转义字符串，覆盖强制转义字符+控制字符的Unicode转义</li>
 * <li>反转义：将JSON转义字符串还原为原始字符串，严格校验转义序列合法性</li>
 * <li>性能优化：预分配缓冲区、减少扩容、高效字符遍历，时间复杂度均为O(n)</li>
 * <li>线程安全：无状态设计，可通过{@link #INSTANCE}常量复用实例</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see JsonEscaping
 * @see CodecException
 */
public class RFC8259Escaping implements JsonEscaping {

	/**
	 * RFC8259转义/反转义编解码器默认实例（无状态，线程安全，推荐直接复用）
	 */
	public static final RFC8259Escaping INSTANCE = new RFC8259Escaping();

	/**
	 * RFC 8259强制要求转义的字符集合
	 * <p>
	 * 包含：双引号(")、反斜杠(\)、退格(\b)、换页(\f)、换行(\n)、回车(\r)、制表符(\t)
	 * </p>
	 */
	public static final char[] FORCE_ESCAPE_CHARS = { '"', '\\', '\b', '\f', '\n', '\r', '\t' };

	/**
	 * 控制字符范围起始值（U+0000）
	 */
	public static final char CONTROL_CHAR_START = '\u0000';

	/**
	 * 控制字符范围结束值（U+001F）
	 */
	public static final char CONTROL_CHAR_END = '\u001F';

	/**
	 * 构造器
	 */
	public RFC8259Escaping() {
	}

	/**
	 * 对JSON字符串值进行语法合规的转义处理，严格遵循RFC 8259 JSON规范要求
	 * 
	 * <h4>JSON规范强制转义的字符（RFC 8259 §7）</h4>
	 * <ul>
	 * <li>双引号（"）：转义为 {@code \"} - 避免闭合JSON字符串字面量</li>
	 * <li>反斜杠（\）：转义为 {@code \\} - 避免转义序列被误解析</li>
	 * <li>退格符（\b，U+0008）：转义为 {@code \b}</li>
	 * <li>换页符（\f，U+000C）：转义为 {@code \f}</li>
	 * <li>换行符（\n，U+000A）：转义为 {@code \n}</li>
	 * <li>回车符（\r，U+000D）：转义为 {@code \r}</li>
	 * <li>制表符（\t，U+0009）：转义为 {@code \t}</li>
	 * </ul>
	 * 
	 * <h4>JSON规范建议转义的字符</h4>
	 * <ul>
	 * <li>控制字符（U+0000 至 U+001F）：除上述6种外，其余转为 {@code \\uXXXX} 形式的Unicode转义</li>
	 * <li>非ASCII字符：可直接保留（JSON原生支持UTF-8编码），无需转义</li>
	 * </ul>
	 * 
	 * <h4>性能优化</h4>
	 * <ul>
	 * <li>预分配1.25倍原始长度的缓冲区，减少{StringBuilder}扩容次数；
	 * 同时限制缓冲区初始容量最大值为{Integer#MAX_VALUE}，避免超长字符串导致容量计算溢出</li>
	 * <li>逐字符遍历+switch-case判断，时间复杂度O(n)，空间复杂度O(n)（n为输入字符串长度）</li>
	 * </ul>
	 * 
	 * <h4>边界场景处理</h4>
	 * <ul>
	 * <li>空字符串：输入""时，直接返回""</li>
	 * <li>null输入：抛出{@link CodecException}（编解码不支持null值）</li>
	 * <li>超长字符串：初始容量限制为{Integer#MAX_VALUE}，依赖{StringBuilder}自动扩容兜底</li>
	 * </ul>
	 * 
	 * @param value 待转义的原始字符串（非null）
	 * @return 完全符合RFC 8259规范的转义后字符串，可直接嵌入JSON结构中使用
	 * @throws CodecException 输入为null，或转义过程中出现异常时抛出
	 */
	@Override
	public String encode(String value) throws CodecException {
		if (value == null) {
			throw new CodecException("Encode failed: input string is null");
		}
		int size = value.length();
		if (size == 0) {
			return "";
		}

		double targetCapacity = size * 1.25;
		int initialCapacity = targetCapacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.round(targetCapacity);
		StringBuilder sb = new StringBuilder(initialCapacity);

		for (int i = 0; i < size; i++) {
			char currentChar = value.charAt(i);
			switch (currentChar) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				if (currentChar >= CONTROL_CHAR_START && currentChar <= CONTROL_CHAR_END) {
					sb.append(String.format("\\u%04x", (int) currentChar));
				} else {
					sb.append(currentChar);
				}
				break;
			}
		}

		return sb.toString();
	}

	/**
	 * JSON字符串反转义处理（encode的逆向操作），严格遵循RFC 8259规范
	 * 
	 * <h4>反转义核心规则（与转义规则一一对应）</h4>
	 * <ul>
	 * <li>{@code \"} → " （双引号）</li>
	 * <li>{@code \\} → \ （反斜杠）</li>
	 * <li>{@code \b} → \b（退格符，U+0008）</li>
	 * <li>{@code \f} → \f（换页符，U+000C）</li>
	 * <li>{@code \n} → \n（换行符，U+000A）</li>
	 * <li>{@code \r} → \r（回车符，U+000D）</li>
	 * <li>{@code \t} → \t（制表符，U+0009）</li>
	 * <li>{@code \\uXXXX} → 对应Unicode字符（XXXX为4位十六进制数）</li>
	 * </ul>
	 * 
	 * <h4>边界场景处理</h4>
	 * <ul>
	 * <li>无效转义序列：如"{@code \z}"，抛出{@link CodecException}（含具体错误位置）</li>
	 * <li>不完整Unicode转义：如"{@code \\u123}"（仅3位十六进制），抛出{@link CodecException}</li>
	 * <li>转义符在末尾：如"abc\\"，抛出{@link CodecException}</li>
	 * <li>空字符串：输入""时，直接返回""</li>
	 * <li>null输入：抛出{@link CodecException}（编解码不支持null值）</li>
	 * </ul>
	 * 
	 * @param escapedValue 已转义的JSON字符串（非null）
	 * @return 还原后的原始字符串
	 * @throws CodecException 输入为null、无效转义序列、不完整Unicode转义时抛出，含详细错误信息
	 */
	@Override
	public String decode(String escapedValue) throws CodecException {
		if (escapedValue == null) {
			throw new CodecException("Decode failed: input string is null");
		}
		int length = escapedValue.length();
		if (length == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder(length);
		int index = 0;

		while (index < length) {
			char currentChar = escapedValue.charAt(index);
			if (currentChar == '\\') {
				if (index + 1 >= length) {
					throw new CodecException(
							"Invalid escape sequence: backslash at end of string (index: " + index + ")");
				}

				char escapeChar = escapedValue.charAt(++index);
				switch (escapeChar) {
				case '"':
					sb.append('"');
					break;
				case '\\':
					sb.append('\\');
					break;
				case 'b':
					sb.append('\b');
					break;
				case 'f':
					sb.append('\f');
					break;
				case 'n':
					sb.append('\n');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'u':
					if (index + 4 > length) {
						throw new CodecException(
								"Incomplete Unicode escape sequence: " + escapedValue.substring(index - 1));
					}
					String hexStr = escapedValue.substring(index + 1, index + 5);
					try {
						int unicodeCodePoint = Integer.parseInt(hexStr, 16);
						sb.append((char) unicodeCodePoint);
					} catch (NumberFormatException e) {
						throw new CodecException(
								"Invalid Unicode escape sequence: \\u" + hexStr + " (invalid hex digits)", e);
					}
					index += 4;
					break;
				default:
					throw new CodecException(
							"Invalid escape character: \\" + escapeChar + " (index: " + (index - 1) + ")");
				}
			} else {
				sb.append(currentChar);
			}
			index++;
		}

		return sb.toString();
	}
}