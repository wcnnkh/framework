package run.soeasy.framework.io.source;

import run.soeasy.framework.io.transfer.TextTransferrer;

import java.io.IOException;
import java.io.Writer;

/**
 * 写者源接口，用于延迟提供 {@link Writer} 实例。
 *
 * <p>本接口的核心职责是封装 {@link Writer} 的创建逻辑，
 * 通常与 {@link TextTransferrer} 配合使用，
 * 将资源获取与数据传输解耦。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * WriterSource<BufferedWriter> source = () -> Files.newBufferedWriter(path);
 *
 * CharTransferrer transferrer = new DefaultCharTransferrer(1024);
 * transferrer.transfer(reader, source);
 * }</pre>
 *
 * @param <W> 写者类型，必须是 {@link Writer} 的子类
 * @author soeasy.run
 */
@FunctionalInterface
public interface WriterSource<W extends Writer> {

    /**
     * 获取写者实例。
     *
     * <p>调用方负责在使用完毕后关闭返回的写者。
     *
     * @return 写者实例
     * @throws IOException 如果获取写者失败
     */
    W getWriter() throws IOException;
}