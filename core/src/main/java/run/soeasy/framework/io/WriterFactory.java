package run.soeasy.framework.io;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

/**
 * 写入器工厂接口，用于创建具有特定处理流程的{@link Writer}实例。
 * 该接口定义了获取写入器管道的方法，支持通过流水线模式处理写入操作。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>流水线处理：通过{@link Pipeline}接口支持链式处理写入操作</li>
 *   <li>默认实现：提供默认方法直接获取经过包装的写入器实例</li>
 *   <li>类型安全：通过泛型约束确保写入器类型一致性</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>需要自定义写入流程的场景</li>
 *   <li>构建可复用的写入器处理链</li>
 *   <li>实现装饰器模式增强写入功能</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Writer
 * @see Pipeline
 * @see WriterPipeline
 */
@FunctionalInterface
public interface WriterFactory<T extends Writer> {
    /**
     * 获取默认的写入器实例，该实例基于工厂定义的流水线处理。
     * <p>
     * 此方法返回一个{@link WriterPipeline}实例，它将工厂提供的
     * 写入器管道应用于所有写入操作，实现统一的处理流程。
     * 
     * @return 基于流水线处理的写入器实例
     * @throws IOException 如果创建写入器过程中发生I/O错误
     * @see WriterPipeline
     */
    default Writer getWriter() throws IOException {
        return new WriterPipeline(getWriterPipeline());
    }

    /**
     * 获取用于创建写入器的流水线。
     * <p>
     * 实现类必须提供此方法的具体实现，返回一个{@link Pipeline}实例，
     * 该实例定义了如何创建和处理特定类型的写入器。
     * 
     * @return 写入器流水线，不可为null
     * @throws IOException 如果获取流水线过程中发生I/O错误
     */
    @NonNull
    Pipeline<T, IOException> getWriterPipeline();
}