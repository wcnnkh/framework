package run.soeasy.framework.messaging.convert;

import java.io.IOException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.MediaTypes;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;
import run.soeasy.framework.messaging.convert.support.ByteArrayMessageConverter;
import run.soeasy.framework.messaging.convert.support.QueryStringMessageConveter;
import run.soeasy.framework.messaging.convert.support.TextMessageConverter;

/**
 * 消息转换器集合类，继承自{@link ConfigurableServices}并实现{@link MessageConverter}接口，
 * 作为多个{@link MessageConverter}实例的统一管理入口，采用门面模式封装转换器的聚合逻辑，
 * 对外提供简洁的消息转换接口，内部通过委托模式分发转换操作到具体转换器。
 * 
 * <p>核心设计：
 * - 集成服务发现与排序能力：基于{@link ConfigurableServices}管理转换器生命周期，使用{@link MessageConverterComparator}
 *   确保转换器按媒体类型特异性排序（具体类型优先于通配符类型）；
 * - 系统级单例实例：提供{@link #system()}方法获取预配置的全局实例，内置常用转换器（文本、字节数组、表单查询字符串）；
 * - 依赖注入支持：自动为实现{@link MessageConverterAware}的转换器注入当前集合实例，支持转换器间的协作。
 * 
 * @author soeasy.run
 * @see MessageConverter
 * @see ConfigurableServices
 * @see MessageConverterComparator
 * @see TextMessageConverter
 * @see ByteArrayMessageConverter
 * @see QueryStringMessageConveter
 */
public class MessageConverters extends ConfigurableServices<MessageConverter> implements MessageConverter {
    private static final Logger logger = LogManager.getLogger(MessageConverters.class);

    /**
     * 系统级默认消息转换器集合（单例），预注册基础转换器，供全局使用
     */
    private static volatile MessageConverters system;

    /**
     * 文本消息转换器实例（全局共享），处理text/*类型消息
     */
    public static final TextMessageConverter TEXT_MESSAGE_CONVERTER = new TextMessageConverter();

    /**
     * 字节数组消息转换器实例（全局共享），处理application/octet-stream类型消息
     */
    public static final ByteArrayMessageConverter BYTE_ARRAY_MESSAGE_CONVERTER = new ByteArrayMessageConverter();

    /**
     * 查询字符串消息转换器实例（全局共享），处理application/x-www-form-urlencoded类型消息
     */
    public static final QueryStringMessageConveter QUERY_STRING_MESSAGE_CONVETER = new QueryStringMessageConveter();

    /**
     * 获取系统级默认消息转换器集合（单例）
     * 
     * <p>采用双重检查锁定实现线程安全的懒加载，确保全局仅初始化一次，预注册以下转换器：
     * - {@link #TEXT_MESSAGE_CONVERTER}：文本消息转换
     * - {@link #BYTE_ARRAY_MESSAGE_CONVERTER}：字节数组转换
     * - {@link #QUERY_STRING_MESSAGE_CONVETER}：表单查询字符串转换
     * 
     * @return 系统级消息转换器集合实例（非空）
     */
    public static MessageConverters system() {
        if (system == null) {
            synchronized (MessageConverters.class) {
                if (system == null) {
                    system = new MessageConverters();
                    system.register(TEXT_MESSAGE_CONVERTER);
                    system.register(BYTE_ARRAY_MESSAGE_CONVERTER);
                    system.register(QUERY_STRING_MESSAGE_CONVETER);
                    system.configure(); // 触发服务初始化（排序、注入等）
                }
            }
        }
        return system;
    }

    /**
     * 用于注入到{@link MessageConverterAware}实现类的消息转换器实例，
     * 指向当前{@link MessageConverters}实例，使子转换器能访问整个转换器集合
     */
    @NonNull
    private MessageConverter messageConverterAware = this;

    /**
     * 初始化消息转换器集合，配置服务类型、排序器和依赖注入器
     * 
     * <p>初始化逻辑：
     * 1. 设置服务接口为{@link MessageConverter}，限定管理的服务类型；
     * 2. 配置排序器为{@link MessageConverterComparator#DEFAULT}，确保转换器按媒体类型特异性排序；
     * 3. 注册注入器：当转换器实现{@link MessageConverterAware}时，自动注入当前集合实例。
     */
    public MessageConverters() {
        setServiceClass(MessageConverter.class);
        setComparator(MessageConverterComparator.DEFAULT);
        getInjectors().register((converter) -> {
            if (converter instanceof MessageConverterAware) {
                ((MessageConverterAware) converter).setMessageConverter(messageConverterAware);
            }
            return Registration.SUCCESS;
        });
    }

    /**
     * 聚合所有注册转换器支持的媒体类型
     * 
     * <p>通过flatMap操作合并所有转换器的{@link #getSupportedMediaTypes()}结果，
     * 形成全局支持的媒体类型集合，用于快速判断系统是否支持某种媒体类型。
     * 
     * @return 合并后的媒体类型集合（非空，包含所有转换器支持的类型）
     */
    @Override
    public MediaTypes getSupportedMediaTypes() {
        return MediaTypes.forElements(flatMap((converter) -> converter.getSupportedMediaTypes()));
    }

    /**
     * 聚合支持指定类型描述符的所有转换器的媒体类型
     * 
     * <p>根据目标类型动态筛选支持的媒体类型，仅返回能处理该类型的转换器所支持的媒体类型，
     * 用于针对特定类型的媒体类型匹配（如判断"application/json"是否支持转换为User对象）。
     * 
     * @param requiredDescriptor 目标类型描述符（非空，指定需要转换的类型）
     * @param message 关联的消息（非空，提供上下文信息）
     * @return 支持指定类型的媒体类型集合（非空）
     */
    @Override
    public MediaTypes getSupportedMediaTypes(@NonNull AccessibleDescriptor requiredDescriptor,
            @NonNull Message message) {
        return MediaTypes.forElements(flatMap((converter) -> 
            converter.getSupportedMediaTypes(requiredDescriptor, message)));
    }

    /**
     * 判断是否存在能读取指定类型消息的转换器
     * 
     * <p>遍历所有转换器，检查是否有转换器支持{@code targetDescriptor}类型和{@code contentType}媒体类型，
     * 常用于预处理阶段判断消息是否可被正确解析。
     * 
     * @param targetDescriptor 目标类型描述符（非空，指定解析后的类型）
     * @param message 待读取的消息（非空）
     * @param contentType 消息的媒体类型（可为null，null时匹配任意类型）
     * @return 存在支持的转换器返回true，否则返回false
     */
    @Override
    public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
            MimeType contentType) {
        return anyMatch((converter) -> 
            converter.isReadable(targetDescriptor, message, contentType));
    }

    /**
     * 从输入消息中读取并转换为目标类型对象
     * 
     * <p>按排序后的顺序遍历转换器，选择第一个支持{@code targetDescriptor}和{@code contentType}的转换器执行读取操作，
     * 排序器确保更具体的转换器（如支持"application/json"）优先于通配符转换器（如支持"*&#47;*"）。
     * 
     * @param targetDescriptor 目标类型描述符（非空，指定转换后的类型）
     * @param message 待读取的输入消息（非空，包含消息体和头信息）
     * @param contentType 消息的媒体类型（可为null，由转换器自动推断）
     * @return 转换后的对象，无支持的转换器时返回null
     * @throws IOException 转换过程中发生I/O错误（如流读取失败、格式解析错误）
     */
    @Override
    public Object readFrom(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
            MimeType contentType) throws IOException {
        for (MessageConverter converter : this) {
            if (converter.isReadable(targetDescriptor, message, contentType)) {
                return converter.readFrom(targetDescriptor, message, contentType);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("No converter found for reading: descriptor={}, contentType={}", 
                targetDescriptor, contentType);
        }
        return null;
    }

    /**
     * 判断是否存在能写入指定类型消息的转换器
     * 
     * <p>遍历所有转换器，检查是否有转换器支持{@code sourceDescriptor}类型和{@code contentType}媒体类型，
     * 常用于预处理阶段判断对象是否可被正确序列化。
     * 
     * @param sourceDescriptor 源类型描述符（非空，指定待序列化的类型）
     * @param message 目标消息（非空，用于写入结果）
     * @param contentType 目标媒体类型（可为null，null时由转换器自动选择）
     * @return 存在支持的转换器返回true，否则返回false
     */
    @Override
    public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
            MimeType contentType) {
        return anyMatch((converter) -> 
            converter.isWriteable(sourceDescriptor, message, contentType));
    }

    /**
     * 将源对象转换为指定媒体类型并写入输出消息
     * 
     * <p>按排序后的顺序遍历转换器，选择第一个支持{@code source}类型和{@code contentType}的转换器执行写入操作，
     * 确保优先使用最匹配的转换器（如JSON对象优先使用{@link JsonMessageConverter}而非通用文本转换器）。
     * 
     * @param source 待写入的源数据（非空，包含值和类型信息）
     * @param message 目标输出消息（非空，用于写入转换结果）
     * @param contentType 目标媒体类型（可为null，由转换器自动选择合适类型）
     * @throws IOException 转换过程中发生I/O错误（如流写入失败、序列化错误）
     */
    @Override
    public void writeTo(@NonNull TypedValue source, @NonNull OutputMessage message, MediaType contentType)
            throws IOException {
        for (MessageConverter converter : this) {
            if (converter.isWriteable(source, message, contentType)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Using converter {} for writing: source={}, contentType={}",
                        converter.getClass().getSimpleName(), source, contentType);
                }
                converter.writeTo(source, message, contentType);
                return;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("No converter found for writing: source={}, contentType={}", 
                source, contentType);
        }
    }
}