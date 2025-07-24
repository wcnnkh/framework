package run.soeasy.framework.beans;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.format.KeyValueFormat;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.transform.property.TypedProperties;
import run.soeasy.framework.core.transform.templates.Mapping;

/**
 * 基于JavaBean的键值格式化器，继承自{@link KeyValueFormat}，专门用于将JavaBean对象与键值对格式（如查询字符串、表单数据）进行互转，
 * 扩展了默认的键值映射逻辑，支持通过{@link BeanMapper}解析Bean属性并转换为键值对流。
 * 
 * <p>该类通过注册Object类型的映射器，实现任意JavaBean对象到键值对集合的自动转换，结合分隔符、连接器和编解码器，
 * 完成Bean对象与特定格式字符串（如"key1=value1&key2=value2"）的相互转换。
 * 
 * @author soeasy.run
 * @see KeyValueFormat
 * @see BeanMapper
 * @see KeyValue
 */
@Getter
@Setter
public class BeanFormat extends KeyValueFormat {

    /**
     * 构造Bean格式化器（指定分隔符、连接器及键值编解码器）
     * 
     * <p>初始化时注册Object类型的映射器，通过{@link BeanMapper}获取Bean的属性集合，
     * 将每个属性转换为{@link KeyValue}对象，实现Bean到键值对流的转换。
     * 
     * @param delimiter 键值对之间的分隔符（如"&"）
     * @param connector 键与值之间的连接器（如"="）
     * @param keyCodec 键的编解码器（用于键的编码和解码）
     * @param valueCodec 值的编解码器（用于值的编码和解码）
     */
    public BeanFormat(@NonNull CharSequence delimiter, @NonNull CharSequence connector,
            @NonNull Codec<String, String> keyCodec, @NonNull Codec<String, String> valueCodec) {
        super(delimiter, connector, keyCodec, valueCodec);
        
        // 注册Object类型的映射器：将任意Bean转换为键值对流
        getKeyValueMapper().getMappingProvider().register(Object.class, (bean, type) -> {
            // 通过BeanMapper获取Bean的类型化属性集合
            TypedProperties typedProperties = BeanMapper.getInstane().getMapping(bean, type);
            
            // 定义Bean到键值对流的映射逻辑
            Mapping<Object, TypedValueAccessor> mapping = () -> 
                typedProperties.getElements()
                    .map(property -> KeyValue.of(property.getKey(), property.getValue()));
            
            return mapping;
        });
    }
}