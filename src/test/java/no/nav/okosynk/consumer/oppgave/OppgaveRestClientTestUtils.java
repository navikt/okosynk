package no.nav.okosynk.consumer.oppgave;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static no.nav.okosynk.config.Constants.OPPGAVE_URL_KEY;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OppgaveRestClientTestUtils {

    static CloseableHttpResponse reponseWithErrorCodeGreaterThan400 =
            new CloseableHttpResponseWithAllMethodsImplementedAndThrowing() {

                @Override
                public StatusLine getStatusLine() {
                    return new StatusLineWithAllMethodsImplementedAndThrowing() {
                        @Override
                        public int getStatusCode() {
                            return HttpResponseStatus.NOT_FOUND.code();
                        }
                    };
                }

                @Override
                public HttpEntity getEntity() {
                    return new HttpEntityWithAllMethodsImplementedAndThrowing() {
                        @Override
                        public InputStream getContent() throws IOException, UnsupportedOperationException {
                            final ErrorResponse errorResponse = new ErrorResponse() {
                            };
                            errorResponse.setUuid(UUID.randomUUID().toString());
                            errorResponse.setFeilmelding("Simulated crash");
                            final ObjectMapper objectMapper = new ObjectMapper();
                            final String errorAsJsonString =
                                    objectMapper.writeValueAsString(errorResponse);
                            final InputStream errorAsJsonStringInputStream =
                                    IOUtils.toInputStream(errorAsJsonString, Charset.defaultCharset());
                            return errorAsJsonStringInputStream;
                        }
                    };
                }

                @Override
                public void close() {
                }
            };

    static OppgaveRestClient prepareAMockedOppgaveRestClientThatSucceedsInCreatingZeroOppgaver()
            throws IOException {

        final CloseableHttpResponse preparedCloseableHttpResponse =
                new CloseableHttpResponseWithAllMethodsImplementedAndThrowing() {

                    @Override
                    public void close() {
                    }

                    @Override
                    public StatusLine getStatusLine() {
                        return new StatusLineWithAllMethodsImplementedAndThrowing() {
                            @Override
                            public int getStatusCode() {
                                return HttpResponseStatus.OK.code();
                            }
                        };
                    }

                    @Override
                    public HttpEntity getEntity() {
                        return new HttpEntityWithAllMethodsImplementedAndThrowing() {
                            @Override
                            public InputStream getContent() throws IOException, UnsupportedOperationException {
                                final InputStream oppgaveDtoAsJsonStringInputStream =
                                        IOUtils.toInputStream("", Charset.defaultCharset());

                                return oppgaveDtoAsJsonStringInputStream;
                            }
                        };
                    }
                };

        final OppgaveRestClient oppgaveRestClient =
                OppgaveRestClientTestUtils
                        .prepareAMockedOpprettOppgaverRestClientBaseThatDoesNotFail();

        when(oppgaveRestClient.executeRequest(any(), any()))
                .thenReturn(preparedCloseableHttpResponse);

        return oppgaveRestClient;
    }

    static OppgaveRestClient prepareAMockedOppgaveRestClientThatSucceedsInCreatingOneOppgave()
            throws IOException {

        final CloseableHttpResponse preparedCloseableHttpResponse =
                new CloseableHttpResponseWithAllMethodsImplementedAndThrowing() {

                    @Override
                    public void close() {
                    }

                    @Override
                    public StatusLine getStatusLine() {
                        return new StatusLineWithAllMethodsImplementedAndThrowing() {
                            @Override
                            public int getStatusCode() {
                                return HttpResponseStatus.OK.code();
                            }
                        };
                    }

                    @Override
                    public HttpEntity getEntity() {
                        return new HttpEntityWithAllMethodsImplementedAndThrowing() {
                            @Override
                            public InputStream getContent() throws IOException, UnsupportedOperationException {
                                final ObjectMapper objectMapper = new ObjectMapper();
                                objectMapper.setAnnotationIntrospector(new DisablingJsonIgnoreIntrospector());

                                final OppgaveDto oppgaveDto = new OppgaveDto();
                                final Random random = new Random();
                                oppgaveDto.setOpprettetTidspunkt(
                                        OppgaveRestClientTestUtils.createRandomDateTimeWithZone(random));
                                oppgaveDto.setEndretTidspunkt(
                                        OppgaveRestClientTestUtils.createRandomDateTimeWithZone(random));
                                oppgaveDto.setFerdigstiltTidspunkt(
                                        OppgaveRestClientTestUtils.createRandomDateTimeWithZone(random));

                                final String oppgaveDtoAsJsonString =
                                        objectMapper.writeValueAsString(oppgaveDto);
                                final InputStream oppgaveDtoAsJsonStringInputStream =
                                        IOUtils.toInputStream(oppgaveDtoAsJsonString, Charset.defaultCharset());

                                return oppgaveDtoAsJsonStringInputStream;
                            }
                        };
                    }
                };

        final OppgaveRestClient oppgaveRestClient =
                OppgaveRestClientTestUtils
                        .prepareAMockedOpprettOppgaverRestClientBaseThatDoesNotFail();

        when(oppgaveRestClient.executeRequest(any(), any()))
                .thenReturn(preparedCloseableHttpResponse);

        return oppgaveRestClient;
    }


    static OppgaveRestClient prepareAMockedOppgaveRestClientThatSucceedsInFindingZeroOppgaver()
            throws IOException {

        final OppgaveRestClient mockedOppgaveRestClient = prepareAMockedFinnOppgaveRestClientBaseThatDoesNotFail();
        when(mockedOppgaveRestClient
                .executeRequest(any(CloseableHttpClient.class), any(HttpUriRequest.class)))
                .thenReturn(createTestResponseWithNOppgaver(0));

        return mockedOppgaveRestClient;
    }

    static OppgaveRestClient prepareAMockedOppgaveRestClientThatSucceedsInFindingOneOppgave()
            throws IOException {

        final OppgaveRestClient mockedOppgaveRestClient = prepareAMockedFinnOppgaveRestClientBaseThatDoesNotFail();
        when(mockedOppgaveRestClient
                .executeRequest(any(CloseableHttpClient.class), any(HttpUriRequest.class)))
                .thenReturn(createTestResponseWithNOppgaver(1))
                .thenReturn(createTestResponseWithNOppgaver(0))
        ;

        return mockedOppgaveRestClient;
    }

    static OppgaveRestClient prepareAMockedOppgaveRestClientThatSucceedsInFinding19Oppgaver()
            throws IOException {

        final OppgaveRestClient mockedOppgaveRestClient = prepareAMockedFinnOppgaveRestClientBaseThatDoesNotFail();
        when(mockedOppgaveRestClient
                .executeRequest(any(CloseableHttpClient.class), any(HttpUriRequest.class)))
                .thenReturn(createTestResponseWithNOppgaver(19))
                .thenReturn(createTestResponseWithNOppgaver(0))
        ;

        return mockedOppgaveRestClient;
    }

    static OppgaveRestClient prepareAMockedOppgaveRestClientThatSucceedsInFinding50Oppgaver()
            throws IOException {

        final OppgaveRestClient mockedOppgaveRestClient = prepareAMockedFinnOppgaveRestClientBaseThatDoesNotFail();
        when(mockedOppgaveRestClient
                .executeRequest(any(CloseableHttpClient.class), any(HttpUriRequest.class)))
                .thenReturn(createTestResponseWithNOppgaver(50))
                .thenReturn(createTestResponseWithNOppgaver(0))
        ;

        return mockedOppgaveRestClient;
    }

    static OppgaveRestClient prepareAMockedOppgaveRestClientThatSucceedsInFinding51Oppgaver()
            throws IOException {

        final OppgaveRestClient mockedOppgaveRestClient = prepareAMockedFinnOppgaveRestClientBaseThatDoesNotFail();
        when(mockedOppgaveRestClient
                .executeRequest(any(CloseableHttpClient.class), any(HttpUriRequest.class)))
                .thenReturn(createTestResponseWithNOppgaver(50))
                .thenReturn(createTestResponseWithNOppgaver(1))
                .thenReturn(createTestResponseWithNOppgaver(0))
        ;

        return mockedOppgaveRestClient;
    }

    static OppgaveRestClient prepareAMockedFinnOppgaveRestClientBaseThatDoesNotFail()
            throws IOException {

        System.setProperty(OPPGAVE_URL_KEY, "https://oppgave.nais.adeo.no/api/v1/oppgaver");
        final OppgaveRestClient mockedOppgaveRestClient = mock(OppgaveRestClient.class);
        when(mockedOppgaveRestClient.finnOppgaver(anySet())).thenCallRealMethod();
        when(mockedOppgaveRestClient.getUsernamePasswordCredentials())
                .thenReturn(new UsernamePasswordCredentials("someRubbishUser", "someRubbishPassword"));
        when(mockedOppgaveRestClient.getOkosynkConfiguration())
                .thenReturn(new FakeOkosynkConfiguration());
        when(mockedOppgaveRestClient.getBatchType()).thenReturn(BATCH_TYPE.UR);

        return mockedOppgaveRestClient;
    }

    static OppgaveRestClient prepareAMockedOpprettOppgaverRestClientBaseThatDoesNotFail() {

        System.setProperty(OPPGAVE_URL_KEY, "https://oppgave.nais.adeo.no/api/v1/oppgaver");
        final OppgaveRestClient mockedOppgaveRestClient = mock(OppgaveRestClient.class);
        when(mockedOppgaveRestClient.opprettOppgaver(anyCollection())).thenCallRealMethod();
        when(mockedOppgaveRestClient.getUsernamePasswordCredentials())
                .thenReturn(new UsernamePasswordCredentials("someRubbishUser", "someRubbishPassword"));
        when(mockedOppgaveRestClient.getOkosynkConfiguration())
                .thenReturn(new FakeOkosynkConfiguration());
        when(mockedOppgaveRestClient.getBatchType()).thenReturn(BATCH_TYPE.UR);

        return mockedOppgaveRestClient;
    }

    static OppgaveRestClient prepareAMockedPatchOppgaverRestClientBaseThatDoesNotFail() {

        System.setProperty(OPPGAVE_URL_KEY, "https://oppgave.nais.adeo.no/api/v1/oppgaver");
        final OppgaveRestClient mockedOppgaveRestClient = mock(OppgaveRestClient.class);
        when(mockedOppgaveRestClient.patchOppgaver(anySet(), anyBoolean())).thenCallRealMethod();
        when(mockedOppgaveRestClient.getUsernamePasswordCredentials())
                .thenReturn(new UsernamePasswordCredentials("someRubbishUser", "someRubbishPassword"));
        when(mockedOppgaveRestClient.getOkosynkConfiguration())
                .thenReturn(new FakeOkosynkConfiguration());
        when(mockedOppgaveRestClient.getBatchType()).thenReturn(BATCH_TYPE.UR);

        return mockedOppgaveRestClient;
    }

    static OppgaveRestClient prepareAMockedOpprettOppgaveRestClientThatFailsWithAnHttpCodeGreaterThan400()
            throws IOException {

        final OppgaveRestClient mockedOppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedOpprettOppgaveRestClientBaseThatFailsWithAnHttpCodeGreaterThan400();

        return mockedOppgaveRestClient;
    }

    static OppgaveRestClient prepareAMockedPatchOppgaverRestClientThatFailsWithAnHttpCodeGreaterThan400()
            throws IOException {

        final OppgaveRestClient mockedOppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedPatchOppgaveRestClientBaseThatFailsWithAnHttpCodeGreaterThan400();

        return mockedOppgaveRestClient;
    }

    private static OppgaveRestClient prepareAMockedOpprettOppgaveRestClientBaseThatFailsWithAnHttpCodeGreaterThan400()
            throws IOException {

        System.setProperty(OPPGAVE_URL_KEY, "https://oppgave.nais.adeo.no/api/v1/oppgaver");
        final OppgaveRestClient mockedOppgaveRestClient = mock(OppgaveRestClient.class);
        when(mockedOppgaveRestClient.opprettOppgaver(anyCollection())).thenCallRealMethod();
        when(mockedOppgaveRestClient.getUsernamePasswordCredentials())
                .thenReturn(new UsernamePasswordCredentials("someRubbishUser", "someRubbishPassword"));
        when(mockedOppgaveRestClient.getOkosynkConfiguration())
                .thenReturn(new FakeOkosynkConfiguration());
        when(mockedOppgaveRestClient.getBatchType()).thenReturn(BATCH_TYPE.UR);
        when(mockedOppgaveRestClient.executeRequest(any(CloseableHttpClient.class), any(HttpUriRequest.class)))
                .thenReturn(OppgaveRestClientTestUtils.reponseWithErrorCodeGreaterThan400);

        return mockedOppgaveRestClient;
    }

    private static OppgaveRestClient prepareAMockedPatchOppgaveRestClientBaseThatFailsWithAnHttpCodeGreaterThan400()
            throws IOException {

        System.setProperty(OPPGAVE_URL_KEY, "https://oppgave.nais.adeo.no/api/v1/oppgaver");
        final OppgaveRestClient mockedOppgaveRestClient = mock(OppgaveRestClient.class);
        when(mockedOppgaveRestClient.patchOppgaver(anyCollection(), anyBoolean())).thenCallRealMethod();
        when(mockedOppgaveRestClient.getUsernamePasswordCredentials())
                .thenReturn(new UsernamePasswordCredentials("someRubbishUser", "someRubbishPassword"));
        when(mockedOppgaveRestClient.getOkosynkConfiguration())
                .thenReturn(new FakeOkosynkConfiguration());
        when(mockedOppgaveRestClient.getBatchType()).thenReturn(BATCH_TYPE.UR);
        when(mockedOppgaveRestClient.executeRequest(any(CloseableHttpClient.class), any(HttpUriRequest.class)))
                .thenReturn(OppgaveRestClientTestUtils.reponseWithErrorCodeGreaterThan400);

        return mockedOppgaveRestClient;
    }

    private static String createRandomDate(final Random random, final boolean mayBeNull) {

        final String randomDate;
        if ((random.nextInt(1000) < 900) || !mayBeNull) {
            randomDate =
                    String.format(
                            "%04d-%02d-%02d",
                            2013 + random.nextInt(5),
                            1 + random.nextInt(11),
                            1 + random.nextInt(27)
                    );
        } else {
            randomDate = null;
        }

        return randomDate;
    }

    static String createRandomDateTimeWithZone(final Random random) {

        final String randomDateTimeWithZone =
                String.format(
                        "%04d-%02d-%02dT%02d:%02d:%02d+02:00[Europe/Paris]",
                        2013 + random.nextInt(5),
                        1 + random.nextInt(11),
                        1 + random.nextInt(27),
                        random.nextInt(23),
                        random.nextInt(59),
                        random.nextInt(59)
                );

        return randomDateTimeWithZone;
    }

    private static OppgaveDto createOneOppgaveDto(final Random random) {
        final OppgaveDto oppgaveDto = new OppgaveDto();

        oppgaveDto.setAktivDato(createRandomDate(random, true));
        oppgaveDto.setVersjon(1 + random.nextInt(319));
        oppgaveDto.setAktoerId(RandomStringUtils.randomAlphanumeric(7));
        oppgaveDto.setStatus(OppgaveStatus.values()[random.nextInt(OppgaveStatus.values().length)]);
        oppgaveDto.setOpprettetTidspunkt(createRandomDateTimeWithZone(random));
        oppgaveDto.setEndretTidspunkt(createRandomDateTimeWithZone(random));
        oppgaveDto.setFerdigstiltTidspunkt(createRandomDateTimeWithZone(random));
        oppgaveDto.setId(RandomStringUtils.randomNumeric(7, 17));
        oppgaveDto.setSamhandlernr(RandomStringUtils.randomAlphanumeric(23));
        oppgaveDto.setOrgnr(RandomStringUtils.randomAlphanumeric(13));
        oppgaveDto.setBnr(RandomStringUtils.randomAlphanumeric(11));
        oppgaveDto.setOppgavetype(RandomStringUtils.randomAlphanumeric(11));
        oppgaveDto.setTema(RandomStringUtils.randomAlphanumeric(11));
        oppgaveDto.setBehandlingstema(RandomStringUtils.randomAlphanumeric(17));
        oppgaveDto.setBehandlingstype(RandomStringUtils.randomAlphanumeric(12));
        oppgaveDto.setPrioritet(RandomStringUtils.randomAlphanumeric(19));
        oppgaveDto.setBeskrivelse(RandomStringUtils.randomAlphanumeric(319));
        oppgaveDto.setFristFerdigstillelse(createRandomDate(random, true));
        oppgaveDto.setTildeltEnhetsnr(RandomStringUtils.randomAlphanumeric(7));
        oppgaveDto.setMappeId(RandomStringUtils.randomAlphanumeric(21));
        oppgaveDto.setTilordnetRessurs(RandomStringUtils.randomAlphanumeric(14));

        return oppgaveDto;
    }

    private static CloseableHttpResponse createTestResponseWithNOppgaver(final int noOppgaver) {

        final CloseableHttpResponse closeableHttpResponse =
                new CloseableHttpResponseWithAllMethodsImplementedAndThrowing() {

                    final StatusLine statusLine = new StatusLineWithAllMethodsImplementedAndThrowing() {
                        @Override
                        public int getStatusCode() {
                            return HttpResponseStatus.OK.code();
                        }
                    };

                    final HttpEntity httpEntity = new HttpEntityWithAllMethodsImplementedAndThrowing() {

                        final int noOppgaverWanted = noOppgaver;

                        @Override
                        public InputStream getContent() throws IOException, UnsupportedOperationException {

                            final FinnOppgaveResponse finnOppgaveResponse =
                                    new FinnOppgaveResponse() {

                                        @Override
                                        public List<OppgaveDto> getOppgaver() {

                                            if (super.getOppgaver() == null) {
                                                final List<OppgaveDto> oppgaver = new ArrayList<>();
                                                int counter = 0;
                                                final Random random = new Random(889735);

                                                while (counter++ < noOppgaverWanted) {
                                                    oppgaver.add(createOneOppgaveDto(random));
                                                }
                                                super.setOppgaver(oppgaver);
                                                super.setAntallTreffTotalt(oppgaver.size());
                                            }
                                            return super.getOppgaver();
                                        }

                                        @Override
                                        public int getAntallTreffTotalt() {
                                            this.getOppgaver(); // Guarantee that the oppgaver have been created
                                            return super.getAntallTreffTotalt();
                                        }
                                    };

                            final ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.setAnnotationIntrospector(new DisablingJsonIgnoreIntrospector());

                            final String oppgaveAsJsonString =
                                    objectMapper.writeValueAsString(finnOppgaveResponse);
                            final InputStream oppgaveAsJsonStringInputStream =
                                    IOUtils.toInputStream(oppgaveAsJsonString, Charset.defaultCharset());

                            return oppgaveAsJsonStringInputStream;
                        }
                    };

                    @Override
                    public ProtocolVersion getProtocolVersion() {
                        return getStatusLine().getProtocolVersion();
                    }

                    @Override
                    public StatusLine getStatusLine() {
                        return this.statusLine;
                    }

                    @Override
                    public HttpEntity getEntity() {
                        return httpEntity;
                    }

                    @Override
                    public void close() throws IOException {
                    }
                };

        return closeableHttpResponse;
    }

    static OppgaveRestClient prepareAMockedPatchRestClientThatSucceedsInCreatingZeroOppgaver()
            throws IOException {

        final CloseableHttpResponse preparedCloseableHttpResponse =
                new CloseableHttpResponseWithAllMethodsImplementedAndThrowing() {

                    @Override
                    public void close() {
                    }

                    @Override
                    public StatusLine getStatusLine() {
                        return new StatusLineWithAllMethodsImplementedAndThrowing() {
                            @Override
                            public int getStatusCode() {
                                return HttpResponseStatus.OK.code();
                            }
                        };
                    }

                    @Override
                    public HttpEntity getEntity() {
                        return new HttpEntityWithAllMethodsImplementedAndThrowing() {
                            @Override
                            public InputStream getContent() throws IOException, UnsupportedOperationException {
                                final InputStream oppgaveDtoAsJsonStringInputStream =
                                        IOUtils.toInputStream("", Charset.defaultCharset());

                                return oppgaveDtoAsJsonStringInputStream;
                            }
                        };
                    }
                };

        final OppgaveRestClient oppgaveRestClient =
                OppgaveRestClientTestUtils
                        .prepareAMockedPatchOppgaverRestClientBaseThatDoesNotFail();

        when(oppgaveRestClient.executeRequest(any(), any()))
                .thenReturn(preparedCloseableHttpResponse);

        return oppgaveRestClient;
    }

    static OppgaveRestClient prepareAMockedPatchRestClientThatSucceedsInCreatingOneOppgave()
            throws IOException {

        final CloseableHttpResponse preparedCloseableHttpResponse =
                new CloseableHttpResponseWithAllMethodsImplementedAndThrowing() {

                    @Override
                    public void close() {
                    }

                    @Override
                    public StatusLine getStatusLine() {
                        return new StatusLineWithAllMethodsImplementedAndThrowing() {
                            @Override
                            public int getStatusCode() {
                                return HttpResponseStatus.OK.code();
                            }
                        };
                    }

                    @Override
                    public HttpEntity getEntity() {
                        return new HttpEntityWithAllMethodsImplementedAndThrowing() {
                            @Override
                            public InputStream getContent() throws IOException, UnsupportedOperationException {
                                final ObjectMapper objectMapper = new ObjectMapper();
                                objectMapper.setAnnotationIntrospector(new DisablingJsonIgnoreIntrospector());

                                final PatchOppgaverResponse patchOppgaverResponse = new PatchOppgaverResponse();

                                patchOppgaverResponse.setFeilet(0);
                                patchOppgaverResponse.setSuksess(1);
                                patchOppgaverResponse.setTotalt(1);

                                final String patchOppgaverResponseAsJsonString =
                                        objectMapper.writeValueAsString(patchOppgaverResponse);
                                final InputStream patchOppgaverResponseAsJsonStringInputStream =
                                        IOUtils.toInputStream(patchOppgaverResponseAsJsonString, Charset.defaultCharset());

                                return patchOppgaverResponseAsJsonStringInputStream;
                            }
                        };
                    }
                };

        final OppgaveRestClient oppgaveRestClient =
                OppgaveRestClientTestUtils
                        .prepareAMockedPatchOppgaverRestClientBaseThatDoesNotFail();

        when(oppgaveRestClient.executeRequest(any(), any()))
                .thenReturn(preparedCloseableHttpResponse);

        return oppgaveRestClient;
    }
}