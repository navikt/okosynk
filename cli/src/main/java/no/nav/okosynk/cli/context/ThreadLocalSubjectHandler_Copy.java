package no.nav.okosynk.cli.context;

import javax.security.auth.Subject;

public class ThreadLocalSubjectHandler_Copy
    extends TestSubjectHandler_Copy {

    private static ThreadLocal<Subject> subjectHolder = new ThreadLocal();

    public ThreadLocalSubjectHandler_Copy() {
    }

    public Subject getSubject() {
        return (Subject)subjectHolder.get();
    }

    public void setSubject(Subject subject) {
        subjectHolder.set(subject);
    }

    public void reset() {
        this.setSubject((Subject)null);
    }
}
