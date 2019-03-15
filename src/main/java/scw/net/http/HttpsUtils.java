package scw.net.http;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.LinkedList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import scw.common.utils.ConfigUtils;

public class HttpsUtils {
	public static X509TrustManager[] loadX509TrustManager(String filePath, String password)
			throws KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, CertificateException,
			FileNotFoundException, IOException {
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(ConfigUtils.getFile(filePath)), password.toCharArray());
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
		tmf.init(ks);
		TrustManager[] tms = tmf.getTrustManagers();
		LinkedList<X509TrustManager> list = new LinkedList<X509TrustManager>();
		for (TrustManager tm : tms) {
			if (tm instanceof X509TrustManager) {
				list.add((X509TrustManager) tm);
			}
		}
		return list.toArray(new X509TrustManager[0]);
	}

	public static SSLSocketFactory getSSLSocketFactory(X509TrustManager[] tms)
			throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException {
		SSLContext context = SSLContext.getInstance("SSL", "SunJSSE");
		context.init(null, tms, new SecureRandom());
		return context.getSocketFactory();
	}
}
