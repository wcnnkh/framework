package run.soeasy.framework.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingRunnable;

/**
 * 输出流流水线实现，作为{@link OutputStream}的装饰器，
 * 提供线程安全的关闭操作和流水线处理能力。
 * 
 * <p>该类包装目标{@link OutputStream}实例，通过{@link ThrowingRunnable}
 * 实现自定义关闭逻辑，并使用原子布尔值确保关闭操作的线程安全性。
 * 同时实现{@link Pipeline}接口，支持将输出流纳入流水线处理流程。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装原始输出流并增强关闭行为</li>
 *   <li>线程安全：使用{@code AtomicBoolean}确保关闭操作的原子性</li>
 *   <li>自定义关闭：通过{@link ThrowingRunnable}支持额外资源释放</li>
 *   <li>流水线集成：实现{@link Pipeline}接口以支持链式处理</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>需要在关闭输出流时执行额外清理操作的场景</li>
 *   <li>多线程环境下确保输出流关闭操作的一致性</li>
 *   <li>构建输出流处理链并统一管理资源释放</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see FilterOutputStream
 * @see Pipeline
 * @see ThrowingRunnable
 */
class OutputStreamPipeline extends FilterOutputStream implements Pipeline<OutputStream, IOException> {
    @NonNull
    private final ThrowingRunnable<? extends IOException> closeable;
    private final AtomicBoolean closed = new AtomicBoolean();

    /**
     * 基于流水线创建输出流包装实例。
     * <p>
     * 从流水线获取目标输出流，并将流水线的关闭操作作为自定义关闭逻辑。
     * 适用于需要将输出流纳入流水线处理的场景。
     * 
     * @param pipeline 输出流流水线，不可为null
     * @throws IOException 当从流水线获取输出流失败时抛出
     */
    public OutputStreamPipeline(Pipeline<? extends OutputStream, ? extends IOException> pipeline) throws IOException {
        this(pipeline.get(), pipeline::close);
    }

    /**
     * 基于目标输出流和自定义关闭逻辑创建包装实例。
     * <p>
     * 允许为输出流指定额外的关闭操作，该操作将在输出流关闭时执行。
     * 
     * @param out 目标输出流，不可为null
     * @param closeable 自定义关闭逻辑，不可为null
     */
    public OutputStreamPipeline(OutputStream out, ThrowingRunnable<? extends IOException> closeable) {
        super(out);
        this.closeable = closeable;
    }

    /**
     * 获取包装的目标输出流。
     * 
     * @return 目标输出流实例
     * @throws IOException 当获取输出流失败时抛出（通常不会发生）
     */
    @Override
    public OutputStream get() throws IOException {
        return out;
    }

    /**
     * 检查输出流是否已关闭。
     * 
     * @return {@code true}表示已关闭，{@code false}表示未关闭
     */
    @Override
    public boolean isClosed() {
        return closed.get();
    }

    /**
     * 关闭输出流并执行自定义关闭逻辑。
     * <p>
     * 使用原子布尔值确保关闭操作仅执行一次，先关闭目标输出流，
     * 再执行自定义关闭逻辑，确保资源释放的顺序性。
     * 
     * @throws IOException 当关闭输出流或执行自定义逻辑时发生I/O错误
     */
    @Override
    public void close() throws IOException {
        if (closed.compareAndSet(false, true)) {
            try {
                super.close();
            } finally {
                closeable.run();
            }
        }
    }
}