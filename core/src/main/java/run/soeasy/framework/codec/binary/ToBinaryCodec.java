package run.soeasy.framework.codec.binary;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.format.Base64;
import run.soeasy.framework.codec.format.HexCodec;

/**
 * 字节数组编解码器接口，继承自{@link Codec}、{@link ToBinaryEncoder}和{@link FromBinaryDecoder}，
 * 提供数据与字节数组的双向转换能力，并支持将字节数组结果进一步转换为Base64、十六进制等常见格式，
 * 适用于需要同时处理编码和解码的二进制数据场景。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>双向转换：支持数据类型{D}与字节数组{@code byte[]}的相互转换</li>
 *   <li>格式扩展：通过{@link #toBase64()}和{@link #toHex()}组合常见编码格式</li>
 *   <li>功能集成：同时具备编码（{@link ToBinaryEncoder}）和解码（{@link FromBinaryDecoder}）能力</li>
 *   <li>链式组合：支持与其他编解码器组合形成复杂编解码流程</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>二进制数据处理：对象与字节数组的序列化/反序列化</li>
 *   <li>数据传输编码：将数据转换为字节数组用于网络传输或存储</li>
 *   <li>格式转换流程：组合Base64、十六进制等编码形成多级转换流程</li>
 *   <li>加密解密场景：配合加密编解码器实现数据的安全转换</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <D> 待编解码的源数据类型
 * @see Codec
 * @see ToBinaryEncoder
 * @see FromBinaryDecoder
 * @see Base64
 * @see HexCodec
 */
public interface ToBinaryCodec<D> extends Codec<D, byte[]>, ToBinaryEncoder<D>, FromBinaryDecoder<D> {
    
    /**
     * 组合Base64编解码器形成新的编解码器。
     * <p>
     * 新编解码器的编解码流程为：
     * <pre>
     * 编码：D → [当前编解码器] → byte[] → [Base64编解码器] → String
     * 解码：String → [Base64编解码器] → byte[] → [当前编解码器] → D
     * </pre>
     * 等价于调用{@code to(Base64.DEFAULT)}。
     * 
     * @return 组合后的Base64编解码器，不可为null
     */
    default Codec<D, String> toBase64() {
        return to(Base64.DEFAULT);
    }

    /**
     * 组合十六进制编解码器形成新的编解码器。
     * <p>
     * 新编解码器的编解码流程为：
     * <pre>
     * 编码：D → [当前编解码器] → byte[] → [十六进制编解码器] → String
     * 解码：String → [十六进制编解码器] → byte[] → [当前编解码器] → D
     * </pre>
     * 等价于调用{@code to(HexCodec.DEFAULT)}。
     * 
     * @return 组合后的十六进制编解码器，不可为null
     */
    default Codec<D, String> toHex() {
        return to(HexCodec.DEFAULT);
    }
}