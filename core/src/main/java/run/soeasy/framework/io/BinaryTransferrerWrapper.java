package run.soeasy.framework.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.sequences.UUIDSequence;

/**
 * 二进制传输器包装器接口，提供重复传输功能的实现
 * <p>
 * 该接口继承了BinaryTransferrer和Wrapper接口，用于包装另一个BinaryTransferrer
 * 实例，并支持将数据传输操作重复执行指定次数。
 * 
 * @author soeasy.run
 * @param <W> 被包装的二进制传输器类型
 */
public interface BinaryTransferrerWrapper<W extends BinaryTransferrer> extends BinaryTransferrer, Wrapper<W> {
	
	/**
	 * 获取数据传输的重复次数
	 * 
	 * @return 重复传输的次数，大于0表示需要重复传输
	 */
	int getRepetitions();

	/**
	 * 实现带重复功能的数据传输逻辑
	 * <p>
	 * 根据指定的重复次数执行传输操作：
	 * <ul>
	 * <li>次数≤0：不执行任何传输</li>
	 * <li>次数=1：直接使用被包装的传输器执行一次传输</li>
	 * <li>次数>1：使用临时文件缓存中间结果，交替进行多次传输，最后将结果传输到目标</li>
	 * </ul>
	 * 
	 * @param source     输入流，不可为null
	 * @param bufferSize 传输缓冲区大小
	 * @param target     数据消费者，不可为null
	 * @param <E>        消费者可能抛出的异常类型
	 * @throws IOException 当IO操作失败时抛出
	 * @throws E           消费者处理数据时抛出的异常
	 */
	@Override
	default <E extends Throwable> void transferTo(@NonNull InputStream source, int bufferSize,
			@NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
		int count = getRepetitions();
		if (count <= 0) {
			return;
		}

		// 只有一次传输，直接执行并关闭流
		if (count == 1) {
			getSource().transferTo(source, bufferSize, target);
		}

		// 多次传输，使用临时文件
		File[] files = new File[2];
		try {
			// 创建两个临时文件
			files[0] = File.createTempFile(UUIDSequence.random().next(),
					BinaryTransferrerWrapper.class.getSimpleName());
			files[1] = File.createTempFile(UUIDSequence.random().next(),
					BinaryTransferrerWrapper.class.getSimpleName());
			// 初始传输：从输入流到第一个文件
			getSource().transferTo(source, bufferSize, Resource.forFile(files[0]));
			count--;

			// 交替使用两个文件进行重复传输
			int currentFileIndex = 0;
			for (; count > 0; count--) {
				int nextFileIndex = (currentFileIndex + 1) % 2;
				getSource().transferTo(Resource.forFile(files[currentFileIndex]), bufferSize,
						Resource.forFile(files[nextFileIndex]));
				currentFileIndex = nextFileIndex;
			}

			// 最终传输：从最后一个文件到目标
			getSource().transferTo(Resource.forFile(files[currentFileIndex]), bufferSize, target);
		} finally {
			// 确保删除所有临时文件
			for (File file : files) {
				if (file != null) {
					file.delete();
				}
			}
		}
	}
}
    