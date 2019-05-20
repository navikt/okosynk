package no.nav.okosynk.consumer.interceptor;

import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SAMLCallbackHandler;

import java.util.Map;

public class SystemSAMLOutInterceptorMedBruker extends AbstractSAMLOutInterceptor {
    private String brukernavn;

    @SuppressWarnings("unused")
    public SystemSAMLOutInterceptorMedBruker() {
        super(false);
        this.getProperties().put("samlCallbackRef", this.getCallbackHandler());
    }

    public SystemSAMLOutInterceptorMedBruker(String brukernavn) {
        super(false);
        this.brukernavn = brukernavn;
        this.getProperties().put("samlCallbackRef", this.getCallbackHandler());
    }

    @SuppressWarnings("unused")
    public SystemSAMLOutInterceptorMedBruker(Map<String, Object> props) {
        super(false, props);
        this.getProperties().put("samlCallbackRef", this.getCallbackHandler());
    }

    protected SAMLCallbackHandler getCallbackHandler() {
        return new SAMLCallbackHandler(new SystemSAMLAttributesMedBruker(brukernavn));
    }
}
