package run.soeasy.framework.io.source;

import run.soeasy.framework.io.transfer.TextTransferrer;

import java.io.IOException;
import java.io.Reader;

/**
 * 读者源接口，用于延迟提供 {@link Reader} 实例。
 *
 * <p>本接口的核心职责是封装 {@link Reader} 的创建逻辑，
 * 通常与 {@link TextTransferrer} 配合使用，将资源获取与数据传输解耦。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * ReaderSource<BufferedReader> source = () -> Files.newBufferedReader(path);
 *
 * CharTransferrer transferrer = new DefaultCharTransferrer(1024);
 * String content = transferrer.readAll(source);
 * }</pre>
 *
 * @param <R> 读者类型，必须是 {@link Reader} 的子类
 * @author soeasy.run
 */
@FunctionalInterface
public interface ReaderSource<R extends Reader> {

    /**
     * 获取读者实例。
     *
     * <p>调用方负责在使用完毕后关闭返回的读者。
     *
     * @return 读者实例
     * @throws IOException 如果获取读者失败
     */
    R getReader() throws IOException;
}