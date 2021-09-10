package no.nav.okosynk.consumer.oppgave.json;

import org.junit.jupiter.api.Test;

public class PostOppgaveResponseJsonUnitTest  extends AbstractOppgaveJsonUnitTest<PostOppgaveResponseJson> {

    @Override
    protected PostOppgaveResponseJson createEmptyInstance() {
        return new PostOppgaveResponseJson();
    }

    @Test
    void dummy_to_get_it_run_through_abstract_uniut_test() {}
}
