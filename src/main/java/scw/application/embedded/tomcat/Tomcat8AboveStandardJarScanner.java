package scw.application.embedded.tomcat;

import javax.servlet.ServletContext;

import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.scan.StandardJarScanner;

import scw.application.embedded.EmbeddedUtils;
import scw.core.PropertyFactory;

/**
 * tomcat8以上
 * 
 * @author shuchaowen
 *
 */
public class Tomcat8AboveStandardJarScanner extends StandardJarScanner {
	private boolean scanTld;// 是否进行tld扫描

	public Tomcat8AboveStandardJarScanner(PropertyFactory propertyFactory) {
		this.scanTld = EmbeddedUtils.tomcatScanTld(propertyFactory);
		setScanManifest(false);
	}

	@Override
	public void scan(JarScanType scanType, ServletContext context, JarScannerCallback callback) {
		if (!scanTld && scanType == JarScanType.TLD) {
			return;
		}
		super.scan(scanType, context, callback);
	}
}
