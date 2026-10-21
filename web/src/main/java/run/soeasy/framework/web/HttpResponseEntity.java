package run.soeasy.framework.web;

import java.lang.reflect.Type;
import java.util.function.Function;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 扩展 Spring 原生 {@link ResponseEntity} 的响应实体接口，核心增强「泛型友好的类型转换」与「转换器工厂动态管理」能力。
 * 
 * <p>设计目标：解决 Spring 原生响应体转换中「泛型擦除导致复杂类型（如 List&lt;User&gt;）转换困难」「转换策略无法动态切换」的问题，
 * 同时完全兼容原生 {@link ResponseEntity} 的核心能力（响应体、状态码、响应头管理），可无缝集成到 Spring Web 生态（如 Controller 返回、RestTemplate 响应处理）。
 * 
 * <p>核心能力拆解：
 * <ul>
 * <li><b>{@code cast} 类型转换</b>：支持普通 Class 类型（如 User）和泛型 Type 类型（如 List&lt;User&gt;），自动适配转换器，无需手动处理泛型擦除；
 * <li><b>{@code map} 函数式转换</b>：提供两种模式——Lambda 自定义转换（如字段裁剪）、基于 ContentType 自动匹配转换器（如 JSON→XML 格式转换）；
 * <li><b>{@code assignableFrom} 转换器工厂切换</b>：动态绑定 {@link MediaTypeConverterFactory}，适配不同业务场景（如接口版本差异化格式转换）。
 * </ul>
 * 
 * <p>集成关系：与框架类型转换体系深度联动，依赖 {@link Converter} 定义转换规则、{@link SystemConversionService} 提供默认转换能力，
 * 推荐在响应处理链路（如拦截器、统一响应处理器）中使用，简化多格式、多类型的响应适配逻辑。
 *
 * @param <T> 响应体的原始数据类型（如 Controller 返回的 User、List&lt;Order&gt; 等）
 * @see MediaTypeConverterFactory 媒体类型转换器工厂，根据 ContentType（如 application/json）匹配对应的转换器
 * @see MappedHttpResponseEntity 函数式转换的实现类，承载 Lambda 转换逻辑
 * @see AssignableHttpResponseEntity 转换器工厂管理的实现类，负责绑定自定义转换工厂
 * @see org.springframework.http.ResponseEntity Spring 原生响应实体，当前接口的兼容基础
 */
public interface HttpResponseEntity<T> {

    /**
     * 静态工厂方法：创建「绑定指定转换器工厂」的 {@link HttpResponseEntity} 实例
     * 
     * <p>核心作用：将 Spring 原生响应实体包装为扩展接口实例，同时注入自定义转换器工厂，后续通过 {@code cast} 或 {@code map} 转换时，
     * 会优先使用该工厂匹配的转换器（而非系统默认转换器），适用于需要定制转换规则的场景（如特定接口返回 Protobuf 格式）。
     *
     * @param responseEntity        Spring 原生响应实体（不可为 null，提供响应体、状态码、响应头等原始数据）
     * @param mediaTypeConverterFactory 用于类型转换的媒体类型转换器工厂（不可为 null，需提前初始化并注册对应 ContentType 的转换器）
     * @param <V>                   响应体的原始类型（与传入的 responseEntity 响应体类型一致）
     * @return 绑定了转换器工厂的 {@link HttpResponseEntity} 实例（具体实现为 {@link StandardHttpResponseEntity}）
     * @throws NullPointerException 若 responseEntity 或 mediaTypeConverterFactory 为 null（由 {@link NonNull} 注解强制校验）
     */
    public static <V> HttpResponseEntity<V> assignableFrom(@NonNull ResponseEntity<V> responseEntity,
                                                           @NonNull MediaTypeConverterFactory mediaTypeConverterFactory) {
        return new StandardHttpResponseEntity<>(responseEntity, mediaTypeConverterFactory);
    }

    /**
     * 获取响应体内容（原始类型）
     *
     * @return 响应体对象，可能为 null（需通过 {@link #hasBody()} 提前判断是否存在非空响应体，避免空指针异常）
     */
    T getBody();

    /**
     * 获取 HTTP 响应头信息（包含所有响应头字段，如 Content-Type、Authorization 等）
     *
     * @return 非 null 的 {@link HttpHeaders} 对象：若响应无自定义头，返回空的 HttpHeaders 实例（而非 null）
     */
    HttpHeaders getHeaders();

    /**
     * 获取 HTTP 状态码（枚举形式，包含状态码含义描述）
     *
     * @return 非 null 的 {@link HttpStatus} 枚举（如 HttpStatus.OK 对应 200，HttpStatus.NOT_FOUND 对应 404）
     */
    HttpStatus getStatusCode();

    /**
     * 获取 HTTP 状态码的数值形式（便于直接用于数值比较或日志打印）
     *
     * @return 状态码整数（如 200 表示成功、400 表示参数错误、500 表示服务器异常）
     */
    int getStatusCodeValue();

    /**
     * 判断响应是否包含「非空响应体」
     * 
     * <p>注意：区别于「响应体存在但为 null」的场景（如 Controller 返回 null）和「无响应体」的场景（如 204 No Content 状态码），
     * 该方法仅在「响应体存在且不为 null」时返回 true。
     *
     * @return true：响应体存在且非 null；false：响应体为 null 或无响应体
     */
    boolean hasBody();

    /**
     * 将响应体转换为「指定泛型类型」（支持复杂泛型，如 List&lt;User&gt;、Map&lt;String, Order&gt;）
     * 
     * <p>委托 {@link #map(Type, MediaTypeConverterFactory)} 实现，默认不指定转换器工厂（使用系统默认转换逻辑），
     * 泛型类型需通过 {@code TypeToken} 获取（如 {@code new TypeToken<List<User>>(){}.getType()}），避免泛型擦除导致转换失败。
     *
     * @param bodyType 目标泛型类型的 {@link Type} 对象（不可为 null，需准确描述泛型结构）
     * @param <R>      转换后的响应体类型（如 List&lt;User&gt;）
     * @return 包含转换后响应体的新 {@link HttpResponseEntity} 实例（响应头、状态码与原实例一致）
     * @throws NullPointerException 若 bodyType 为 null
     * @throws run.soeasy.framework.codec.CodecException 若转换失败（如类型不兼容、无匹配转换器）
     */
    default <R> HttpResponseEntity<R> cast(Type bodyType) {
        return map(bodyType, null);
    }

    /**
     * 将响应体转换为「指定普通 Class 类型」（非泛型，如 User、String、Integer）
     * 
     * <p>委托 {@link #map(Type, MediaTypeConverterFactory)} 实现，适用于简单类型转换，无需处理泛型擦除，
     * 若原始响应体可直接强转为目标类型（如 Object→User），会跳过转换器直接强转，提升性能。
     *
     * @param bodyType 目标类型的 {@link Class} 对象（不可为 null，需为非泛型类型）
     * @param <R>      转换后的响应体类型（如 User）
     * @return 包含转换后响应体的新 {@link HttpResponseEntity} 实例（响应头、状态码与原实例一致）
     * @throws NullPointerException 若 bodyType 为 null
     * @throws run.soeasy.framework.codec.CodecException 若转换失败（如类型不兼容、无匹配转换器）
     */
    default <R> HttpResponseEntity<R> cast(Class<R> bodyType) {
        return map(bodyType, null);
    }

    /**
     * 切换当前响应实体使用的「媒体类型转换器工厂」（动态替换转换策略）
     * 
     * <p>创建 {@link AssignableHttpResponseEntity} 实例并绑定新工厂，后续通过 {@code cast} 或 {@code map} 转换时，
     * 会优先使用新工厂的转换器（覆盖原工厂逻辑），适用于同一响应需要适配多格式的场景（如 PC 端返回 JSON、移动端返回 ProtoBuf）。
     *
     * @param mediaTypeConverterFactory 新的媒体类型转换器工厂（不可为 null，需提前注册所需 ContentType 的转换器）
     * @return 绑定新工厂的 {@link HttpResponseEntity} 实例（具体实现为 {@link AssignableHttpResponseEntity}）
     * @throws NullPointerException 若 mediaTypeConverterFactory 为 null（由 {@link NonNull} 注解强制校验）
     */
    default HttpResponseEntity<T> assignableFrom(@NonNull MediaTypeConverterFactory mediaTypeConverterFactory) {
        return new AssignableHttpResponseEntity<>(this, mediaTypeConverterFactory);
    }

    /**
     * 通过「函数式接口」自定义响应体转换逻辑（灵活处理简单转换场景）
     * 
     * <p>基于 {@link MappedHttpResponseEntity} 实现，直接执行传入的 Lambda 表达式（如字段裁剪、格式重命名），
     * 不依赖转换器工厂，适用于无需复用的一次性转换（如将 User 转换为 UserVO，仅保留 id 和 name 字段）。
     *
     * @param mapper 转换函数（接收原始类型 &lt;T&gt;，返回目标类型 &lt;R&gt;，不可为 null，需确保函数内无空指针风险）
     * @param <R>    转换后的响应体类型（如 UserVO）
     * @return 包含转换后响应体的新 {@link HttpResponseEntity} 实例（具体实现为 {@link MappedHttpResponseEntity}）
     * @throws NullPointerException 若 mapper 为 null（由 {@link NonNull} 注解强制校验）
     * @throws RuntimeException 若 mapper 函数内部抛出异常（如空指针、类型转换异常）
     */
    default <R> HttpResponseEntity<R> map(@NonNull Function<? super T, ? extends R> mapper) {
        return new MappedHttpResponseEntity<>(this, mapper);
    }

    /**
     * 核心转换逻辑：根据「目标类型」和「转换器工厂」自动完成响应体转换（泛型与普通类型通用）
     * 
     * <p>转换优先级与流程（按顺序执行，找到可用转换器后终止）：
     * 1. <b>非泛型类型快速校验</b>：若目标类型无泛型参数（如 User、String），且原始响应体可直接强转为目标类型（{@code isInstance} 为 true），则直接强转（避免冗余转换）；
     * 2. <b>转换器查找（按优先级）</b>：
     *    - 优先级1：使用传入的 {@code converterFactory}，根据响应头 ContentType（如 application/json）获取转换器；
     *    - 优先级2：若工厂无匹配，从 {@link MediaTypeConverterRegistry} 系统注册表查找对应 ContentType 的转换器；
     *    - 优先级3：若注册表无匹配，使用 {@link SystemConversionService} 提供的默认转换服务；
     * 3. <b>执行转换</b>：通过找到的转换器，结合目标类型的 {@link TypeDescriptor}（描述泛型结构）完成转换。
     *
     * @param bodyType         目标类型（支持泛型，不可为 null，需通过 {@code TypeToken} 或 {@code Class} 准确描述）
     * @param converterFactory 用于查找转换器的工厂（可为 null，null 时跳过优先级1，直接进入优先级2）
     * @param <R>              转换后的响应体类型（如 List&lt;User&gt;、UserVO）
     * @return 包含转换后响应体的新 {@link HttpResponseEntity} 实例（响应头、状态码与原实例一致）
     * @throws NullPointerException 若 bodyType 为 null
     * @throws run.soeasy.framework.codec.CodecException 若无匹配转换器，或转换过程中出现类型不兼容
     */
    @SuppressWarnings("unchecked")
    default <R> HttpResponseEntity<R> map(@NonNull Type bodyType, MediaTypeConverterFactory converterFactory) {
        return map((e) -> {
            // 将目标 Type 转换为 ResolvableType，便于解析泛型参数（如 List<User> 中的 User）
            ResolvableType resolvableType = ResolvableType.forType(bodyType);
            // 处理非泛型类型：直接强转（若类型兼容）
            if (!resolvableType.hasActualTypeArguments()) {
                if (resolvableType.getRawType().isInstance(e)) {
                    return (R) resolvableType.getRawType().cast(e);
                }
            }

            // 从响应头获取 ContentType，用于匹配对应的转换器（无 ContentType 时直接进入默认转换）
            MediaType contentType = getHeaders().getContentType();
            Converter converter = null;
            if (contentType != null) {
                // 1. 优先使用传入的转换器工厂
                converter = converterFactory == null ? null : converterFactory.getConverter(contentType);
                // 2. 工厂无匹配时，从系统注册表获取
                if (converter == null) {
                    converter = MediaTypeConverterRegistry.system().getConverter(contentType);
                }
            }

            // 3. 无匹配转换器时，使用默认系统转换服务
            if (converter == null) {
                converter = SystemConversionService.getInstance();
            }

            // 执行转换：基于 ResolvableType 构建 TypeDescriptor，确保泛型信息不丢失
            return (R) converter.convert(e, TypeDescriptor.forType(resolvableType));
        });
    }

    /**
     * 将当前扩展响应实体转换为「Spring 原生 {@link ResponseEntity}」（兼容原生 API）
     * 
     * <p>核心用途：当需要与 Spring 原生组件（如 RestTemplate 的 {@code exchange} 方法、Controller 直接返回）交互时，
     * 通过该方法保留所有响应信息（响应体、响应头、状态码），避免扩展能力与原生生态冲突。
     *
     * @return 与当前响应信息完全一致的 Spring 原生 {@link ResponseEntity} 实例：
     *         - 响应体：若 {@link #hasBody()} 为 true，返回原始响应体；否则为 null；
     *         - 响应头：直接复用当前实例的 {@link HttpHeaders}（引用传递，修改会影响原实例）；
     *         - 状态码：基于 {@link #getStatusCodeValue()} 构建，确保数值一致。
     */
    default ResponseEntity<T> shared() {
        return new ResponseEntity<T>(hasBody() ? getBody() : null, getHeaders(), getStatusCodeValue());
    }
}