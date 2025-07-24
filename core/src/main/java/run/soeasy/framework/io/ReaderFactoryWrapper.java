package run.soeasy.framework.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.function.Pipeline;

/**
 * 读取器工厂包装器接口，用于装饰和增强现有{@link ReaderFactory}实例。
 * 该接口结合了{@link ReaderFactory}和{@link Wrapper}的功能，
 * 允许在不修改原始工厂的情况下扩展其行为。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：通过包装现有工厂实现非侵入式功能扩展</li>
 *   <li>透明代理：默认方法直接委派给被包装的工厂实例</li>
 *   <li>类型安全：通过泛型约束确保包装器与被包装对象的类型一致性</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>增强现有读取器工厂的功能</li>
 *   <li>在读取器创建过程中添加统一的预处理或后处理逻辑</li>
 *   <li>实现工厂的链式组合和责任链模式</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <R> 读取器类型
 * @param <W> 被包装的读取器工厂类型
 * @see ReaderFactory
 * @see Wrapper
 */
@FunctionalInterface
public interface ReaderFactoryWrapper<R extends Reader, W extends ReaderFactory<R>>
        extends ReaderFactory<R>, Wrapper<W> {

    /**
     * 获取读取器实例，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link ReaderFactory#getReader()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强读取器创建过程。
     * </p>
     * 
     * @return 读取器实例
     * @throws IOException 如果创建读取器过程中发生I/O错误
     */
    @Override
    default Reader getReader() throws IOException {
        return getSource().getReader();
    }

    /**
     * 获取读取器流水线，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link ReaderFactory#getReaderPipeline()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强流水线处理过程。
     * </p>
     * 
     * @return 读取器流水线
     */
    @Override
    default Pipeline<R, IOException> getReaderPipeline() {
        return getSource().getReaderPipeline();
    }

    /**
     * 将读取器内容转换为字符序列，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link ReaderFactory#toCharSequence()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强内容转换过程。
     * </p>
     * 
     * @return 读取器内容的字符序列表示
     * @throws IOException 如果读取过程中发生I/O错误
     */
    @Override
    default CharSequence toCharSequence() throws IOException {
        return getSource().toCharSequence();
    }

    /**
     * 按行读取内容并返回元素流，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link ReaderFactory#readLines()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强行读取过程。
     * </p>
     * 
     * @return 按行读取的元素流
     */
    @Override
    default Elements<String> readLines() {
        return getSource().readLines();
    }

    /**
     * 将读取器内容传输到指定的写入器工厂，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link ReaderFactory#transferTo(WriterFactory)}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强数据传输过程。
     * </p>
     * 
     * @param dest 目标写入器工厂，不可为null
     * @return 传输的字符数
     * @throws IOException 如果传输过程中发生I/O错误
     */
    @Override
    default <T extends Writer> long transferTo(@NonNull WriterFactory<? extends T> dest) throws IOException {
        return getSource().transferTo(dest);
    }
}