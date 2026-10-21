package run.soeasy.framework.commons.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.io.source.InputStreamSource;
import run.soeasy.framework.io.source.LastModifiedAware;
import run.soeasy.framework.io.source.OutputStreamSource;

/**
 * Spring {@link Resource} 到 {@link InputStreamSource} /
 * {@link OutputStreamSource} 的适配器。
 *
 * <p>
 * 将 Spring 资源抽象适配为框架的 IO 源接口，使 Spring 生态中的资源 （如
 * {@code ClassPathResource}、{@code FileSystemResource}、 {@code UrlResource}
 * 等）能够无缝接入 Source 管道体系。
 *
 * <p>
 * <b>可读/可写判定：</b>
 * <ul>
 * <li>可读性委托给 {@link Resource#isReadable()}</li>
 * <li>可写性通过检测 {@code resource} 是否为 {@link WritableResource} 实例判定</li>
 * </ul>
 *
 * <p>
 * <b>资源责任：</b>
 * <ul>
 * <li>{@link #getInputStream()} 委托给 {@link Resource#getInputStream()}</li>
 * <li>{@link #getOutputStream()} 仅在底层为 {@link WritableResource} 时可用，否则抛出
 * {@link IOException}</li>
 * <li>通过继承获得的 {@link #readableChannel()} / {@link #writableChannel()} 将流包装为 NIO
 * 通道，通道关闭时联动关闭底层流</li>
 * </ul>
 *
 * <p>
 * <b>关于 {@link Resource#isOpen()}：</b> 如果底层 {@code resource} 的 {@code isOpen()}
 * 返回 {@code true} （如 {@code InputStreamResource}），则每次调用
 * {@link #getInputStream()} 返回的是<b>同一个已打开的流</b>，多次消费将导致数据丢失。 调用者应遵循 Spring
 * {@link Resource} 的语义约定。
 *
 * @author soeasy.run
 */
@RequiredArgsConstructor
@Getter
public final class ResourceAdapter
		implements InputStreamSource<InputStream>, OutputStreamSource<OutputStream>, LastModifiedAware {

	/**
	 * 为指定的 Spring {@link Resource} 创建适配器。
	 *
	 * @param resource 底层资源
	 * @return 适配器实例
	 */
	public static ResourceAdapter of(Resource resource) {
		return resource == null ? null : new ResourceAdapter(resource);
	}

	/**
	 * 为多个 Spring {@link Resource} 批量创建适配器。
	 *
	 * <p>
	 * 如果 {@code resources} 中包含 {@code null} 元素，返回的数组中 对应位置也将为 {@code null}，不会抛出异常。
	 *
	 * @param resources 底层资源数组
	 * @return 适配器数组，顺序与输入一致，null 元素对应位置为 null
	 */
	public static ResourceAdapter[] of(Resource... resources) {
		if (resources == null) {
			return null;
		}

		ResourceAdapter[] adapters = new ResourceAdapter[resources.length];
		for (int i = 0; i < resources.length; i++) {
			Resource res = resources[i];
			adapters[i] = res == null ? null : new ResourceAdapter(res);
		}
		return adapters;
	}

	@NonNull
	private final Resource resource;

	@Override
	public @NonNull InputStream getInputStream() throws IOException {
		return resource.getInputStream();
	}

	@Override
	public @NonNull OutputStream getOutputStream() throws IOException {
		if (resource instanceof WritableResource) {
			return ((WritableResource) resource).getOutputStream();
		}
		throw new IOException("Resource is not writable: " + resource);
	}

	/**
	 * 判断底层资源是否可读。
	 *
	 * @return 委托给 {@link Resource#isReadable()}
	 */
	public boolean isReadable() {
		return resource.isReadable();
	}

	/**
	 * 判断底层资源是否可写。
	 *
	 * @return 如果底层资源是 {@link WritableResource} 且可写则返回 {@code true}
	 */
	public boolean isWritable() {
		if (resource instanceof WritableResource) {
			return ((WritableResource) resource).isWritable();
		}
		return false;
	}

	/**
	 * 获取底层资源的最后修改时间。
	 *
	 * <p>
	 * 委托给 {@link Resource#lastModified()}。
	 *
	 * @return 最后修改时间，单位为毫秒（自 epoch 起）
	 * @throws IOException 如果底层资源不支持该操作或发生 I/O 错误
	 */
	@Override
	public long lastModified() throws IOException {
		return resource.lastModified();
	}

	@Override
	public String toString() {
		return "ResourceAdapter{resource=" + resource + "}";
	}
}