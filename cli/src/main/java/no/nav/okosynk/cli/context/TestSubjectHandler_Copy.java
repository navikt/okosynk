package no.nav.okosynk.cli.context;

import javax.security.auth.Subject;

public abstract class TestSubjectHandler_Copy
        extends SubjectHandler_Copy  {

    public TestSubjectHandler_Copy() {
    }

    public abstract void setSubject(Subject var1);

    public abstract void reset();
}
