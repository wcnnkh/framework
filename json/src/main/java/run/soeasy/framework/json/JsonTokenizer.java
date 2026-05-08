package run.soeasy.framework.json;

import java.io.IOException;
import java.nio.CharBuffer;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.io.BufferFeeder;
import run.soeasy.framework.io.IOUtils;

/**
 * JSON 分词器（数字无校验版：修复 CharBuffer.wrap 越界异常）
 */
public final class JsonTokenizer implements AutoCloseable {
	// ===================== 常量定义 =====================
	private static final char MINUS = '-';
	private static final char DOT = '.';
	private static final char E_CHAR = 'e';
	private static final char E_UPPER_CHAR = 'E';
	private static final char PLUS = '+';
	private static final char ESCAPE_CHAR = '\\';

	private static final char[] TRUE_CHARS = "true".toCharArray();
	private static final char[] FALSE_CHARS = "false".toCharArray();
	private static final char[] NULL_CHARS = "null".toCharArray();
	private static final int TRUE_LENGTH = TRUE_CHARS.length;
	private static final int FALSE_LENGTH = FALSE_CHARS.length;
	private static final int NULL_LENGTH = NULL_CHARS.length;

	private static final int BATCH_READ_SIZE = IOUtils.DEFAULT_CHAR_BUFFER_SIZE;
	private static final int INITIAL_EXPAND_CAPACITY = 1024;

	private static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	// ===================== 核心字段 =====================
	@NonNull
	private final JsonReader jsonReader;
	@NonNull
	private final StringBuilder reusableBuffer;
	private int readIndex;
	private int writeIndex;
	private boolean closed = false;

	@Getter
	private JsonToken lastToken;
	@Getter
	private CharBuffer tokenValue;

	// ===================== 构造器 =====================
	public JsonTokenizer(@NonNull JsonReader jsonReader, @NonNull StringBuilder reusableBuffer) {
		this.jsonReader = jsonReader;
		this.reusableBuffer = reusableBuffer;
		this.readIndex = 0;
		this.writeIndex = 0;
	}

	public static JsonTokenizer create(@NonNull CharSequence json) {
		return create(CharBuffer.wrap(json)::read);
	}

	public static JsonTokenizer create(@NonNull BufferFeeder<? super CharBuffer> readable) {
		CharBuffer readBuffer = CharBuffer.allocate(IOUtils.DEFAULT_CHAR_BUFFER_SIZE);
		CharBuffer batchEscapingBuffer = CharBuffer.allocate(IOUtils.DEFAULT_CHAR_BUFFER_SIZE);
		JsonReader jsonReader = new JsonReader(readable, readBuffer, batchEscapingBuffer);
		StringBuilder sb = new StringBuilder(BATCH_READ_SIZE);
		return new JsonTokenizer(jsonReader, sb);
	}

	// ===================== 指数扩容 =====================
	private void ensureCapacity(int requiredCapacity) {
		int currentCapacity = this.reusableBuffer.capacity();
		if (currentCapacity >= requiredCapacity) {
			return;
		}
		int newCapacity = Math.max(requiredCapacity,
				currentCapacity < INITIAL_EXPAND_CAPACITY ? INITIAL_EXPAND_CAPACITY : currentCapacity << 1);
		this.reusableBuffer.ensureCapacity(newCapacity);
	}

	// ===================== 累积式批量读取（修复越界异常） =====================
	private boolean appendBatchData() {
		checkClosed();
		try {
			int requiredCapacity = writeIndex + BATCH_READ_SIZE;
			ensureCapacity(requiredCapacity);

			// 修复核心1：先将 StringBuilder 的 length 扩展到 capacity，确保包装区域有效
			// 因为 ensureCapacity 已扩容容量，此处扩展 length 不会触发数组拷贝
			if (this.reusableBuffer.length() < this.reusableBuffer.capacity()) {
				this.reusableBuffer.setLength(this.reusableBuffer.capacity());
			}

			// 修复核心2：修正 CharBuffer.wrap 的第三个参数（end = 合法结束索引）
			// 结束索引 = writeIndex + 剩余容量（此时 length = capacity，确保不越界）
			int remainingCapacity = this.reusableBuffer.capacity() - writeIndex;
			int endIndex = writeIndex + remainingCapacity; // 合法结束索引
			CharBuffer wrappedBuffer = CharBuffer.wrap(this.reusableBuffer, writeIndex, endIndex);

			int readLen = this.jsonReader.read(wrappedBuffer);
			if (readLen == IOUtils.EOF) {
				return false;
			}
			writeIndex += readLen;
			return true;
		} catch (IOException | CodecException e) {
			throw new JsonParseException("Failed to append batch data to reusable buffer", e);
		}
	}

	// ===================== 零拷贝包装 Token =====================
	private CharBuffer wrapTokenBuffer(int startIndex, int endIndex) {
		if (startIndex < readIndex || endIndex > writeIndex || startIndex > endIndex) {
			throw new IndexOutOfBoundsException(
					String.format("Invalid token range: [%d, %d) (readIndex: %d, writeIndex: %d)", startIndex, endIndex,
							readIndex, writeIndex));
		}
		int tokenLength = endIndex - startIndex;
		return CharBuffer.wrap(this.reusableBuffer, startIndex, tokenLength).asReadOnlyBuffer();
	}

	// ===================== 布尔 Token 解析 =====================
	private JsonToken parseBooleanToken(boolean expectTrue) {
		int expectedLength = expectTrue ? TRUE_LENGTH : FALSE_LENGTH;
		char[] expectedChars = expectTrue ? TRUE_CHARS : FALSE_CHARS;
		int endIndex = readIndex + expectedLength;

		if (endIndex > writeIndex) {
			this.tokenValue = null;
			return null;
		}

		for (int i = 0; i < expectedLength; i++) {
			if (this.reusableBuffer.charAt(readIndex + i) != expectedChars[i]) {
				throw new JsonParseException(String.format("Invalid boolean value at index %d", readIndex));
			}
		}

		this.tokenValue = wrapTokenBuffer(readIndex, endIndex);
		readIndex = endIndex;
		return JsonToken.BOOLEAN;
	}

	// ===================== Null Token 解析 =====================
	private JsonToken parseNullToken() {
		int endIndex = readIndex + NULL_LENGTH;

		if (endIndex > writeIndex) {
			this.tokenValue = null;
			return null;
		}

		for (int i = 0; i < NULL_LENGTH; i++) {
			if (this.reusableBuffer.charAt(readIndex + i) != NULL_CHARS[i]) {
				throw new JsonParseException(String.format("Invalid null value at index %d", readIndex));
			}
		}

		this.tokenValue = wrapTokenBuffer(readIndex, endIndex);
		readIndex = endIndex;
		return JsonToken.NULL;
	}

	// ===================== 字符串 Token 解析 =====================
	private JsonToken parseStringToken() {
		int startIndex = readIndex + 1;
		int endIndex = -1;
		int end = writeIndex;

		for (int i = startIndex; i < end; i++) {
			char c = this.reusableBuffer.charAt(i);
			if (c == JsonElement.QUOTE) {
				endIndex = i;
				break;
			}
			if (c == ESCAPE_CHAR && i + 1 < end) {
				i++;
			}
		}

		if (endIndex == -1) {
			this.tokenValue = null;
			return null;
		}

		this.tokenValue = wrapTokenBuffer(startIndex, endIndex);
		readIndex = endIndex + 1;
		return JsonToken.STRING;
	}

	// ===================== 数字 Token 解析 =====================
	private JsonToken parseNumberToken() {
		int startIndex = readIndex;
		int currentPos = startIndex;
		int end = writeIndex;

		if (currentPos < end && this.reusableBuffer.charAt(currentPos) == MINUS) {
			currentPos++;
		}

		while (currentPos < end && isDigit(this.reusableBuffer.charAt(currentPos))) {
			currentPos++;
		}

		if (currentPos < end && this.reusableBuffer.charAt(currentPos) == DOT) {
			currentPos++;
			while (currentPos < end && isDigit(this.reusableBuffer.charAt(currentPos))) {
				currentPos++;
			}
		}

		if (currentPos < end) {
			char c = this.reusableBuffer.charAt(currentPos);
			if (c == E_CHAR || c == E_UPPER_CHAR) {
				currentPos++;
				if (currentPos < end) {
					char sign = this.reusableBuffer.charAt(currentPos);
					if (sign == PLUS || sign == MINUS) {
						currentPos++;
					}
				}
				while (currentPos < end && isDigit(this.reusableBuffer.charAt(currentPos))) {
					currentPos++;
				}
			}
		}

		this.tokenValue = wrapTokenBuffer(startIndex, currentPos);
		readIndex = currentPos;
		return JsonToken.NUMBER;
	}

	// ===================== 解析单个 Token =====================
	private JsonToken parseSingleToken() {
		if (readIndex >= writeIndex) {
			this.tokenValue = null;
			return null;
		}

		char currChar = this.reusableBuffer.charAt(readIndex);
		JsonToken token = null;
		this.tokenValue = null;

		switch (currChar) {
		case JsonElement.BEGIN_OBJECT:
			this.tokenValue = wrapTokenBuffer(readIndex, readIndex + 1);
			token = JsonToken.BEGIN_OBJECT;
			readIndex++;
			break;
		case JsonElement.END_OBJECT:
			this.tokenValue = wrapTokenBuffer(readIndex, readIndex + 1);
			token = JsonToken.END_OBJECT;
			readIndex++;
			break;
		case JsonElement.BEGIN_ARRAY:
			this.tokenValue = wrapTokenBuffer(readIndex, readIndex + 1);
			token = JsonToken.BEGIN_ARRAY;
			readIndex++;
			break;
		case JsonElement.END_ARRAY:
			this.tokenValue = wrapTokenBuffer(readIndex, readIndex + 1);
			token = JsonToken.END_ARRAY;
			readIndex++;
			break;
		case JsonElement.VALUE_SEPARATOR:
			this.tokenValue = wrapTokenBuffer(readIndex, readIndex + 1);
			token = JsonToken.VALUE_SEPARATOR;
			readIndex++;
			break;
		case JsonElement.NAME_SEPARATOR:
			this.tokenValue = wrapTokenBuffer(readIndex, readIndex + 1);
			token = JsonToken.NAME_SEPARATOR;
			readIndex++;
			break;
		case JsonElement.QUOTE:
			token = parseStringToken();
			break;
		case MINUS:
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			token = parseNumberToken();
			break;
		case 't':
			token = parseBooleanToken(true);
			break;
		case 'f':
			token = parseBooleanToken(false);
			break;
		case 'n':
			token = parseNullToken();
			break;
		default:
			throw new JsonParseException(String.format("Unexpected character '%c' (ASCII: %d) at index %d", currChar,
					(int) currChar, readIndex));
		}

		return token;
	}

	// ===================== 对外核心方法 =====================
	public JsonToken readNextToken() {
		checkClosed();
		JsonToken token = null;

		while (token == null) {
			token = parseSingleToken();

			if (token != null) {
				if (readIndex > writeIndex / 2) {
					this.reusableBuffer.delete(0, readIndex);
					writeIndex -= readIndex;
					readIndex = 0;
				}
				this.lastToken = token;
				return token;
			}

			boolean hasMoreData = appendBatchData();
			if (!hasMoreData) {
				this.tokenValue = null;
				this.lastToken = JsonToken.EOF;
				return JsonToken.EOF;
			}
		}

		this.lastToken = token;
		return token;
	}

	// ===================== 辅助方法 =====================
	private void checkClosed() {
		if (this.closed) {
			throw new IllegalStateException("JsonTokenizer has been closed and cannot be reused");
		}
	}

	// ===================== 关闭方法 =====================
	@Override
	public void close() {
		if (!closed) {
			closed = true;
			this.jsonReader.close();
			this.reusableBuffer.setLength(0);
			this.readIndex = 0;
			this.writeIndex = 0;
			this.lastToken = null;
			this.tokenValue = null;
		}
	}
}