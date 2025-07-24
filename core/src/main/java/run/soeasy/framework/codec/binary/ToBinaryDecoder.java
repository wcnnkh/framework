package run.soeasy.framework.codec.binary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.Decoder;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.OutputSource;

/**
 * 字节数组解码器接口，继承自{@link Decoder}，用于将源数据解码为字节数组，
 * 提供流式解码、文件解码和消费者模式处理能力，适用于二进制数据解析场景。
 * 
 * <p><b>核心功能：</b>
 * <ul>
 *     <li>类型解码：将泛型数据{E}解码为字节数组{@code byte[]}</li>
 *     <li>流式处理：支持将解码结果写入{@link OutputStream}</li>
 *     <li>文件操作：支持直接解码到文件系统（通过{@link OutputSource}）</li>
 *     <li>消费者模式：通过{@link BufferConsumer}处理解码结果</li>
 * </ul>
 * 
 * <p><b>典型场景：</b>
 * <ul>
 *     <li>二进制数据解析：解析图片、视频、加密文件等二进制格式</li>
 *     <li>网络数据处理：解析Socket传输的字节流数据</li>
 *     <li>文件解码操作：从压缩包、加密文件中提取原始字节数据</li>
 *     <li>格式转换：将Base64/十六进制等格式转换为原始字节数组</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <E> 待解码的源数据类型
 */
@FunctionalInterface
public interface ToBinaryDecoder<E> extends Decoder<E, byte[]> {

    /**
     * 将源数据解码为字节数组（实现{@link Decoder}接口）。
     * <p>
     * 执行流程：
     * <ol>
     *     <li>创建字节数组输出流收集解码结果</li>
     *     <li>调用{@link #decode(Object, OutputStream)}执行解码</li>
     *     <li>转换输出流内容为字节数组返回</li>
     * </ol>
     * 
     * @param source 待解码的源数据
     * @return 解码后的字节数组，不可为null
     * @throws DecodeException 解码逻辑失败时抛出（封装IO异常）
     */
    @Override
    default byte[] decode(E source) throws CodecException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        try {
            decode(source, target::write);
        } catch (IOException e) {
            throw new DecodeException("Internal IO exception", e);
        }
        return target.toByteArray();
    }

    /**
     * 将源数据解码并写入输出源。
     * <p>
     * 执行流程：
     * <ol>
     *     <li>从输出源获取输出流</li>
     *     <li>调用{@link #decode(Object, OutputStream)}执行解码</li>
     *     <li>确保输出流被正确关闭（使用try-finally）</li>
     * </ol>
     * 
     * @param source 待解码的源数据
     * @param target 目标输出源，不可为null
     * @throws CodecException 解码逻辑失败时抛出
     * @throws IOException 输出源操作失败时抛出
     */
    default void decode(E source, @NonNull OutputSource target) throws CodecException, IOException {
        OutputStream outputStream = target.getOutputStream();
        try {
            decode(source, outputStream::write);
        } finally {
            outputStream.close();
        }
    }

    /**
     * 将源数据解码为字节数组并通过消费者处理。
     * <p>
     * 实现类应定义具体逻辑：
     * <ol>
     *     <li>将源数据解码为字节数组</li>
     *     <li>通过消费者处理解码结果</li>
     *     <li>处理大文件时建议采用内存优化策略</li>
     * </ol>
     * 
     * @param source 待解码的源数据
     * @param target 结果消费者，不可为null
     * @param <S> 消费者可能抛出的异常类型
     * @throws CodecException 解码逻辑失败时抛出
     * @throws IOException 文件操作或IO错误时抛出
     * @throws S 消费者处理过程中抛出的异常
     */
    <S extends Throwable> void decode(E source, @NonNull BufferConsumer<? super byte[], ? extends S> target)
            throws CodecException, IOException, S;
}