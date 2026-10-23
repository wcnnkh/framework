package run.soeasy.framework.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * HTTPS 请求工厂类<br>
 * 继承自 {@link SimpleClientHttpRequestFactory}，添加 SSL 套接字工厂配置功能， 支持自定义 HTTPS 连接的
 * SSL 上下文配置
 * 
 * <p>
 * 核心功能：
 * <ul>
 * <li>自定义 SSL 套接字工厂：通过 {@link #setSslSocketFactory(SSLSocketFactory)} 设置</li>
 * <li>HTTPS 连接自动配置：检测到 HttpsURLConnection 时应用 SSL 配置</li>
 * <li>保持 HTTP 连接处理逻辑：继承父类的 HTTP 连接管理功能</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see SimpleClientHttpRequestFactory
 * @see SSLSocketFactory
 */
public class SimpleClientHttpsRequestFactory extends SimpleClientHttpRequestFactory {
	private SSLSocketFactory sslSocketFactory;

	/**
	 * 重写连接打开方法，添加 HTTPS 连接的 SSL 配置
	 * 
	 * <p>
	 * 处理逻辑：
	 * <ul>
	 * <li>调用父类方法创建原始连接</li>
	 * <li>若为 HTTPS 连接，调用 {@link #afterProperties} 配置 SSL</li>
	 * <li>保持非 HTTPS 连接的原生处理逻辑</li>
	 * </ul>
	 * 
	 * @param url   请求 URL
	 * @param proxy 代理设置
	 * @return 配置后的 HTTP 连接
	 * @throws IOException 连接创建失败时抛出
	 */
	@Override
	protected HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
		HttpURLConnection httpURLConnection = super.openConnection(url, proxy);
		if (httpURLConnection instanceof HttpsURLConnection) {
			afterProperties((HttpsURLConnection) httpURLConnection);
		}
		return httpURLConnection;
	}

	/**
	 * 配置 HTTPS 连接的 SSL 套接字工厂（模板方法）
	 * 
	 * @param httpsURLConnection HTTPS 连接实例
	 */
	protected void afterProperties(HttpsURLConnection httpsURLConnection) {
		if (sslSocketFactory != null) {
			httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
		}
	}

	public SSLSocketFactory getSslSocketFactory() {
		return sslSocketFactory;
	}

	public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.sslSocketFactory = sslSocketFactory;
	}

}