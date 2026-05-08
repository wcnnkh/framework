package run.soeasy.framework.io;

import java.nio.Buffer;

/**
 * Buffer 数据拷贝器接口，定义从源 Buffer 到目标载体的拷贝逻辑。
 * <p>
 * 该接口是 IOUtils 中拷贝逻辑的扩展点，支持自定义不同类型 Buffer（如 ByteBuffer/CharBuffer） 到不同目标载体（如
 * byte[]/char[]）的拷贝规则，弥补 Buffer 原生 get/put 方法的通用性不足。
 * 
 * @param <S> 源 Buffer 类型（如 ByteBuffer、CharBuffer，必须继承自 java.nio.Buffer）
 * @param <T> 目标载体类型（如 byte[]、char[]，支持任意自定义载体）
 * @author soeasy.run
 * @see IOUtils#copy(Buffer, Object, int, BufferCopier, BufferConsumer)
 */
public interface BufferCopier<S extends Buffer, T> {
	/**
	 * 从源 Buffer 拷贝指定长度的数据到目标载体的指定偏移位置。
	 * <p>
	 * 核心要求：
	 * <ul>
	 * <li>拷贝长度不能超过源 Buffer 的剩余数据量（source.remaining()），否则会抛出
	 * BufferUnderflowException</li>
	 * <li>目标载体的偏移位置 + 拷贝长度不能超过载体的有效容量，否则会抛出 IndexOutOfBoundsException</li>
	 * <li>拷贝完成后，源 Buffer 的 position 需向后移动 {@code length} 个位置（由实现类保证）</li>
	 * </ul>
	 * 
	 * @param source       源 Buffer（不可为 null），待拷贝数据的缓冲区
	 * @param target       目标载体（不可为 null），接收拷贝数据的对象
	 * @param targetOffset 目标载体的起始偏移位置（≥ 0）
	 * @param length       拷贝数据长度（> 0，且 ≤ source.remaining()）
	 * @throws java.nio.BufferUnderflowException 源 Buffer 剩余数据不足时抛出
	 * @throws IndexOutOfBoundsException         目标载体偏移/长度越界时抛出
	 */
	void copy(S source, T target, int targetOffset, int length);
}