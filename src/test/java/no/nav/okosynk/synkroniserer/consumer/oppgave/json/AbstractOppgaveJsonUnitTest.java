package no.nav.okosynk.synkroniserer.consumer.oppgave.json;

import no.nav.okosynk.synkroniserer.consumer.oppgave.OppgaveStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractOppgaveJsonUnitTest<OPPGAVEJSON extends AbstractOppgaveJson> {

    protected abstract OPPGAVEJSON createEmptyInstance();

    protected void fillWithAllHardCodedData(final OPPGAVEJSON oppgaveJson) {
        oppgaveJson.setAktivDato("tull");
        oppgaveJson.setAktoerId("AB12");
        oppgaveJson.setBehandlingstema("CD19");
        oppgaveJson.setBehandlingstype("xy13");
        oppgaveJson.setBeskrivelse("abc102");
        oppgaveJson.setBeskrivelse("abc102");
        oppgaveJson.setId("qpd99");
        oppgaveJson.setId("qpd99");
        oppgaveJson.setBnr("kl12ui");
        oppgaveJson.setOppgavetype("ops0909");
        oppgaveJson.setOrgnr("fikka13");
        oppgaveJson.setSamhandlernr("13io26");
        oppgaveJson.setTema("1def987");
        oppgaveJson.setMappeId("ghi876");
        oppgaveJson.setStatus(OppgaveStatus.OPPRETTET);
        oppgaveJson.setVersjon(13579);
        oppgaveJson.setEndretAv("bkjhbkjhbkjbkjbkj");
        oppgaveJson.setEndretTidspunkt("2020-12-31T13:08:14+01:00");
        oppgaveJson.setFerdigstiltTidspunkt("2020-12-31T13:08:14+01:00");
    }

    protected void setFieldsAnnotetedWithJsonIgnoreToNull(final OPPGAVEJSON oppgaveJson) {
        oppgaveJson.setId(null);
        oppgaveJson.setEndretAvEnhetsnr(null);
        oppgaveJson.setStatus(null);
        oppgaveJson.setOpprettetAv(null);
        oppgaveJson.setOpprettetTidspunkt(null);
        oppgaveJson.setVersjon(null);
        oppgaveJson.setEndretAv(null);
        oppgaveJson.setEndretTidspunkt(null);
        oppgaveJson.setFerdigstiltTidspunkt(null);
    }

    @Test
    void when_instance_is_compared_to_null_then_the_result_should_be_unequal() {
        final OPPGAVEJSON oppgaveJson = createEmptyInstance();
        assertFalse(oppgaveJson.equals(null));
    }

    @Test
    void when_instance_is_compared_to_itself_then_the_result_should_be_equal() {
        final OPPGAVEJSON oppgaveJson = createEmptyInstance();
        assertTrue(oppgaveJson.equals(oppgaveJson));
    }

    @Test
    void when_two_empty_instances_are_compared_then_the_result_should_be_equal() {

        final OPPGAVEJSON oppgaveJson1 = createEmptyInstance();
        final OPPGAVEJSON oppgaveJson2 = createEmptyInstance();

        assertTrue(oppgaveJson1.equals(oppgaveJson2));
    }

    @Test
    void when_instance_is_compared_to_a_subclass_then_the_result_should_be_unequal() {

        final OPPGAVEJSON oppgaveJson1 = createEmptyInstance();
        final AbstractOppgaveJson oppgaveJson2 = new AbstractOppgaveJson() {{
        }};

        assertFalse(oppgaveJson1.equals(oppgaveJson2));
    }

    @Test
    void when_selected_fields_differ_then_the_result_should_be_unequal() {

        final OPPGAVEJSON oppgaveJson1 = createEmptyInstance();
        final OPPGAVEJSON oppgaveJson2 = createEmptyInstance();

        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setAktivDato("tull");
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setAktivDato("tull");
        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setAktoerId("AB12");
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setAktoerId("AB12");
        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setBehandlingstema("CD19");
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setBehandlingstema("CD19");
        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setBehandlingstype("xy13");
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setBehandlingstype("xy13");
        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setBeskrivelse("abc102");
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setBeskrivelse("abc102");
        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setId("qpd99");
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setId("qpd99");
        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setBnr("kl12ui");
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setBnr("kl12ui");
        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setOppgavetype("ops0909");
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setOppgavetype("ops0909");
        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setOrgnr("fikka13");
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setOrgnr("fikka13");
        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setSamhandlernr("13io26");
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setSamhandlernr("13io26");
        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setTema("1def987");
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setTema("1def987");
        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setMappeId("ghi876");
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setMappeId("ghi876");
        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setStatus(OppgaveStatus.OPPRETTET);
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setStatus(OppgaveStatus.OPPRETTET);
        assertTrue(oppgaveJson1.equals(oppgaveJson2));

        oppgaveJson1.setVersjon(13579);
        assertFalse(oppgaveJson1.equals(oppgaveJson2));
        oppgaveJson2.setVersjon(13579);
        assertTrue(oppgaveJson1.equals(oppgaveJson2));
    }
}
