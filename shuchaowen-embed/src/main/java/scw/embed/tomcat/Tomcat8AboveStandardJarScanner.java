package scw.embed.tomcat;

import javax.servlet.ServletContext;

import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.scan.StandardJarScanner;

import scw.beans.annotation.Configuration;
import scw.core.PropertyFactory;
import scw.embed.EmbeddedUtils;

/**
 * tomcat8以上
 * 
 * @author shuchaowen
 *
 */
@Configuration(order=Integer.MIN_VALUE)
public class Tomcat8AboveStandardJarScanner extends StandardJarScanner {
	private boolean scanTdl;
	
	public Tomcat8AboveStandardJarScanner(PropertyFactory propertyFactory) {
		this.scanTdl = EmbeddedUtils.tomcatScanTld(propertyFactory);
		setScanManifest(false);
	}

	@Override
	public void scan(JarScanType scanType, ServletContext context, JarScannerCallback callback) {
		if (!scanTdl && scanType == JarScanType.TLD) {
			return;
		}
		super.scan(scanType, context, callback);
	}
}
