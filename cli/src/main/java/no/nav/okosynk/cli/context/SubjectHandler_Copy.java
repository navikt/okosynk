package no.nav.okosynk.cli.context;

import no.nav.brukerdialog.security.context.JbossSubjectHandler;
import no.nav.brukerdialog.security.domain.AuthenticationLevelCredential;
import no.nav.brukerdialog.security.domain.ConsumerId;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.brukerdialog.security.domain.OidcCredential;
import no.nav.brukerdialog.security.domain.SluttBruker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SubjectHandler_Copy {

    private static final Logger logger = LoggerFactory.getLogger(SubjectHandler_Copy.class);

    static final String SUBJECTHANDLER_KEY = "no.nav.modig.core.context.subjectHandlerImplementationClass";
    static final String JBOSS_PROPERTY_KEY = "jboss.home.dir";

    public SubjectHandler_Copy() {
    }

    public static SubjectHandler_Copy getSubjectHandler() {
        String subjectHandlerImplementationClass;
        if (runningOnJboss()) {
            subjectHandlerImplementationClass = JbossSubjectHandler.class.getName();
            logger.debug("Detected running on JBoss Application Server. Using: " + subjectHandlerImplementationClass);
        } else {
            subjectHandlerImplementationClass = resolveProperty("no.nav.modig.core.context.subjectHandlerImplementationClass");
        }

        if (subjectHandlerImplementationClass == null) {
            throw new RuntimeException("Du kjører på noe annet enn JBoss. Om du kjører i jetty og test må du konfigurere opp en System property med key no.nav.modig.core.context.subjectHandlerImplementationClass. Dette kan gjøres på følgende måte: System.setProperty(\"no.nav.modig.core.context.subjectHandlerImplementationClass\", ThreadLocalSubjectHandler.class.getName());");
        } else {
            try {
                Class<?> clazz = Class.forName(subjectHandlerImplementationClass);
                return (SubjectHandler_Copy)clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException var2) {
                throw new RuntimeException("Could not configure platform dependent subject handler", var2);
            }
        }
    }

    public abstract Subject getSubject();

    public String getUid() {
        if (!this.hasSubject()) {
            return null;
        } else {
            SluttBruker sluttBruker = (SluttBruker)this.getTheOnlyOneInSet(this.getSubject().getPrincipals(SluttBruker.class));
            return sluttBruker != null ? sluttBruker.getName() : null;
        }
    }

    public IdentType getIdentType() {
        if (!this.hasSubject()) {
            return null;
        } else {
            SluttBruker sluttBruker = (SluttBruker)this.getTheOnlyOneInSet(this.getSubject().getPrincipals(SluttBruker.class));
            return sluttBruker != null ? sluttBruker.getIdentType() : null;
        }
    }

    public String getInternSsoToken() {
        if (!this.hasSubject()) {
            return null;
        } else {
            OidcCredential tokenCredential = (OidcCredential)this.getTheOnlyOneInSet(this.getSubject().getPublicCredentials(OidcCredential.class));
            return tokenCredential != null ? tokenCredential.getToken() : null;
        }
    }

    public Integer getAuthenticationLevel() {
        if (!this.hasSubject()) {
            return null;
        } else {
            AuthenticationLevelCredential authenticationLevelCredential = (AuthenticationLevelCredential)this.getTheOnlyOneInSet(this.getSubject().getPublicCredentials(AuthenticationLevelCredential.class));
            return authenticationLevelCredential != null ? authenticationLevelCredential.getAuthenticationLevel() : null;
        }
    }

    String getConsumerId() {
        if (!this.hasSubject()) {
            return null;
        } else {
            ConsumerId consumerId = (ConsumerId)this.getTheOnlyOneInSet(this.getSubject().getPrincipals(ConsumerId.class));
            return consumerId != null ? consumerId.getConsumerId() : null;
        }
    }

    private <T> T getTheOnlyOneInSet(Set<T> set) {
        if (set.isEmpty()) {
            return null;
        } else {
            T first = set.iterator().next();
            if (set.size() == 1) {
                return first;
            } else {
                Set<String> classNames = (Set)set.stream().map(Object::getClass).map(Class::getName).collect(Collectors.toSet());
                String errorMessage = "Expected 1 or 0 items, but got " + set.size() + " items. Class of items: " + classNames;
                logger.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        }
    }

    private static String resolveProperty(String key) {
        String value = System.getProperty(key);
        if (value != null) {
            logger.debug("Setting " + key + "={} from System.properties", value);
        }

        return value;
    }

    private static boolean runningOnJboss() {
        return existsInProperties("jboss.home.dir");
    }

    private static boolean existsInProperties(String key) {
        return System.getProperties().containsKey(key);
    }

    private Boolean hasSubject() {
        return this.getSubject() != null;
    }

    public String toString() {
        return super.toString();
    }
}
