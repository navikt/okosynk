package no.nav.okosynk.consumer.interceptor;

import no.nav.modig.security.ws.attributes.SystemSAMLAttributes;

public class SystemSAMLAttributesMedBruker extends SystemSAMLAttributes {

    private final String brukernavn;

    public SystemSAMLAttributesMedBruker(String brukernavn) {
        this.brukernavn = brukernavn;
    }

    @Override
    public String getUid() {
        return brukernavn;
    }
}
