package run.soeasy.framework.codec.binary;

import run.soeasy.framework.codec.Codec;

/**
 * 字节数组编解码器接口，继承自{@link Codec}、{@link FromBinaryEncoder}和{@link ToBinaryDecoder}，
 * 提供字节数组与目标类型的双向转换能力，整合编码和解码功能，
 * 适用于需要同时处理字节数组与其他类型数据相互转换的场景。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>双向转换：支持字节数组与类型{E}的相互转换（编码/解码）</li>
 *   <li>功能整合：同时具备{@link FromBinaryEncoder}的编码能力和{@link ToBinaryDecoder}的解码能力</li>
 *   <li>多源支持：继承自{@link FromBinaryEncoder}的多源编码能力（输入流、文件等）</li>
 *   <li>链式组合：支持通过{@link Codec}接口组合其他编解码器形成转换流程</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>数据序列化/反序列化：对象与字节数组的双向转换</li>
 *   <li>网络数据传输：字节数组与应用数据的相互转换</li>
 *   <li>文件读写处理：二进制文件与对象的转换</li>
 *   <li>多级编解码流程：组合其他编解码器实现复杂转换逻辑</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <E> 与字节数组转换的目标数据类型
 * @see Codec
 * @see FromBinaryEncoder
 * @see ToBinaryDecoder
 */
public interface FromBinaryCodec<E> extends Codec<byte[], E>, FromBinaryEncoder<E>, ToBinaryDecoder<E> {
    // 继承自父接口的方法已在父接口注释中说明，此处无需重复
}