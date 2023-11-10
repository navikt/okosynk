# Opprette oppgaver

## Aggregering
Først deles oppgavene inn etter FunksjonelleAggregeringskriterier

### OsMeldingFunksjonelleAggregeringsKriterier
Meldinger fra OS deles inn etter Faggruppe, GjelderId, GjelderIdType og AnsvarligEnhetId.

### UrMeldingFunksjonelleAggregeringsKriterier
Meldinger fra UR deles inn etter Oppdragskode, GjelderId, GjelderIdType og AnsvarligEnhetId.

## Opprettelse av oppgave

```mermaid
stateDiagram-v2
    direction TB
    [*] --> OpprettOppgave: Alle meldinger med samme FunksjonelleAggregeringskriterier
    OpprettOppgave --> MappingRegel
    MappingRegel --> GjelderId:finner
    GjelderId --> BNR: 21 <= Måned <= 32
    GjelderId --> ORGANISASJON: Starter med 00
    GjelderId --> SAMHANDLER: Starter med 8,9
    GjelderId --> hentGjeldendeAktørId: (fnr/dnr)
    BNR --> [*]
    ORGANISASJON --> [*]
    SAMHANDLER --> [*]
    hentGjeldendeAktørId --> AKTORID: finnes
    note right of MappingRegel
        Ser etter kombinasjonen av
        Faggruppe/Oppdragskode og
        Behandlende enhet i
        *_mapping_regler.properties
    end note
    MappingRegel --> feilmelding: finner ikke
    note right of hentGjeldendeAktørId
        Sjekker Pdl for
        gjeldende ident
        med gruppe AKTORID
    end note
    hentGjeldendeAktørId --> feilmelding: finnes ikke
    note left of feilmelding
        Kan ikke
        behandle
        melding!
    end note
    AKTORID --> [*]
```
