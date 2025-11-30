package run.soeasy.framework.io;

import java.io.IOException;
import java.net.URLConnection;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapped;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.PipelineWrapper;
import run.soeasy.framework.core.function.ThrowingSupplier;

/**
 * URLConnection管道封装类，用于对{@link URLConnection}及其子类的流水线（Pipeline）操作进行封装，
 * 提供类型转换、单实例创建、基于供应商创建等便捷能力，简化URLConnection相关的IO异常处理流程。
 *
 * @param <T> 管道中处理的核心类型，限定为URLConnection的子类
 * @author YourName（建议补充作者信息）
 * @since 1.0.0（建议补充版本号）
 */
public class URLConnectionPipeline<T extends URLConnection> extends Wrapped<Pipeline<T, IOException>>
		implements PipelineWrapper<T, IOException, Pipeline<T, IOException>> {

	/**
	 * 构造URLConnectionPipeline实例，包装指定的Pipeline源对象。
	 *
	 * @param source 待包装的Pipeline核心实例，不能为空（由{@link NonNull}强制约束）
	 */
	public URLConnectionPipeline(@NonNull Pipeline<T, IOException> source) {
		super(source);
	}

	/**
	 * 将当前管道的处理类型转换为指定的URLConnection子类类型。
	 * <p>
	 * 转换逻辑基于{@link Class#cast(Object)}实现，若类型不兼容会抛出{@link ClassCastException}。
	 * </p>
	 *
	 * @param targetType 目标转换类型的Class对象，限定为URLConnection的子类
	 * @param <R>        目标转换后的URLConnection子类类型
	 * @return 转换类型后的URLConnectionPipeline实例
	 * @throws ClassCastException 当当前管道内的对象无法转换为目标类型时抛出
	 */
	public <R extends URLConnection> URLConnectionPipeline<R> cast(@NonNull Class<? extends R> targetType) {
		Pipeline<R, IOException> pipeline = map(targetType::cast);
		return new URLConnectionPipeline<>(pipeline);
	}

	/**
	 * 创建包含单个URLConnection实例的URLConnectionPipeline。
	 * <p>
	 * 适用于直接操作已有URLConnection实例的场景，底层通过无异常供应商封装实例。
	 * </p>
	 *
	 * @param urlConnection 要封装的URLConnection实例
	 * @param <R>           URLConnection子类类型
	 * @return 包含单个连接实例的URLConnectionPipeline
	 */
	public static <R extends URLConnection> URLConnectionPipeline<R> singleton(@NonNull R urlConnection) {
		return forSupplier(() -> urlConnection);
	}

	/**
	 * 基于异常抛出型供应商创建URLConnectionPipeline。
	 * <p>
	 * 供应商支持延迟创建URLConnection实例，且允许抛出{@link IOException}，适配连接创建可能失败的场景。
	 * </p>
	 *
	 * @param supplier 提供URLConnection实例的供应商，其{@link ThrowingSupplier#get()}方法可能抛出IOException
	 * @param <R>      URLConnection子类类型
	 * @return 基于供应商构建的URLConnectionPipeline
	 */
	public static <R extends URLConnection> URLConnectionPipeline<R> forSupplier(
			@NonNull ThrowingSupplier<R, IOException> supplier) {
		return new URLConnectionPipeline<>(supplier.closeable());
	}
}