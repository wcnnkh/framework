package run.soeasy.framework.json;

import java.io.IOException;
import java.io.Serializable;
import java.nio.CharBuffer;
import java.util.Arrays;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.string.StringCodec;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.IOUtils;

/**
 * JSON 字符串转义/反转义编解码器
 */
public final class JsonEscaping implements StringCodec, Serializable {
	private static final long serialVersionUID = 1L;

	// ========== 全局单例 ==========
	public static final JsonEscaping INSTANCE = new JsonEscaping();

	// ========== 核心常量 ==========
	private static final char BACKSLASH = '\\';
	private static final char UNICODE_ESCAPE_MARKER = 'u';
	private static final char CONTROL_CHAR_END = '\u001F';
	private static final int UNICODE_HEX_LENGTH = 4;
	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };
	private static final int ESCAPE_MAP_LEN = 128;

	// ========== 映射表 ==========
	private static final char[][] ESCAPE_MAP;
	private static final char[] UNESCAPE_MAP;
	private static final byte[] HEX_VALUE_MAP;

	static {
		// 转义映射表（RFC 8259 规范）
		ESCAPE_MAP = new char[ESCAPE_MAP_LEN][];
		Arrays.fill(ESCAPE_MAP, null);
		ESCAPE_MAP['"'] = new char[] { '\\', '"' };
		ESCAPE_MAP['\\'] = new char[] { '\\', '\\' };
		ESCAPE_MAP['\b'] = new char[] { '\\', 'b' };
		ESCAPE_MAP['\f'] = new char[] { '\\', 'f' };
		ESCAPE_MAP['\n'] = new char[] { '\\', 'n' };
		ESCAPE_MAP['\r'] = new char[] { '\\', 'r' };
		ESCAPE_MAP['\t'] = new char[] { '\\', 't' };

		// 反转义映射表
		UNESCAPE_MAP = new char[ESCAPE_MAP_LEN];
		Arrays.fill(UNESCAPE_MAP, (char) -1);
		UNESCAPE_MAP['"'] = '"';
		UNESCAPE_MAP['\\'] = '\\';
		UNESCAPE_MAP['b'] = '\b';
		UNESCAPE_MAP['f'] = '\f';
		UNESCAPE_MAP['n'] = '\n';
		UNESCAPE_MAP['r'] = '\r';
		UNESCAPE_MAP['t'] = '\t';

		// 十六进制值映射表
		HEX_VALUE_MAP = new byte[ESCAPE_MAP_LEN];
		Arrays.fill(HEX_VALUE_MAP, (byte) -1);
		for (int i = '0'; i <= '9'; i++)
			HEX_VALUE_MAP[i] = (byte) (i - '0');
		for (int i = 'a'; i <= 'f'; i++)
			HEX_VALUE_MAP[i] = (byte) (10 + i - 'a');
		for (int i = 'A'; i <= 'F'; i++)
			HEX_VALUE_MAP[i] = (byte) (10 + i - 'A');
	}

	// ========== 私有构造器 ==========
	private JsonEscaping() {
	}
	
	/**
	 * 转义JSON字符
	 * 
	 * @param source 待转义的字符序列
	 * @return 转义后的CharBuffer
	 * @throws CodecException 入参为空或转义异常
	 */
	@Override
	public CharSequence encode(@NonNull CharSequence source) throws CodecException {
		return encodeBatch(source instanceof CharBuffer ? ((CharBuffer) source) : CharBuffer.wrap(source));
	}

	/**
	 * 反转义JSON字符
	 * 
	 * @param source 待反转义的字符序列
	 * @return 反转义后的CharBuffer
	 * @throws CodecException 入参为空或反转义异常
	 */
	@Override
	public CharSequence decode(@NonNull CharSequence source) throws CodecException {
		return decodeBatch(source instanceof CharBuffer ? ((CharBuffer) source) : CharBuffer.wrap(source));
	}
	
	/**
	 * 计算剩余字符最大转义长度
	 * 
	 * @param arr      字符数组
	 * @param startPos 起始位置
	 * @param len      长度
	 * @return 最大转义长度
	 */
	private static int calcMaxEscapedLenForRemaining(char[] arr, int startPos, int len) {
		int maxLen = 0;
		final int endPos = startPos + len;
		for (int i = startPos; i < endPos; i++) {
			final char c = arr[i];
			if (c >= ESCAPE_MAP_LEN || (ESCAPE_MAP[c] == null && c > CONTROL_CHAR_END)) {
				maxLen += 1;
			} else if (ESCAPE_MAP[c] != null) {
				maxLen += 2;
			} else {
				maxLen += 6;
			}
		}
		return maxLen;
	}

	/**
	 * 批量编码（支持Readable源）
	 */
	@Override
	public <X extends Exception> void encode(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull ThrowingConsumer<? super CharSequence, ? extends X> consumer)
			throws CodecException, IOException, X {
		IOUtils.transfer(source::read, buffer, (b) -> consumer.accept(encodeBatch(b)));
	}

	/**
	 * 批量解码（支持Readable源）
	 */
	@Override
	public <X extends Exception> void decode(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull ThrowingConsumer<? super CharSequence, ? extends X> consumer)
			throws CodecException, IOException, X {
		IOUtils.transfer(source::read, buffer, (b) -> consumer.accept(decodeBatch(b)));
	}

	/**
	 * 反序列化单例兼容
	 * 
	 * @return 单例实例
	 */
	private Object readResolve() {
		return INSTANCE;
	}
	
	/**
	 * 批量转义JSON字符（直接操作原CharBuffer的position/limit，修复无底层数组Bug）
	 * 
	 * @param source 待转义的源CharBuffer（会修改其position和limit）
	 * @return 转义后的CharSequence（优先返回CharBuffer，保证零拷贝）
	 * @throws CodecException 入参非法或转义异常
	 */
	public static CharSequence encodeBatch(@NonNull CharBuffer source) throws CodecException {
	    // 直接使用原source的position和limit，不创建副本（修改原缓冲区状态）
	    final int srcStartPos = source.position();
	    final int srcLimit = source.limit();
	    // 性能优化：优先操作底层char[]数组
	    final boolean srcHasArray = source.hasArray();
	    final char[] srcArray = srcHasArray ? source.array() : null;
	    final int srcArrayOffset = srcHasArray ? source.arrayOffset() : 0;

	    char[] outArr = null;
	    int outPos = 0;
	    int normalCharCount = 0;
	    int inPos = srcStartPos;

	    while (inPos < srcLimit) {
	        final char c;
	        // 优先从底层数组获取字符，提升性能
	        if (srcHasArray) {
	            c = srcArray[srcArrayOffset + inPos];
	        } else {
	            c = source.get(inPos);
	        }

	        // 普通字符（无需转义）
	        if (c >= ESCAPE_MAP_LEN || (ESCAPE_MAP[c] == null && c > CONTROL_CHAR_END)) {
	            if (outArr == null) {
	                normalCharCount++;
	            } else {
	                outArr[outPos++] = c;
	            }
	            inPos++;
	            continue;
	        }

	        // 按需初始化输出数组（延迟初始化，减少内存开销）
	        if (outArr == null) {
	            final int remainingLen = srcLimit - inPos;
	            final int maxRemainingLen;
	            // 计算剩余字符的最大转义长度
	            if (srcHasArray) {
	                maxRemainingLen = calcMaxEscapedLenForRemaining(srcArray, srcArrayOffset + inPos, remainingLen);
	            } else {
	                // 无底层数组时，临时拷贝字符数组用于计算（仅少量场景触发）
	                char[] tempArr = new char[remainingLen];
	                CharBuffer tempBuffer = source.duplicate();
	                tempBuffer.position(inPos);
	                tempBuffer.get(tempArr);
	                maxRemainingLen = calcMaxEscapedLenForRemaining(tempArr, 0, remainingLen);
	            }
	            outArr = new char[normalCharCount + maxRemainingLen];
	            // 拷贝前置普通字符
	            if (srcHasArray && normalCharCount > 0) {
	                System.arraycopy(srcArray, srcArrayOffset + srcStartPos, outArr, 0, normalCharCount);
	            } else if (normalCharCount > 0) {
	                CharBuffer normalBuffer = source.duplicate();
	                normalBuffer.position(srcStartPos);
	                normalBuffer.limit(srcStartPos + normalCharCount);
	                normalBuffer.get(outArr, 0, normalCharCount);
	            }
	            outPos = normalCharCount;
	        }

	        // 处理转义字符（保留原有RFC 8259规范逻辑）
	        final char[] escaped = ESCAPE_MAP[c];
	        if (escaped != null) {
	            // 按缓存行友好顺序赋值，提升性能
	            outArr[outPos++] = escaped[0];
	            outArr[outPos++] = escaped[1];
	        } else {
	            // 控制字符转义（\\uXXXX格式）
	            outArr[outPos++] = BACKSLASH;
	            outArr[outPos++] = UNICODE_ESCAPE_MARKER;
	            outArr[outPos++] = HEX_CHARS[(c >>> 12) & 0x0F];
	            outArr[outPos++] = HEX_CHARS[(c >>> 8) & 0x0F];
	            outArr[outPos++] = HEX_CHARS[(c >>> 4) & 0x0F];
	            outArr[outPos++] = HEX_CHARS[c & 0x0F];
	        }
	        inPos++;
	    }

	    // 更新原source的position到处理完成的位置（srcLimit）
	    source.position(srcLimit);

	    // 零拷贝返回：直接操作原source的pos/limit，无需依赖底层数组
	    if (outArr == null) {
	        CharBuffer resultBuffer = source.duplicate();
	        resultBuffer.position(srcStartPos);
	        resultBuffer.limit(srcLimit);
	        return resultBuffer.asReadOnlyBuffer();
	    } else {
	        return CharBuffer.wrap(outArr, 0, outPos);
	    }
	}
	
	/**
	 * 批量反转义JSON字符（直接操作原CharBuffer的position/limit，修复两个核心Bug）
	 * 
	 * @param source 待反转义的源CharBuffer（会修改其position和limit）
	 * @return 反转义后的CharSequence（优先返回CharBuffer，保证零拷贝）
	 * @throws CodecException 入参非法或反转义异常（无效转义/不完整序列）
	 */
	public static CharSequence decodeBatch(@NonNull CharBuffer source) throws CodecException {
	    // 直接使用原source的position和limit，不创建副本（修改原缓冲区状态）
	    final int srcStartPos = source.position();
	    final int srcLimit = source.limit();
	    final int srcLen = srcLimit - srcStartPos;

	    // 性能优化：优先操作底层char[]数组
	    final boolean srcHasArray = source.hasArray();
	    final char[] srcArray = srcHasArray ? source.array() : null;
	    final int srcArrayOffset = srcHasArray ? source.arrayOffset() : 0;

	    char[] outArr = null;
	    int outPos = 0;
	    int normalCharCount = 0;
	    int inPos = srcStartPos;

	    while (inPos < srcLimit) {
	        final char c;
	        // 优先从底层数组获取字符，减少缓冲区操作开销
	        if (srcHasArray) {
	            c = srcArray[srcArrayOffset + inPos];
	        } else {
	            c = source.get(inPos);
	        }

	        // 普通字符（非反斜杠，无需反转义）
	        if (c != BACKSLASH) {
	            if (outArr == null) {
	                normalCharCount++;
	            } else {
	                outArr[outPos++] = c;
	            }
	            inPos++;
	            continue;
	        }

	        // 按需初始化输出数组（延迟初始化，减少内存开销）
	        if (outArr == null) {
	            outArr = new char[srcLen]; // 反转义后长度不会超过原长度，直接用原长度初始化
	            // 拷贝前置普通字符
	            if (srcHasArray && normalCharCount > 0) {
	                System.arraycopy(srcArray, srcArrayOffset + srcStartPos, outArr, 0, normalCharCount);
	            } else if (normalCharCount > 0) {
	                CharBuffer normalBuffer = source.duplicate();
	                normalBuffer.position(srcStartPos);
	                normalBuffer.limit(srcStartPos + normalCharCount);
	                normalBuffer.get(outArr, 0, normalCharCount);
	            }
	            outPos = normalCharCount;
	        }

	        // 处理转义字符（反斜杠后续逻辑）
	        inPos++;
	        if (inPos >= srcLimit) {
	            throw new CodecException("Incomplete escape sequence, position: " + inPos);
	        }

	        final char escapeChar;
	        if (srcHasArray) {
	            escapeChar = srcArray[srcArrayOffset + inPos];
	        } else {
	            escapeChar = source.get(inPos);
	        }

	        // 普通反转义（匹配RFC 8259规范的转义字符）
	        if (escapeChar < ESCAPE_MAP_LEN && UNESCAPE_MAP[escapeChar] != (char) -1) {
	            outArr[outPos++] = UNESCAPE_MAP[escapeChar];
	            inPos++;
	        }
	        // Unicode反转义（\\uXXXX格式，修复冗余逻辑Bug）
	        else if (escapeChar == UNICODE_ESCAPE_MARKER) {
	            inPos++;
	            if (inPos + UNICODE_HEX_LENGTH > srcLimit) {
	                throw new CodecException("Incomplete Unicode escape sequence, position: " + inPos
	                        + ", remaining length: " + (srcLimit - inPos));
	            }

	            // 直接获取4个十六进制字符（无冗余变量，提升性能）
	            final char h0 = srcHasArray ? srcArray[srcArrayOffset + inPos] : source.get(inPos);
	            final char h1 = srcHasArray ? srcArray[srcArrayOffset + inPos + 1] : source.get(inPos + 1);
	            final char h2 = srcHasArray ? srcArray[srcArrayOffset + inPos + 2] : source.get(inPos + 2);
	            final char h3 = srcHasArray ? srcArray[srcArrayOffset + inPos + 3] : source.get(inPos + 3);

	            // 校验十六进制字符合法性：修复冗余逻辑，直接用已获取的h0-h3
	            if (h0 >= ESCAPE_MAP_LEN || HEX_VALUE_MAP[h0] == -1 || h1 >= ESCAPE_MAP_LEN
	                    || HEX_VALUE_MAP[h1] == -1 || h2 >= ESCAPE_MAP_LEN || HEX_VALUE_MAP[h2] == -1
	                    || h3 >= ESCAPE_MAP_LEN || HEX_VALUE_MAP[h3] == -1) {
	                final char[] hexSeqArr = new char[] {h0, h1, h2, h3};
	                final String hexSeq = new String(hexSeqArr);
	                throw new CodecException(
	                        "Invalid Unicode hex character, full sequence: " + hexSeq + ", position: " + inPos);
	            }

	            // 优化移位计算（无冗余临时变量，提升性能）
	            final int codePoint = (HEX_VALUE_MAP[h0] << 12) | (HEX_VALUE_MAP[h1] << 8)
	                    | (HEX_VALUE_MAP[h2] << 4) | HEX_VALUE_MAP[h3];

	            if (codePoint > Character.MAX_VALUE) {
	                // 修复冗余逻辑，直接用已获取的h0-h3构建异常信息
	                final char[] hexSeqArr = new char[] {h0, h1, h2, h3};
	                final String hexSeq = new String(hexSeqArr);
	                throw new CodecException(
	                        "Unicode value exceeds BMP range: \\u" + hexSeq + " (code value: " + codePoint + ")");
	            }
	            outArr[outPos++] = (char) codePoint;
	            inPos += UNICODE_HEX_LENGTH;
	        }
	        // 无效转义字符
	        else {
	            throw new CodecException("Invalid escape character: \\" + escapeChar + ", position: " + inPos);
	        }
	    }

	    // 更新原source的position到处理完成的位置（srcLimit）
	    source.position(srcLimit);

	    // 零拷贝返回：直接操作原source的pos/limit，无需依赖底层数组
	    if (outArr == null) {
	        CharBuffer resultBuffer = source.duplicate();
	        resultBuffer.position(srcStartPos);
	        resultBuffer.limit(srcLimit);
	        return resultBuffer.asReadOnlyBuffer();
	    } else {
	        return CharBuffer.wrap(outArr, 0, outPos);
	    }
	}
}