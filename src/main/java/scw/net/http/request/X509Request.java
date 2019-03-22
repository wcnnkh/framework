package scw.net.http.request;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.LinkedList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import scw.common.utils.ConfigUtils;
import scw.common.utils.StringUtils;
import scw.core.NestedRuntimeException;
import scw.net.http.enums.Method;

public class X509Request extends HttpRequest {
	private final SSLSocketFactory sslSocketFactory;

	public X509Request(Method method, String url, String filePath, String password) {
		super(method, url);
		try {
			this.sslSocketFactory = getSSLSocketFactory(filePath, password);
		} catch (Exception e) {
			throw new NestedRuntimeException(e);
		}
	}

	public SSLSocketFactory getSslSocketFactory() {
		return sslSocketFactory;
	}

	private SSLSocketFactory getSSLSocketFactory(String filePath, String password)
			throws KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, CertificateException,
			FileNotFoundException, IOException, KeyManagementException {
		KeyStore ks = KeyStore.getInstance("JKS");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(ConfigUtils.getFile(filePath));
			ks.load(fis, StringUtils.isEmpty(password) ? null : password.toCharArray());
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
			tmf.init(ks);
			TrustManager[] tms = tmf.getTrustManagers();
			LinkedList<X509TrustManager> list = new LinkedList<X509TrustManager>();
			for (TrustManager tm : tms) {
				if (tm instanceof X509TrustManager) {
					list.add((X509TrustManager) tm);
				}
			}

			SSLContext context = SSLContext.getInstance("SSL", "SunJSSE");
			context.init(null, list.toArray(new TrustManager[0]), new SecureRandom());
			return context.getSocketFactory();
		} finally {
			fis.close();
		}
	}

	@Override
	public void request(URLConnection urlConnection) throws Throwable {
		HttpsURLConnection https = (HttpsURLConnection) urlConnection;
		https.setSSLSocketFactory(sslSocketFactory);
		super.request(urlConnection);
	}
}
