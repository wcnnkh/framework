package run.soeasy.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 基于{@link ByteBuffer}的输出流实现，将标准IO的OutputStream接口适配为ByteBuffer操作，
 * 支持将字节数据写入ByteBuffer缓冲区，适用于需要在标准IO和NIO ByteBuffer之间转换的场景。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>字节缓冲写入：封装ByteBuffer的put()操作</li>
 * <li>标准接口适配：实现OutputStream的write()方法</li>
 * <li>容量校验：写入前检查缓冲区剩余空间，避免溢出</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>NIO与标准IO转换：将OutputStream数据写入ByteBuffer</li>
 * <li>内存数据操作：向堆外内存(DirectByteBuffer)写入数据</li>
 * <li>分段数据组装：配合ByteBuffer的flip/rewind实现数据重读</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see ByteBuffer
 * @see OutputStream
 */
@Getter
@RequiredArgsConstructor
public class ByteBufferOutputStream extends OutputStream {
	@NonNull
	private final ByteBuffer buffer;

	/**
	 * 写入单个字节到ByteBuffer（实现OutputStream.write(int)）。
	 * <p>
	 * 执行逻辑：
	 * <ol>
	 * <li>检查ByteBuffer是否有剩余空间</li>
	 * <li>若无剩余空间则抛出IOException</li>
	 * <li>将整数转换为字节并写入ByteBuffer</li>
	 * </ol>
	 * 
	 * @param b 待写入的字节（0-255）
	 * @throws IOException 缓冲区空间不足时抛出
	 */
	@Override
	public void write(int b) throws IOException {
		if (!buffer.hasRemaining()) {
			throw new IOException("Buffer is full");
		}
		buffer.put((byte) b);
	}

	/**
	 * 批量写入字节数组到ByteBuffer（实现OutputStream.write(byte[], int, int)）。
	 * <p>
	 * 执行逻辑：
	 * <ol>
	 * <li>计算ByteBuffer剩余空间与请求写入长度</li>
	 * <li>若剩余空间不足则抛出IOException</li>
	 * <li>使用ByteBuffer.put(byte[], int, int)写入数据</li>
	 * </ol>
	 * 
	 * @param bytes 待写入的字节数组
	 * @param off   数组读取偏移量
	 * @param len   请求写入的字节数
	 * @throws IOException               缓冲区空间不足时抛出
	 * @throws IndexOutOfBoundsException 若off/len参数越界
	 */
	@Override
	public void write(byte[] bytes, int off, int len) throws IOException {
		if (buffer.remaining() < len) {
			throw new IOException("ByteBuffer has insufficient remaining space and requires " + len
					+ " bytes, remaining " + buffer.remaining() + " bytes ");
		}
		buffer.put(bytes, off, len);
	}
}