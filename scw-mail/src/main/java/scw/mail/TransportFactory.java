package scw.mail;

import javax.mail.Transport;

public interface TransportFactory {
	Transport getTransport();
}
