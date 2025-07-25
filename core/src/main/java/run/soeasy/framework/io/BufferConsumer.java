package run.soeasy.framework.io;

/**
 * 缓冲区消费者接口，定义处理缓冲区数据的功能契约。 该接口为函数式接口，适用于需要对缓冲区数据进行消费处理的场景，
 * 支持自定义异常类型以处理消费过程中的错误。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>类型参数化：通过泛型支持不同类型的缓冲区（如字节缓冲区、字符缓冲区）</li>
 * <li>精准操作：通过偏移量和长度指定缓冲区处理范围</li>
 * <li>异常扩展：支持自定义异常类型{E}处理消费过程中的错误</li>
 * <li>函数式设计：单方法接口，便于使用Lambda表达式实现</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>IO数据处理：消费网络传输或文件读写的缓冲区数据</li>
 * <li>数据转换：对缓冲区数据进行格式转换或编码处理</li>
 * <li>流式处理：配合流水线模式处理分段缓冲区数据</li>
 * <li>高性能场景：避免频繁的缓冲区拷贝，直接操作原始数据</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <T> 缓冲区类型
 * @param <E> 可能抛出的异常类型，需继承{@link Throwable}
 * @see java.nio.Buffer
 * @see java.io.IOException
 */
@FunctionalInterface
public interface BufferConsumer<T, E extends Throwable> {
	/**
	 * 消费缓冲区数据的核心方法。
	 * <p>
	 * 该方法负责处理指定缓冲区的部分数据，通过偏移量和长度 精准定位处理范围，适用于需要分段处理大数据的场景。
	 * 
	 * @param buffer 待处理的缓冲区，具体类型由泛型<T>指定
	 * @param offset 处理的起始偏移量
	 * @param length 处理的数据长度
	 * @throws E 当消费过程中发生异常时抛出，类型由泛型<E>指定
	 */
	void accept(T buffer, int offset, int length) throws E;
}