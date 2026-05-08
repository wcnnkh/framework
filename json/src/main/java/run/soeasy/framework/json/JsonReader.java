package run.soeasy.framework.json;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.buffer.JsonEscaping;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BufferFeeder;
import run.soeasy.framework.io.IOUtils;

/**
 * JSON 专用读取器
 */
public final class JsonReader extends Reader {
	// ===================== 通用常量（无硬编码） =====================
	/** 斜杠字符：注释起始标记 */
	private static final char SLASH = '/';
	/** 星号字符：多行注释标记 */
	private static final char STAR = '*';
	/** 空CharBuffer常量：复用避免频繁创建临时对象 */
	private static final CharBuffer EMPTY_CHAR_BUFFER = CharBuffer.allocate(0).asReadOnlyBuffer();

	// ===================== 空白字符常量（统一管理，无硬编码） =====================
	/** 制表符：JSON合法空白字符 */
	private static final char TAB = '\t';
	/** 换行符：JSON合法空白字符 & 单行注释结束标记 */
	private static final char NEWLINE = '\n';
	/** 回车符：JSON合法空白字符 */
	private static final char CARRIAGE_RETURN = '\r';

	// ===================== 缓冲区相关字段 =====================
	/** 数据读取源：提供待读取的CharBuffer数据 */
	private final BufferFeeder<? super CharBuffer> readable;
	/** 原始读取缓冲区：存储待处理的原始字符数据 */
	private final CharBuffer readBuffer;
	/** 批量转义缓冲区：存储批量有效字符及转义残留数据 */
	private final CharBuffer batchEscapingBuffer;

	/** 读取缓冲区底层字符数组：堆缓冲区有效，直接缓冲区为null */
	private final char[] readBufferArray;
	/** 批量转义缓冲区底层字符数组：堆缓冲区有效，直接缓冲区为null */
	private final char[] batchEscapingBufferArray;
	/** 是否为堆读取缓冲区：标记readBuffer是否为堆缓冲区 */
	private final boolean isReadBufferHeap;
	/** 是否为堆批量转义缓冲区：标记batchEscapingBuffer是否为堆缓冲区 */
	private final boolean isBatchEscapingBufferHeap;
	/** 读取缓冲区数组偏移量：堆缓冲区有效，直接缓冲区为0 */
	private final int readBufferArrayOffset;
	/** 批量转义缓冲区数组偏移量：堆缓冲区有效，直接缓冲区为0 */
	private final int batchEscapingBufferArrayOffset;

	// ===================== 状态相关字段 =====================
	/** 读取器关闭状态：true表示已关闭，false表示正常可用 */
	private boolean closed = false;
	/** JSON字符串内部状态：true表示处于字符串内，false表示字符串外 */
	private boolean inString = false;
	/** 读取缓冲区当前位置：标记下一个待读取字符的索引 */
	private int bufferPos = 0;
	/** 读取缓冲区限制位置：标记有效字符的结束索引 */
	private int bufferLimit = 0;
	/** 当前读取字符：存储最近一次读取的字符，EOF表示无可用字符 */
	private int currentChar = IOUtils.EOF;

	/** 标记缓冲区位置：mark方法调用时记录的bufferPos */
	private int markedBufferPos = -1;
	/** 标记字符：mark方法调用时记录的currentChar */
	private int markedChar = IOUtils.EOF;
	/** 标记字符串状态：mark方法调用时记录的inString */
	private boolean markedInString = false;

	/** 转义残留起始位置：在batchEscapingBuffer中的起始索引，-1表示无残留 */
	private int escapeRemainStart = -1;
	/** 转义残留有效长度：残留数据的字符个数，0表示无残留 */
	private int escapeRemainLen = 0;

	// ===================== 构造器 =====================
	/**
	 * 构造JSON读取器
	 * 
	 * @param readable            数据读取源
	 * @param readBuffer          原始读取缓冲区
	 * @param batchEscapingBuffer 批量转义缓冲区
	 */
	public JsonReader(@NonNull BufferFeeder<? super CharBuffer> readable, @NonNull CharBuffer readBuffer,
			@NonNull CharBuffer batchEscapingBuffer) {
		this.readable = readable;
		this.readBuffer = readBuffer;
		this.batchEscapingBuffer = batchEscapingBuffer;
		this.bufferPos = readBuffer.position();
		this.bufferLimit = readBuffer.limit();

		this.isReadBufferHeap = readBuffer.hasArray();
		this.readBufferArray = isReadBufferHeap ? readBuffer.array() : null;
		this.readBufferArrayOffset = isReadBufferHeap ? readBuffer.arrayOffset() : 0;

		this.isBatchEscapingBufferHeap = batchEscapingBuffer.hasArray();
		this.batchEscapingBufferArray = isBatchEscapingBufferHeap ? batchEscapingBuffer.array() : null;
		this.batchEscapingBufferArrayOffset = isBatchEscapingBufferHeap ? batchEscapingBuffer.arrayOffset() : 0;
	}

	/**
	 * 0拷贝读取：仅传递只读视图给消费者，杜绝数据修改风险，保证状态一致性
	 * 
	 * @param maxSize  最大处理字符数，非负整数（0返回0，负数抛出异常）
	 * @param consumer 字符缓冲区消费者（仅能读取只读视图，无法修改数据/状态）
	 * @param <E>      消费者允许抛出的自定义异常类型
	 * @return 实际处理的字符个数，IOUtils.EOF表示无可用字符
	 * @throws IOException              读取过程中发生I/O异常
	 * @throws CodecException           转义处理过程中发生编码异常
	 * @throws IllegalArgumentException 当maxSize为负数时抛出
	 * @throws NullPointerException     当consumer为null时抛出
	 * @throws E                        消费者抛出的自定义异常
	 */
	public <E extends Throwable> int read(int maxSize, @NonNull ThrowingConsumer<? super CharBuffer, ? extends E> consumer)
			throws IOException, CodecException, E {
		// 1. 前置合法性校验（保持原有规范，杜绝无效调用）
		checkClosed();
		if (maxSize < 0) {
			throw new IllegalArgumentException("maxSize cannot be negative: " + maxSize);
		}
		if (maxSize == 0 || isEOF()) {
			return isEOF() && maxSize == 0 ? IOUtils.EOF : 0;
		}

		// 总处理字符数 & 剩余可处理字符数（严格控制不超过maxSize）
		int totalProcessCount = 0;
		int remainingMax = maxSize;

		// 2. 优先处理转义残留数据（只读视图 + 0拷贝 + 状态隔离）
		if (escapeRemainLen > 0 && remainingMax > 0) {
			CharBuffer remainBuffer = getEscapeRemainBuffer();
			if (remainBuffer.hasRemaining()) {
				// 计算本次可处理的字符数（取剩余数据和剩余maxSize的最小值）
				int processLen = Math.min(remainBuffer.remaining(), remainingMax);
				int origPos = remainBuffer.position();

				// 步骤1：duplicate() 创建状态独立视图（共享底层数据，0拷贝，不污染原始缓冲区状态）
				CharBuffer stateIsolatedView = remainBuffer.duplicate();
				// 步骤2：调整临时视图的范围，精准截取本次有效数据（仅修改临时视图，与原始缓冲区解耦）
				stateIsolatedView.position(origPos);
				stateIsolatedView.limit(origPos + processLen);
				// 步骤3：转为只读视图（核心！消费者无法修改数据，仅能读取，避免错误）
				CharBuffer readOnlyView = stateIsolatedView.asReadOnlyBuffer();

				// 传递只读视图给消费者（安全 + 0拷贝）
				consumer.accept(readOnlyView);

				// 更新原始缓冲区状态（记录已处理进度，不影响其他逻辑）
				totalProcessCount += processLen;
				remainingMax -= processLen;
				remainBuffer.position(origPos + processLen);

				// 重置/存储剩余残留数据（保持原有状态逻辑）
				escapeRemainStart = -1;
				escapeRemainLen = 0;
				if (remainBuffer.hasRemaining()) {
					storeEscapeRemain(remainBuffer);
				}
			} else {
				escapeRemainStart = -1;
				escapeRemainLen = 0;
			}

			// 剩余容量耗尽，直接返回
			if (remainingMax <= 0) {
				return totalProcessCount;
			}
		}

		// 3. 处理批量转义缓冲区中的已有数据（同上：只读视图 + 0拷贝 + 状态隔离）
		if (escapeRemainLen == 0 && batchEscapingBuffer.position() > 0 && remainingMax > 0) {
			batchEscapingBuffer.flip();
			CharBuffer decodedBuffer;

			// 0拷贝解码：堆缓冲区直接使用底层数组视图，直接缓冲区使用共享视图
			if (isBatchEscapingBufferHeap) {
				char[] batchArr = batchEscapingBufferArray;
				int batchOffset = batchEscapingBufferArrayOffset;
				int batchLen = batchEscapingBuffer.remaining();
				decodedBuffer = JsonEscaping.decodeBatch(batchArr, batchOffset, batchLen);
			} else {
				decodedBuffer = JsonEscaping.INSTANCE.decode(batchEscapingBuffer);
			}

			if (decodedBuffer.hasRemaining()) {
				int processLen = Math.min(decodedBuffer.remaining(), remainingMax);
				int origPos = decodedBuffer.position();

				// 先状态隔离，再截取范围，最后转为只读（安全三步法）
				CharBuffer stateIsolatedView = decodedBuffer.duplicate();
				stateIsolatedView.position(origPos);
				stateIsolatedView.limit(origPos + processLen);
				CharBuffer readOnlyView = stateIsolatedView.asReadOnlyBuffer();

				// 消费者安全消费只读视图
				consumer.accept(readOnlyView);

				// 更新状态
				totalProcessCount += processLen;
				remainingMax -= processLen;
				decodedBuffer.position(origPos + processLen);
			}

			// 清空批量转义缓冲区，0拷贝存储解码残留
			batchEscapingBuffer.clear();
			if (decodedBuffer.hasRemaining()) {
				if (isBatchEscapingBufferHeap && decodedBuffer.hasArray()) {
					char[] srcArr = decodedBuffer.array();
					int srcOffset = decodedBuffer.arrayOffset() + decodedBuffer.position();
					int copyLen = decodedBuffer.remaining();
					System.arraycopy(srcArr, srcOffset, batchEscapingBufferArray, batchEscapingBufferArrayOffset,
							copyLen);
					batchEscapingBuffer.position(copyLen);
				} else {
					batchEscapingBuffer.put(decodedBuffer);
				}
			}

			if (remainingMax <= 0) {
				return totalProcessCount;
			}
		}

		// 4. 流式处理原始读取缓冲区数据（只读视图 + 0拷贝 + 状态隔离）
		while (!isEOF() && remainingMax > 0) {
			int batchReadCount = batchReadValidChars();
			if (batchReadCount == 0) {
				prefetchBuffer();
				if (isEOF()) {
					break;
				}
				continue;
			}

			// 解码批量有效字符（0拷贝）
			batchEscapingBuffer.flip();
			CharBuffer tempDecodedBuffer;
			if (isBatchEscapingBufferHeap) {
				char[] batchArr = batchEscapingBufferArray;
				int batchOffset = batchEscapingBufferArrayOffset;
				int batchLen = batchEscapingBuffer.remaining();
				tempDecodedBuffer = JsonEscaping.decodeBatch(batchArr, batchOffset, batchLen);
			} else {
				tempDecodedBuffer = JsonEscaping.INSTANCE.decode(batchEscapingBuffer);
			}

			if (tempDecodedBuffer.hasRemaining()) {
				int processLen = Math.min(tempDecodedBuffer.remaining(), remainingMax);
				int origPos = tempDecodedBuffer.position();

				// 安全三步法：状态隔离 → 范围截取 → 只读保护
				CharBuffer stateIsolatedView = tempDecodedBuffer.duplicate();
				stateIsolatedView.position(origPos);
				stateIsolatedView.limit(origPos + processLen);
				CharBuffer readOnlyView = stateIsolatedView.asReadOnlyBuffer();

				// 消费者仅能读取，无法修改，杜绝错误
				consumer.accept(readOnlyView);

				// 更新状态
				totalProcessCount += processLen;
				remainingMax -= processLen;
				tempDecodedBuffer.position(origPos + processLen);
			}

			// 清空批量转义缓冲区，0拷贝存储解码残留
			batchEscapingBuffer.clear();
			if (tempDecodedBuffer.hasRemaining()) {
				if (isBatchEscapingBufferHeap && tempDecodedBuffer.hasArray()) {
					char[] srcArr = tempDecodedBuffer.array();
					int srcOffset = tempDecodedBuffer.arrayOffset() + tempDecodedBuffer.position();
					int copyLen = tempDecodedBuffer.remaining();
					System.arraycopy(srcArr, srcOffset, batchEscapingBufferArray, batchEscapingBufferArrayOffset,
							copyLen);
					batchEscapingBuffer.position(copyLen);
				} else {
					batchEscapingBuffer.put(tempDecodedBuffer);
				}
			}

			if (remainingMax <= 0) {
				break;
			}

			prefetchBuffer();
		}

		// 5. 返回结果：无处理且EOF返回EOF，否则返回实际处理数
		return totalProcessCount == 0 && isEOF() ? IOUtils.EOF : totalProcessCount;
	}

	/**
	 * 将缓冲区中的有效JSON数据写入目标Appendable，最多写入指定字符数
	 * 
	 * @param appendable 目标追加器，用于接收读取到的字符数据
	 * @param maxSize    最大写入字符数，非负整数（0返回0，负数抛出异常）
	 * @return 实际写入的字符个数，IOUtils.EOF表示无可用字符
	 * @throws IOException              读取或写入过程中发生I/O异常
	 * @throws CodecException           转义处理过程中发生编码异常
	 * @throws IllegalArgumentException 当maxSize为负数时抛出
	 */
	public final int read(Appendable appendable, int maxSize) throws IOException, CodecException {
		return read(maxSize, appendable::append);
	}

	// ===================== 核心读取方法 =====================
	/**
	 * 从读取器读取字符到目标CharBuffer
	 * 
	 * @param target 目标CharBuffer，存储读取到的字符
	 * @return 实际读取的字符个数，EOF表示无可用字符
	 * @throws IOException    读取过程中发生I/O异常
	 * @throws CodecException 转义处理过程中发生编码异常
	 */
	@Override
	public final int read(CharBuffer target) throws IOException, CodecException {
		return read(target, target.remaining());
	}

	/**
	 * 从读取器读取字符到目标字符数组
	 * 
	 * @param cbuf 目标字符数组
	 * @param off  数组起始偏移量
	 * @param len  待读取字符个数
	 * @return 实际读取的字符个数，EOF表示无可用字符
	 * @throws IOException    读取过程中发生I/O异常
	 * @throws CodecException 转义处理过程中发生编码异常
	 */
	@Override
	public final int read(char[] cbuf, int off, int len) throws IOException, CodecException {
		CharBuffer wrappedBuffer = CharBuffer.wrap(cbuf, off, len);
		return read(wrappedBuffer);
	}

	// ===================== 内部辅助方法 =====================
	/**
	 * 获取转义残留缓冲区
	 * 
	 * @return 转义残留数据封装的CharBuffer
	 * @throws CodecException 转义处理过程中发生编码异常
	 */
	private CharBuffer getEscapeRemainBuffer() throws CodecException {
		if (escapeRemainLen <= 0 || escapeRemainStart == -1) {
			return EMPTY_CHAR_BUFFER;
		}

		if (isBatchEscapingBufferHeap) {
			char[] batchArr = batchEscapingBufferArray;
			int escapeOffset = batchEscapingBufferArrayOffset + escapeRemainStart;
			return JsonEscaping.encodeBatch(batchArr, escapeOffset, escapeRemainLen);
		}

		char[] tempArr = new char[escapeRemainLen];
		int originalPos = batchEscapingBuffer.position();
		int originalLimit = batchEscapingBuffer.limit();
		try {
			batchEscapingBuffer.position(escapeRemainStart);
			batchEscapingBuffer.limit(escapeRemainStart + escapeRemainLen);
			batchEscapingBuffer.get(tempArr);
			return JsonEscaping.encodeBatch(tempArr, 0, tempArr.length);
		} finally {
			batchEscapingBuffer.position(originalPos);
			batchEscapingBuffer.limit(originalLimit);
		}
	}

	/**
	 * 存储转义残留数据到批量转义缓冲区
	 * 
	 * @param remainBuffer 待存储的转义残留数据
	 */
	private void storeEscapeRemain(CharBuffer remainBuffer) {
		if (!remainBuffer.hasRemaining()) {
			return;
		}

		batchEscapingBuffer.clear();

		escapeRemainStart = batchEscapingBuffer.position();
		escapeRemainLen = remainBuffer.remaining();

		if (isBatchEscapingBufferHeap && remainBuffer.hasArray()) {
			char[] srcArr = remainBuffer.array();
			int srcOffset = remainBuffer.arrayOffset() + remainBuffer.position();
			char[] destArr = batchEscapingBufferArray;
			int destOffset = batchEscapingBufferArrayOffset + escapeRemainStart;
			if (escapeRemainLen > 0) {
				System.arraycopy(srcArr, srcOffset, destArr, destOffset, escapeRemainLen);
			}
			batchEscapingBuffer.position(escapeRemainStart + escapeRemainLen);
		} else {
			batchEscapingBuffer.put(remainBuffer);
		}
	}

	/**
	 * 预填充原始读取缓冲区 当读取缓冲区无可用数据时，从readable中读取新数据填充
	 * 
	 * @throws IOException 读取过程中发生I/O异常
	 */
	private void prefetchBuffer() throws IOException {
		if (escapeRemainLen > 0) {
			CharBuffer tempRemain = getEscapeRemainBuffer();
			escapeRemainStart = -1;
			escapeRemainLen = 0;
			if (tempRemain.hasRemaining()) {
				storeEscapeRemain(tempRemain);
			}
		}

		if (bufferPos < bufferLimit) {
			return;
		}

		readBuffer.clear();
		int readLen = readable.read(readBuffer);
		if (readLen == IOUtils.EOF) {
			currentChar = IOUtils.EOF;
			return;
		}

		readBuffer.flip();
		bufferPos = readBuffer.position();
		bufferLimit = readBuffer.limit();
		currentChar = bufferPos < bufferLimit ? (char) readBuffer.get(bufferPos++) : IOUtils.EOF;
	}

	/**
	 * 批量读取有效JSON字符 跳过空白字符和注释，提取有效字符存储到批量转义缓冲区
	 * 
	 * @return 读取到的有效字符个数
	 * @throws IOException 读取过程中发生I/O异常
	 */
	private int batchReadValidChars() throws IOException {
		batchEscapingBuffer.clear();

		int validStart = bufferPos;
		int batchRemain = batchEscapingBuffer.remaining();

		final char[] localReadBufferArray = this.readBufferArray;
		final int localReadBufferArrayOffset = this.readBufferArrayOffset;
		final char[] localBatchEscapingBufferArray = this.batchEscapingBufferArray;
		final int localBatchEscapingBufferArrayOffset = this.batchEscapingBufferArrayOffset;
		final boolean localIsReadBufferHeap = this.isReadBufferHeap;
		final boolean localIsBatchEscapingBufferHeap = this.isBatchEscapingBufferHeap;

		int localBufferPos = bufferPos;
		int localBufferLimit = bufferLimit;
		int localCurrentChar = currentChar;
		boolean localInString = inString;

		while (batchRemain > 0 && localBufferPos < localBufferLimit && localCurrentChar != IOUtils.EOF) {
			int skipCount = 0;
			while (localBufferPos + skipCount < localBufferLimit) {
				char tempC;
				if (localIsReadBufferHeap) {
					tempC = localReadBufferArray[localReadBufferArrayOffset + localBufferPos + skipCount];
				} else {
					tempC = readBuffer.get(localBufferPos + skipCount);
				}
				// 全部使用空白字符常量，无任何硬编码
				if (tempC <= 32 && (tempC == JsonElement.SPACE || tempC == TAB || tempC == NEWLINE
						|| tempC == CARRIAGE_RETURN)) {
					skipCount++;
				} else {
					break;
				}
			}

			if (skipCount > 0) {
				localBufferPos += skipCount;
				localCurrentChar = localBufferPos < localBufferLimit ? (char) readBuffer.get(localBufferPos)
						: IOUtils.EOF;
				continue;
			}

			char c = (char) localCurrentChar;
			if (!localInString && c == SLASH) {
				localBufferPos++;
				localCurrentChar = localBufferPos < localBufferLimit ? (char) readBuffer.get(localBufferPos)
						: IOUtils.EOF;
				if (localCurrentChar == IOUtils.EOF || localBufferPos >= localBufferLimit) {
					break;
				}

				char nextChar = (char) localCurrentChar;
				if (nextChar == SLASH) {
					// 单行注释：跳过直到换行符（使用NEWLINE常量）
					while (localBufferPos < localBufferLimit && localCurrentChar != IOUtils.EOF) {
						if ((char) localCurrentChar == NEWLINE) {
							localBufferPos++;
							localCurrentChar = localBufferPos < localBufferLimit ? (char) readBuffer.get(localBufferPos)
									: IOUtils.EOF;
							break;
						}
						localBufferPos++;
						localCurrentChar = localBufferPos < localBufferLimit ? (char) readBuffer.get(localBufferPos)
								: IOUtils.EOF;
					}
				} else if (nextChar == STAR) {
					boolean foundEnd = false;
					int bufferLimitCache = localBufferLimit;
					while (localBufferPos < bufferLimitCache && localCurrentChar != IOUtils.EOF && !foundEnd) {
						int starIndex = localBufferPos;
						if (localIsReadBufferHeap) {
							while (starIndex < bufferLimitCache
									&& localReadBufferArray[localReadBufferArrayOffset + starIndex] != STAR) {
								starIndex++;
							}
						} else {
							while (starIndex < bufferLimitCache && readBuffer.get(starIndex) != STAR) {
								starIndex++;
							}
						}

						if (starIndex == bufferLimitCache) {
							localBufferPos = starIndex;
							localCurrentChar = IOUtils.EOF;
							prefetchBuffer();
							bufferLimitCache = bufferLimit;
							continue;
						}

						localBufferPos = starIndex;
						localBufferPos++;
						localCurrentChar = localBufferPos < bufferLimitCache ? (char) readBuffer.get(localBufferPos)
								: IOUtils.EOF;
						if (localCurrentChar != IOUtils.EOF && (char) localCurrentChar == SLASH) {
							localBufferPos++;
							localCurrentChar = localBufferPos < bufferLimitCache ? (char) readBuffer.get(localBufferPos)
									: IOUtils.EOF;
							foundEnd = true;
							break;
						}

						if (localBufferPos >= bufferLimitCache) {
							prefetchBuffer();
							bufferLimitCache = bufferLimit;
						}
					}

					if (!foundEnd) {
						throw new IOException("Unclosed multi-line comment (missing '*/')");
					}
				} else {
					localBufferPos--;
					localCurrentChar = (char) readBuffer.get(localBufferPos);
					break;
				}
			} else {
				if (c == JsonElement.QUOTE) {
					localInString = !localInString;
				}
				break;
			}
		}

		if (validStart < localBufferPos) {
			int copyLen = Math.min(localBufferPos - validStart, batchRemain);
			if (localIsReadBufferHeap && localIsBatchEscapingBufferHeap) {
				int srcOffset = localReadBufferArrayOffset + validStart;
				int destOffset = localBatchEscapingBufferArrayOffset + batchEscapingBuffer.position();
				if (copyLen > 0) {
					System.arraycopy(localReadBufferArray, srcOffset, localBatchEscapingBufferArray, destOffset,
							copyLen);
				}
				batchEscapingBuffer.position(batchEscapingBuffer.position() + copyLen);
			} else {
				for (int i = validStart; i < localBufferPos && batchEscapingBuffer.hasRemaining(); i++) {
					batchEscapingBuffer.put(readBuffer.get(i));
				}
			}
		}

		bufferPos = localBufferPos;
		bufferLimit = localBufferLimit;
		currentChar = localCurrentChar;
		inString = localInString;

		return batchEscapingBuffer.position();
	}

	/**
	 * 判断是否到达文件末尾（EOF）
	 * 
	 * @return true表示无可用字符，false表示还有待读取字符
	 */
	private boolean isEOF() {
		return currentChar == IOUtils.EOF && bufferPos >= bufferLimit && batchEscapingBuffer.position() == 0
				&& escapeRemainLen == 0;
	}

	/**
	 * 检查读取器关闭状态 若已关闭，抛出IOException异常
	 * 
	 * @throws IOException 读取器已关闭时抛出该异常
	 */
	private void checkClosed() throws IOException {
		if (closed) {
			throw new IOException("JsonReader has been closed, cannot perform any operations");
		}
	}

	/**
	 * 判断读取器是否支持标记功能
	 * 
	 * @return true表示支持标记，false表示不支持
	 */
	@Override
	public boolean markSupported() {
		return true;
	}

	/**
	 * 标记当前读取位置 后续可通过reset方法恢复到该位置
	 * 
	 * @param readAheadLimit 预读取限制（当前实现无实际作用）
	 * @throws IOException 读取器已关闭时抛出该异常
	 */
	@Override
	public void mark(int readAheadLimit) throws IOException {
		checkClosed();
		markedBufferPos = bufferPos - 1;
		markedChar = currentChar;
		markedInString = inString;
	}

	/**
	 * 恢复到最近一次mark标记的位置
	 * 
	 * @throws IOException 无有效标记或读取器已关闭时抛出该异常
	 */
	@Override
	public void reset() throws IOException {
		checkClosed();
		if (markedBufferPos == -1 || markedBufferPos < 0) {
			throw new IOException("No valid mark position to reset");
		}

		bufferPos = markedBufferPos;
		currentChar = markedChar;
		inString = markedInString;
		markedBufferPos = -1;

		batchEscapingBuffer.clear();
		escapeRemainStart = -1;
		escapeRemainLen = 0;
	}

	/**
	 * 关闭读取器 关闭后无法再进行读取操作，所有状态重置
	 */
	@Override
	public void close() {
		if (!closed) {
			closed = true;
			bufferPos = 0;
			bufferLimit = 0;
			currentChar = IOUtils.EOF;
			markedBufferPos = -1;
			inString = false;
			batchEscapingBuffer.clear();
			escapeRemainStart = -1;
			escapeRemainLen = 0;
		}
	}
}