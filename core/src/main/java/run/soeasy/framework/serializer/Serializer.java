package run.soeasy.framework.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.io.IOUtils;

/**
 * 序列化接口
 * 支持对象与字节数组的双向转换，提供序列化、反序列化和对象克隆功能
 * 
 * 继承自{@link ReversibleConverter}&lt;Object, byte[]&gt;，实现：
 * - 正向转换：Object -&gt; byte[]（序列化）
 * - 反向转换：byte[] -&gt; Object（反序列化）
 * 
 * @author soeasy.run
 */
public interface Serializer extends ReversibleConverter<Object, byte[]> {

    /**
     * 克隆对象
     * 
     * @param source 源对象
     * @return 克隆后的对象
     */
    default Object clone(@NonNull Object source) {
        TypeDescriptor typeDescriptor = TypeDescriptor.forObject(source);
        byte[] data = serialize(source, typeDescriptor);
        return deserialize(data, typeDescriptor);
    }

    /**
     * 从字节数组反序列化对象
     * 
     * @param data                 源字节数组
     * @param targetTypeDescriptor 目标类型描述符
     * @return 反序列化后的对象
     * @throws SerializerException 反序列化失败时抛出
     */
    default Object deserialize(byte[] data, TypeDescriptor targetTypeDescriptor) {
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        try {
            return deserialize(input, targetTypeDescriptor);
        } catch (IOException e) {
            throw new SerializerException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * 从输入流反序列化对象
     * 
     * @param source               源输入流
     * @param targetTypeDescriptor 目标类型描述符
     * @return 反序列化后的对象
     * @throws IOException 反序列化过程中发生IO异常时抛出
     */
    Object deserialize(InputStream source, TypeDescriptor targetTypeDescriptor) throws IOException;

    /**
     * 实现{@link ReversibleConverter}的from方法，从字节数组反序列化对象
     */
    @Override
    default Object from(byte[] source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
            throws ConversionException {
        return deserialize(source, targetTypeDescriptor);
    }

    /**
     * 将对象序列化为字节数组
     * 
     * @param source               源对象
     * @param sourceTypeDescriptor 源类型描述符
     * @return 序列化后的字节数组
     * @throws SerializerException 序列化失败时抛出
     */
    default byte[] serialize(Object source, TypeDescriptor sourceTypeDescriptor) throws SerializerException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        try {
            serialize(source, sourceTypeDescriptor, target);
            return target.toByteArray();
        } catch (IOException e) {
            throw new SerializerException(e);
        } finally {
            IOUtils.closeQuietly(target);
        }
    }

    /**
     * 将对象序列化为输出流
     * 
     * @param source               源对象
     * @param sourceTypeDescriptor 源类型描述符
     * @param target               目标输出流
     * @throws IOException 序列化过程中发生IO异常时抛出
     */
    void serialize(Object source, TypeDescriptor sourceTypeDescriptor, OutputStream target) throws IOException;

    /**
     * 实现{@link ReversibleConverter}的to方法，将对象转换为字节数组
     */
    @Override
    default byte[] to(Object source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
            throws ConversionException {
        return serialize(source, sourceTypeDescriptor);
    }

    /**
     * 获取指定类型的序列化器
     * 
     * @param typeDescriptor 类型描述符
     * @return 类型安全的序列化器
     */
    default TypedSerializer typed(TypeDescriptor typeDescriptor) {
        return new ObjectSerializer(this, typeDescriptor);
    }
}