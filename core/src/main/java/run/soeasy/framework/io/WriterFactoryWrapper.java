package run.soeasy.framework.io;

import java.io.IOException;
import java.io.Writer;

import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.function.Pipeline;

/**
 * 写入器工厂包装器接口，用于装饰和增强现有{@link WriterFactory}实例。
 * 该接口结合了{@link WriterFactory}和{@link Wrapper}的功能，
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
 *   <li>增强现有写入器工厂的功能</li>
 *   <li>在写入器创建过程中添加统一的预处理或后处理逻辑</li>
 *   <li>实现工厂的链式组合和责任链模式</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <T> 写入器类型
 * @param <W> 被包装的写入器工厂类型
 * @see WriterFactory
 * @see Wrapper
 */
@FunctionalInterface
public interface WriterFactoryWrapper<T extends Writer, W extends WriterFactory<T>>
        extends WriterFactory<T>, Wrapper<W> {
    /**
     * 获取写入器实例，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link WriterFactory#getWriter()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强写入器创建过程。
     * 
     * @return 写入器实例
     * @throws IOException 如果创建写入器过程中发生I/O错误
     */
    @Override
    default Writer getWriter() throws IOException {
        return getSource().getWriter();
    }

    /**
     * 获取写入器流水线，默认委派给被包装的工厂实现。
     * <p>
     * 该方法直接调用被包装工厂的{@link WriterFactory#getWriterPipeline()}方法，
     * 允许包装器在不改变原始逻辑的情况下拦截或增强流水线处理过程。
     * 
     * @return 写入器流水线
     */
    @Override
    default Pipeline<T, IOException> getWriterPipeline() {
        return getSource().getWriterPipeline();
    }
}