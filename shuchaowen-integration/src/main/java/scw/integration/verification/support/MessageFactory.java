package scw.integration.verification.support;

public interface MessageFactory<M, U> {
	M generatorMessage(int type, String code, U user);
}
