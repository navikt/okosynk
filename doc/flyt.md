# Flyten i en synk

Batchen bruker TinyFtpReader til å lese fila. Rene tekstlinjer sendes tilbake. 

```mermaid
sequenceDiagram
    Batch->>                TinyFtpReader: ftp-config+filnavn
    TinyFtpReader->>        Batch: List<String>
    Batch->> PDL: fnr, dnr
    PDL ->> Batch: aktørider
    Batch->>                OppgaveSynkroniserer: Oppgaver
    OppgaveSynkroniserer->> OppgaveRestClient: aktørider
    OppgaveRestClient   ->> OppgaveSynkroniserer: Åpne oppgaver opprettet av okosynk
    OppgaveSynkroniserer->> OppgaveRestClient: Oppgaver som ikke finnes fra før, OPPRETTES
    OppgaveSynkroniserer->> OppgaveRestClient: Oppgaver som finnes fra før og også er i fila, OPPDATERES
    OppgaveSynkroniserer->> OppgaveRestClient: Oppgaver som finnes fra før men ikke er i fila, FULLFØRES
    OppgaveSynkroniserer->> Batch: ok
    Batch->>TinyFtpReader: INPUT-fila skal renames
```

# Likhetskriterier
Samme gjelderId, gjelderIdType, ansvarligEnhetId.

...men gjelderIdType utledes fra gjelderId såeh...

I tillegg skiller 
OS på faggruppe og
UT på oppdragsKode
