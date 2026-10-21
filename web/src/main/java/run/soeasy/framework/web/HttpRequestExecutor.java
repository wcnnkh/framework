package run.soeasy.framework.web;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.ssl.SSLContexts;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.messaging.convert.support.QueryStringFormat;

/**
 * 函数式接口：HTTP请求执行器的核心抽象，整合媒体类型转换能力，定义HTTP客户端的通用操作契约。
 * 
 * <p>
 * 该接口作为HTTP通信的顶层抽象，旨在简化客户端调用逻辑，同时提供灵活的扩展点。它继承{@link MediaTypeConverterFactory}接口，
 * 实现了媒体类型与转换器的自动匹配，支持JSON、XML等多种数据格式的序列化/反序列化。
 * </p>
 * 
 * <p>
 * 核心能力包括：
 * <ul>
 * <li>基于Spring的{@link RequestEntity}和{@link ResponseEntity}封装请求/响应信息</li>
 * <li>自动处理查询参数拼接（支持对象到查询字符串的转换）</li>
 * <li>根据Content-Type和Accept头自动选择转换器处理请求体/响应体</li>
 * <li>支持HTTPS安全通信（通过SSLContext配置客户端证书）</li>
 * <li>提供「无请求体」「带请求体」两类HTTP方法的默认实现，减少重复编码</li>
 * </ul>
 * 
 * <p>
 * 函数式接口特性：通过{@link #doRequest(RequestEntity, Type)}方法实现函数式编程，可作为方法参数传递，
 * 简化HTTP调用的代码结构。
 * </p>
 * 
 * @see MediaTypeConverterFactory 媒体类型转换器工厂，提供转换器查找能力
 * @see HttpResponseEntity 扩展的响应实体，支持响应体动态转换
 * @see org.springframework.http.RequestEntity Spring的请求实体封装
 */
@FunctionalInterface
public interface HttpRequestExecutor extends MediaTypeConverterFactory {

	/**
	 * 根据媒体类型获取对应的转换器（实现{@link MediaTypeConverterFactory}接口）。
	 * <p>
	 * 默认从系统级转换器注册表{@link MediaTypeConverterRegistry}中查找，
	 * 支持根据Content-Type（如application/json）自动匹配合适的序列化/反序列化器。
	 * </p>
	 * 
	 * @param mediaType 媒体类型，用于匹配对应的转换器（不可为null）
	 * @return 匹配的转换器；若未找到对应转换器，可能返回null（取决于注册表实现）
	 */
	@Override
	default Converter getConverter(@NonNull MediaType mediaType) {
		return MediaTypeConverterRegistry.system().getConverter(mediaType);
	}

	/**
	 * 执行HTTP请求的核心方法（函数式接口的唯一抽象方法）。
	 * <p>
	 * 实现类需在此方法中完成实际的HTTP通信逻辑，包括：
	 * <ol>
	 * <li>发送请求（基于{@code requestEntity}中的URI、方法、头信息和请求体）</li>
	 * <li>接收响应并根据{@code responseType}完成响应体的类型转换</li>
	 * <li>处理通信异常（如连接超时、网络错误等）并封装为{@link WebException}</li>
	 * </ol>
	 * 
	 * @param <S>           请求体的类型
	 * @param <T>           响应体的目标类型
	 * @param requestEntity 封装完整请求信息的对象（包含URI、方法、头、请求体等，不可为null）
	 * @param responseType  响应体的目标类型（支持泛型，如List&lt;User&gt;的Type对象，不可为null）
	 * @return 包含状态码、响应头和转换后响应体的{@link ResponseEntity}
	 */
	<S, T> ResponseEntity<T> doRequest(@NonNull RequestEntity<S> requestEntity, @NonNull Type responseType);

	/**
	 * 重载的请求执行方法，提供更灵活的参数配置（简化请求构建流程）。
	 * <p>
	 * 自动处理：
	 * <ul>
	 * <li>查询参数拼接：将{@code params}转换为查询字符串并附加到URI</li>
	 * <li>请求体转换：根据Content-Type使用对应转换器将{@code body}转换为{@code bodyClass}类型</li>
	 * <li>请求实体构建：整合URI、方法、头信息、请求体为{@link RequestEntity}</li>
	 * </ul>
	 * 
	 * @param <S>          转换后的请求体类型
	 * @param <T>          响应体的目标类型
	 * @param uri          请求的基础URI（可能包含路径参数，不可为null）
	 * @param httpMethod   HTTP方法（如GET、POST、PUT等，不可为null）
	 * @param responseType 响应体的目标类型（不可为null）
	 * @param params       查询参数（可为null；支持String、Map或POJO对象）
	 * @param httpHeaders  请求头（可为null；若为null则使用默认头信息）
	 * @param body         请求体（可为null；如POST请求的表单数据或JSON）
	 * @param bodyClass    转换后的请求体类型（可为null；若为null则不强制转换请求体）
	 * @return 扩展的响应实体{@link HttpResponseEntity}，支持进一步转换响应体
	 */
	default <S, T> HttpResponseEntity<T> doRequest(@NonNull String uri, @NonNull HttpMethod httpMethod,
			@NonNull Type responseType, Object params, HttpHeaders httpHeaders, Object body, Class<S> bodyClass) {
		// 获取请求的Content-Type（用于参数和请求体转换）
		MediaType requestContentType = httpHeaders == null ? null : httpHeaders.getContentType();

		// 处理查询参数：将params转换为查询字符串并拼接至URI
		if (params != null) {
			String queryString;
			if (params instanceof String) {
				// 若参数是字符串，直接作为查询字符串（适用于已手动拼接的场景）
				queryString = (String) params;
			} else {
				// 选择查询字符串转换器（优先使用请求头字符集，默认UTF-8）
				Converter queryStringConverter;
				if (requestContentType != null && requestContentType.getCharset() != null) {
					queryStringConverter = QueryStringFormat.getFormat(requestContentType.getCharset());
				} else {
					queryStringConverter = QueryStringFormat.getFormat(StandardCharsets.UTF_8);
				}

				// 若参数不是Map，尝试转换为Map（便于生成查询字符串）
				if (!(params instanceof Map) && requestContentType != null) {
					Converter paramsConverter = getConverter(requestContentType);
					if (paramsConverter != null && paramsConverter.canConvert(params.getClass(), Map.class)) {
						params = paramsConverter.convert(params, Map.class);
					}
				}
				// 将参数转换为查询字符串（如a=1&b=2）
				queryString = queryStringConverter.convert(params, String.class);
			}
			// 拼接查询字符串到URI
			uri = UriComponentsBuilder.fromUriString(uri).query(queryString).build().toUriString();
		}

		// 处理请求体：根据Content-Type转换为目标类型bodyClass
		if (body != null && bodyClass != null && !bodyClass.isInstance(body) && requestContentType != null) {
			Converter bodyConverter = getConverter(requestContentType);
			if (bodyConverter != null && bodyConverter.canConvert(body.getClass(), bodyClass)) {
				body = bodyConverter.convert(body, bodyClass);
			}
		}

		// 构建RequestEntity并执行请求
		RequestEntity<Object> requestEntity = RequestEntity.method(httpMethod, URI.create(uri)).headers(httpHeaders)
				.body(body);
		ResponseEntity<T> responseEntity = doRequest(requestEntity, responseType);
		return HttpResponseEntity.assignableFrom(responseEntity, this);
	}

	/**
	 * 执行「无请求体」的HTTP方法的便捷方法（如GET、HEAD、OPTIONS等）。
	 * <p>
	 * 自动设置Accept头并合并用户传入的请求头，简化查询参数和响应类型的配置，仅支持无需携带请求体的HTTP方法。
	 * </p>
	 * 
	 * @param <T>             响应体的目标类型
	 * @param uri             请求URI（不可为null）
	 * @param params          查询参数（可为null）
	 * @param httpMethod      「无请求体」的HTTP方法（如GET、HEAD，不可为null）
	 * @param acceptMediaType 期望的响应媒体类型（如application/json，不可为null）
	 * @param responseType    响应体的目标类型（不可为null）
	 * @param httpHeaders     自定义请求头（可为null）
	 * @return 扩展的响应实体，支持响应体转换
	 */
	default <T> HttpResponseEntity<T> doEntitylessRequest(@NonNull String uri, Object params,
			@NonNull HttpMethod httpMethod, @NonNull MediaType acceptMediaType, @NonNull Type responseType,
			HttpHeaders httpHeaders) {
		// 合并请求头：优先使用用户传入的头，否则创建新头
		HttpHeaders headers = new HttpHeaders();
		if (httpHeaders != null) {
			headers.putAll(httpHeaders);
		}
		// 设置Accept头，指定期望的响应格式
		headers.setAccept(Arrays.asList(acceptMediaType));
		// 调用通用doRequest方法执行请求（无请求体）
		return doRequest(uri, httpMethod, responseType, params, headers, null, null);
	}

	/**
	 * 执行「带请求体」的HTTP方法的便捷方法（如POST、PUT、PATCH、DELETE等）。
	 * <p>
	 * 自动设置Content-Type（请求体格式）和Accept（响应格式）头，支持请求体转换和查询参数拼接，仅支持需携带请求体的HTTP方法。
	 * </p>
	 * 
	 * @param <S>             请求体的目标类型
	 * @param <T>             响应体的目标类型
	 * @param uri             请求URI（不可为null）
	 * @param params          查询参数（可为null）
	 * @param httpMethod      「带请求体」的HTTP方法（如POST、PUT，不可为null）
	 * @param acceptMediaType 期望的响应媒体类型（不可为null）
	 * @param responseType    响应体的目标类型（不可为null）
	 * @param httpHeaders     自定义请求头（可为null）
	 * @param contentType     请求体的媒体类型（如application/json，不可为null）
	 * @param content         请求体内容（可为null）
	 * @param bodyClass       转换后的请求体类型（可为null）
	 * @return 扩展的响应实体，支持响应体转换
	 */
	default <S, T> HttpResponseEntity<T> doEntityRequest(@NonNull String uri, Object params,
			@NonNull HttpMethod httpMethod, @NonNull MediaType acceptMediaType, @NonNull Type responseType,
			HttpHeaders httpHeaders, @NonNull MediaType contentType, Object content, Class<S> bodyClass) {
		// 合并请求头
		HttpHeaders headers = new HttpHeaders();
		if (httpHeaders != null) {
			headers.putAll(httpHeaders);
		}
		// 设置Accept和Content-Type头
		headers.setAccept(Arrays.asList(acceptMediaType));
		headers.setContentType(contentType);
		// 调用通用doRequest方法执行请求（含请求体）
		return doRequest(uri, httpMethod, responseType, params, headers, content, bodyClass);
	}
	
	/**
	 * 下载远程资源的便捷方法（默认使用GET请求）。
	 * <p>
	 * 简化远程文件/资源下载流程。
	 * 便于统一处理不同类型的资源（如文件、流、URL资源等）。
	 * </p>
	 * 
	 * @param <S>   请求体的目标类型（本方法无请求体，泛型仅为兼容重载方法）
	 * @param uri   资源的远程URI（不可为null，支持包含路径参数）
	 * @param params 查询参数（可为null；支持String、Map或POJO对象，自动拼接为URI查询字符串）
	 * @return 封装了下载的资源及响应头/状态码
	 * @throws IOException 当资源下载失败（如网络IO异常、响应体读取失败、资源不存在）时抛出
	 * @see #download(String, HttpMethod, Object, HttpHeaders, Object, Class) 支持自定义HTTP方法的重载方法
	 * @see Resource 统一资源操作接口
	 */
	default <S> HttpResponseEntity<Resource> download(@NonNull String uri, Object params) throws IOException {
		return download(uri, HttpMethod.GET, params, null, null, null);
	}

	/**
	 * 下载远程资源的通用方法（支持自定义HTTP方法、请求头、请求体）。
	 * <p>
	 * 核心下载逻辑实现：先通过{@link #doRequest}获取Spring {@link Resource}类型的响应体，支持POST/PUT等带请求体的下载场景。
	 * </p>
	 * 
	 * @param <S>         请求体的目标类型（可为null，无请求体时传null即可）
	 * @param uri         资源的远程URI（不可为null）
	 * @param httpMethod  HTTP方法（不可为null；推荐GET/POST，根据服务端下载接口要求选择）
	 * @param params      查询参数（可为null；自动拼接为URI查询字符串）
	 * @param httpHeaders 自定义请求头（可为null；如设置Authorization、Range等下载相关头）
	 * @param body        请求体（可为null；适用于POST下载接口需携带参数的场景）
	 * @param bodyClass   转换后的请求体类型（可为null；无请求体时传null）
	 * @return 封装了下载的资源及响应头/状态码
	 * @throws IOException 网络IO异常（如连接超时、响应截断）
	 * @see #download(String, Object) 简化版GET下载方法
	 * @see Resource 提供统一的资源访问API
	 */
	default <S> HttpResponseEntity<Resource> download(@NonNull String uri, @NonNull HttpMethod httpMethod,
			Object params, HttpHeaders httpHeaders, Object body, Class<S> bodyClass) throws IOException {
		return doRequest(uri, httpMethod, Resource.class, params, httpHeaders,
				body, bodyClass);
	}

	/**
	 * 加载SSL密钥材料（客户端证书），配置HTTPS通信的安全上下文。
	 * <p>
	 * 适用于需要客户端认证的HTTPS场景，支持.p12/.jks等格式的密钥库。处理流程：
	 * <ol>
	 * <li>验证密钥库资源是否存在且可读</li>
	 * <li>通过{@link SSLContexts}加载密钥库，创建SSLContext</li>
	 * <li>调用{@link #setSSLContext(SSLContext)}应用安全配置</li>
	 * </ol>
	 * 
	 * @param keyMaterialResource 包含私钥和证书链的密钥库资源（不可为null）
	 * @param storePassword       密钥库密码（解锁密钥库，不可为null）
	 * @param keyPassword         私钥密码（解锁私钥，不可为null）
	 * @throws WebException 当资源无效、密码错误或SSLContext创建失败时抛出
	 */
	default void loadKeyMaterial(@NonNull Resource keyMaterialResource, @NonNull String storePassword,
			@NonNull String keyPassword) {
		// 验证资源有效性
		if (!keyMaterialResource.exists()) {
			throw new IllegalArgumentException("Key material resource not found: " + keyMaterialResource);
		}

		// 构建SSL上下文（支持文件或URL形式的资源）
		SSLContext sslContext;
		try {
			if (keyMaterialResource.isFile()) {
				sslContext = SSLContexts.custom().loadKeyMaterial(keyMaterialResource.getFile(),
						storePassword.toCharArray(), keyPassword.toCharArray()).build();
			} else {
				sslContext = SSLContexts.custom().loadKeyMaterial(keyMaterialResource.getURL(),
						storePassword.toCharArray(), keyPassword.toCharArray()).build();
			}
		} catch (KeyManagementException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException
				| CertificateException | IOException e) {
			throw new WebException("Failed to load SSL key material from " + keyMaterialResource.getDescription(), e);
		}

		// 应用SSL上下文到HTTP客户端
		setSSLContext(sslContext);
	}

	/**
	 * 设置SSL上下文，配置HTTPS通信的安全参数。
	 * <p>
	 * SSLContext包含加密套件、证书验证策略等安全配置，实现类需重写此方法以将其应用到实际的HTTP客户端（如配置请求工厂的SSL套接字工厂）。
	 * </p>
	 * 
	 * @param sslContext 要应用的SSL上下文（null表示使用默认安全配置）
	 * @throws WebException 当SSL上下文配置失败（如不支持的协议、无效配置）时抛出
	 */
	default void setSSLContext(SSLContext sslContext) throws WebException {
		throw new WebException("Not supported in default implementation, please override");
	}
}