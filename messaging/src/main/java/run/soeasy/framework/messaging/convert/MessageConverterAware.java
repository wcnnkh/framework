package run.soeasy.framework.messaging.convert;

/**
 * 消息转换器感知接口，定义了设置消息转换器的方法，
 * 使实现类能够获取并使用{@link MessageConverter}进行消息的序列化和反序列化操作，
 * 通常用于依赖注入场景，为组件提供消息转换能力。
 * 
 * <p>实现该接口的类可以通过{@link #setMessageConverter(MessageConverter)}方法接收一个消息转换器实例，
 * 从而在内部使用该转换器处理消息内容，无需关心转换器的具体创建和管理逻辑。
 * 
 * @author soeasy.run
 * @see MessageConverter
 */
public interface MessageConverterAware {

    /**
     * 设置消息转换器实例
     * 
     * <p>该方法通常由容器或框架调用，注入一个可用的{@link MessageConverter}实例，
     * 使实现类能够使用该转换器进行消息的读写操作。
     * 
     * @param messageConverter 消息转换器实例（非空）
     */
    void setMessageConverter(MessageConverter messageConverter);
}