# okosynk
Applikasjon for å synkronisere oppgaver fra økonomisystemene OS (Oppdragssystemet) og UR (Utbetalingsreskontro) mot oppgave-applikasjonen (tidligere Gsak).
Applikasjonen leser flatfiler bestående av meldinger fra OS og UR. Noen av meldingene aggregeres
dersom de gjelder samme oppgave. Fra de resterende meldingene opprettes det oppgaver, og det er
disse oppgavene som skal ligge i oppgave-applikasjonen.

* Oppgaver som ligger i oppgave-applikasjonen, men ikke er tilstede i flatfil, ferdigstilles.
* Oppgaver som ligger både i oppgave-applikasjonen og i flatfil oppdateres med ny informasjon.
* Oppgaver som ligger i flatfil men ikke i oppgave-applikasjonen blir opprettet i oppgave-applikasjonen.

Okosynk er en batchjobb som kjører kl. UTC 4:00 hver morgen hele året (altså kl 05:00 om vinteren og kl 0:600 om sommeren norsk tid).
Den kjører på nais-plattformen i to miljøer:
1) Cluster `preprod-fss`, i namespace `oppgavehandtering`
2) Cluster `prod-fss`, i namespace `oppgavehandtering`

Okosynk kjøres som to applikasjoner, og de heter hhv. okosynkos og okosynkur (nais name). Det er samme koden som kjører, 
men de er konfigurert forskjellig. 
Miljø-variabelen SHOULD_RUN_OS_OR_UR styrer dette, og den kan ha verdiene "OS" eller "UR".

# Testkjøring av batchen
## Lokal testkjøring av batchen
Her beskrives den enkle og frittstående varianten hvor aksessen til providerne er mocka. 
De mocka requestene og responsene blir logga til konsollet. 
Ved å lage en test-property-fil hvor endepunktene er endra til preprod-endepunktene, kan det hende at det er mulig å 
teste lokalt mot disse hvis naisdevice tillater det. Det er ikke forsøkt. 
### Lokal testkjøring av batchen out-of-the-box 
1. Start konsollapplikasjonen (iTerm2 eller DOS-vindu, avhengig av OS) (4)
0. Gå til prosjektrota (f.eks. /Users/r149852/nav/okosynk)
0. Fra kommandolinja, kjør:
    1. `./copyInputFiles.sh` (1) (2)
    0. `java -ea -jar target/okosynk-local-test-run.jar --propFile application-test.testset_001.properties` (3)
       
(1) Antar her at applikasjonen er kompilert og testa vellykka med `mvn clean install` ellser `mvn clean install -DskipTests=true`

(2) I og med at batchen renamer inputfilene, må disse regenereres, og det kan f.eks. gjøres med denne kommandoen (du kan selvfølgelig finne på en smartere og raskere måte å gjøre akkurat dét på)

(3) Den varianten du ønsker å kjøre avgjøres av innholdet i miljø-variabelen SHOULD_RUN_OS_OR_UR, som kan ha verdiene OS eller UR 

(4) Dette kan med hell kjøres innafra IDE-en, noe som vel er å foretrekke når man bedriver utvikling der. I så fall, kjør run eller debug på no.nav.okosynk.cli.CliMainWithTestScope. Regenerering av input-filer som nevnt ovenfor gjelder selvsagt også før kjøring i IDE-en.

### Lokal testkjøring av batchen med skreddersøm
- Kopiér og rename følgende filer...
    - ... src/test/resources/__files/aktoerRegisterResponseFnrToAktoerId.testset_001.json
    - ... src/test/resources/__files/stsResponse.testset_001.json
    - ... src/test/resources/__files/oppgaveResponseFinnOppgaver.testset_001.json
    - ... src/test/resources/__files/oppgaveResponseOpprettOppgaver.testset_001.json
    - ... src/test/resources/__files/oppgaveResponsePatchOppgaver.testset_001.json
    - ... src/test/resources/application-test.testset_001.properties
    - ... src/test/resources/os.testset_001.input
    - ... src/test/resources/ur.testset_001.input

til ditto ...testset_nnn...
1. Endre innholdet i ...testset_nnn...-filene slik at de reflekterer det du spesifikt trenger å teste og at slik at det blir konsistens i test-dataene
0. `./copyInputFiles.sh`
0. `java -ea -jar target/okosynk-local-test-run.jar --propFile application-test.testset_nnn.properties`

## Testkjøring av batchen i preprod
1. Sjekk adressen(e) til inputfil(ene) i nais/app-preprod.yaml under `OSFTPBASEURL_URL` og/eller `URFTPBASEURL_URL`
0. Legg filene du ønsker å teste der, eller rename allerede kjørt(e) fil(er). (Etter en vellykka kjøring blir nemlig inputfilene renama med en timestamp)
0. Start en batchkjøring som beskrevet annet sted i denne dokumentasjonen.

# Bygg og deployment
## Lokalt
`mvn clean install`
## Preprod/Prod
Ved innsjekking til master-greina på GitHub bygges og deployeres okosynk implisitt til både preprod og prod.
Dette skjer på GitHub vha. action-scriptene
`<PROJECT ROOT>/.github/workflows/deploy-dev-prod.yaml` 
og
`<PROJECT ROOT>/.github/workflows/issue-deploy.yml`.
 Hvis dette ikke er ønskelig, bør man vurdere å arbeide på en egen grein.

# Sjekk hvordan det står til i drift

```
kubectl config use-context <riktig cluster> (enten `preprod-fss` eller `prod-fss`)
kubectl config set-context <riktig cluster> --namespace=<riktig namespace> (<riktig namespace> er `oppgavehandtering` uavhengig av om det er `preprod` eller `prod`)
```

Ved å kjøre

`kubectl get cronjobs`

får man en liste over alle cronjobs i dette
namespacet, og okosynk skal være blant dem.

Man får et resultat alla det her:

```
NAME      SCHEDULE    SUSPEND   ACTIVE    LAST SCHEDULE   AGE
okosynk   0 5 * * *   False     0         3h              2d
```

Dette viser at cronjob-en kjører 0 5 * * *, som betyr kl 05:00 UTC hver dag. Man
ser også "last schedule" som er hvor lenge det er siden siste kjøring, og "age" som
er hvor lenge det er siden cronjob-en ble deployet i ny versjon.

Så kan man gjøre

`kubectl get jobs`,

og da får man opp noe som kan ligne på det følgende:

```
NAME                                               COMPLETIONS   DURATION   AGE
okosynkos-1626667200                               0/1           4h55m      4h55m
okosynkur-1626667200                               0/1           4h55m      4h55m
```

En cronjob i Kubernetes vil opprette en ny `job` for hver kjøring. Standard oppførsel
er at disse jobbene blir liggende igjen etter at de er ferdige, slik at man kan lese ut
logger osv. De blir automatisk slettet etter en tid. (Man kan konfigurere og fine-tune
når de skal slettes, men Kubernetes har en fin default.)

Hvis det ligger veldig mange jobber inne i clusteret, kan man f.eks. kjøre

`kubectl get jobs | grep okosynk`

for kun å få okosynk-jobbene. (Det er usikkert hvordan
`grep` vil fungere fra et Windows-image. Antakeligvis har kubectl et kommandolinjeflagg
for å filtrere jobber.)

I lista over jobber kan man se når alle jobbene sist kjørte, og antall forsøk de trengte
for å være successful - i dette tilfellet var det enkelt, alle jobbene kjørte fint på
første forsøk.

# Hvordan gikk kjøringene?

## Logging
Resultatet kan kontrolleres ved å se på loggene i Kibana. Se særlig etter strengen `STATISTIKK`.
Loggen konfigureres i `src/main/resources/logback.xml`.
Forhåpentligvis vil loggene ende opp i Kibana (`https://logs.adeo.no`), men man kan også
lese dem direkte fra Kubernetes. Først må man få en liste av pods tilhørende okosynk:

`kubectl get pods`

```
NAME                                                     READY   STATUS     RESTARTS   AGE
okosynkos-1626667200-4rgvn                               1/2     NotReady   0          4h57m
okosynkur-1626667200-htp4t                               1/2     NotReady   0          4h57m
```

Når status er "Completed", gikk jobben bra. "Age" viser når
hver pod begynte å kjøre. Hvis vi skal sjekke loggene til den siste pod-en, kan vi
kjøre

`kubectl logs okosynkos-1626667200-4rgvn okosynk`
eller
`kubectl logs okosynkos-1626667200-4rgvn vks-sidecar`
eller
`kubectl logs okosynkos-1626667200-4rgvn vks-init`

og da kommer loggene til pod-en opp i terminalen.<BR/>
Og for å finne ut hvorvidt jobbene er vellykka fullførte:

`kubectl logs okosynk-1556078400-gfdhr vks-init | grep -i fullført`

Følgene kommando er heller ikke å forakte (her får du f.eks. lista opp miljøvariablene + mye mer...):<BR/>

`kubectl describe pod okosynk-1536642000-j6ccz`
 
## SQL

```
    SELECT *
    FROM oppgave_p.oppgave o
    WHERE    o.opprettet_av IN ('srvbokosynk001', 'srvbokosynk002')
         AND o.status_id IN (1,2,3)
         AND o.tema = 'OKO'
         AND o.oppgavetype IN ('OKO_OS', 'OKO_UR')
    ORDER BY o.opprettet_tidspunkt DESC;
```

```
    SELECT   o.tildelt_enhetsnr
           , o.tema, o.behandlingstype
           , o.behandlingstema
           , oppgavetype
           , COUNT(*) AS antall
           , MIN(o.endret_tidspunkt)
           , MAX(o.endret_tidspunkt)
    FROM oppgave_p.oppgave o
    WHERE
            o.oppgavetype IN ('OKO_OS', 'OKO_UR')
        AND o.opprettet_av IN ('srvbokosynk001', 'srvbokosynk002')
        AND o.opprettet_tidspunkt >= to_timestamp('2021-06-15 0300','YYYY-MM-DD HH24MI')    
    GROUP BY o.tildelt_enhetsnr, o.tema, o.behandlingstype, o.behandlingstema, o.oppgavetype
    ORDER BY antall DESC;
```

```
    CREATE TYPE strings AS TABLE OF VARCHAR2(40);
    /
    CREATE TYPE numbers AS TABLE OF NUMBER(10);
    /
    SELECT
    TO_CHAR(o.endret_tidspunkt, 'YYYY-MM-DD') AS dato_endret
    , COUNT(*)                              AS Count
    , MIN(TO_CHAR(o.opprettet_tidspunkt, 'YYYY-MM-DD HH24:MI:SS')) AS earliest_insertion
    , MAX(TO_CHAR(o.opprettet_tidspunkt, 'YYYY-MM-DD HH24:MI:SS')) AS latest_insertion
    , MIN(TO_CHAR(o.endret_tidspunkt   , 'YYYY-MM-DD HH24:MI:SS')) AS earliest_update
    , MAX(TO_CHAR(o.endret_tidspunkt   , 'YYYY-MM-DD HH24:MI:SS')) AS latest_update
    , CAST(COLLECT(DISTINCT o.oppgavetype ) AS strings) AS oppgave_typer
    , CAST(COLLECT(DISTINCT o.opprettet_av) AS strings) AS opprettet_av
    , CAST(COLLECT(DISTINCT o.endret_av   ) AS strings) AS endret_av
    , CAST(COLLECT(DISTINCT o.versjon     ) AS numbers) AS versjoner
    FROM oppgave_p.oppgave o
    GROUP BY TO_CHAR(o.endret_tidspunkt, 'YYYY-MM-DD')
    ORDER BY 1 DESC
    /
    DROP TYPE strings;
    /
    DROP TYPE numbers;
```
# Spesifikke Kubernetes-behov i preprod/prod
## Start en batch akkurat nå uavhengig av hva cron schedule tilsier

- Preprod

```
kubectl config use-context preprod-fss
kubectl config set-context preprod-fss --namespace="oppgavehandtering"
kubectl create job --from=cronjob/okosynkos "<okosynk-os-oor-manually-started-2021-07-05-12-21>"
kubectl create job --from=cronjob/okosynkur "<okosynk-ur-oor-manually-started-2021-07-05-12-21>"
```

- Prod

```
kubectl config use-context prod-fss
kubectl config set-context prod-fss --namespace="oppgavehandtering"
kubectl create job --from=cronjob/okosynkos "<okosynk-os-oor-manually-started-2021-07-05-12-21>"
kubectl create job --from=cronjob/okosynkur "<okosynk-ur-oor-manually-started-2021-07-05-12-21>"
```

## Slett en jobb

```kubectl delete job oor-manually-started-2020-01-10-18-18```

## Slett en cronjob slik at det ikke dannes nye batch-jobber til konfigurert tid

```kubectl delete cronjob okosynkos```

eller

```kubectl delete cronjob okosynkur```

## "Ping" PDl preprod

```curl -v -X OPTIONS  https://pdl-api.dev.intern.nav.no/graphql```

# General practical commands, hints and tips

- Which ports are being listened to (e.g. to see whether the SFTP server is running and listening to the expected port)
    - ```sudo lsof -i -P | grep -i "listen"``` (MAC)
    - ```netstat -an -ptcp | grep LISTEN``` (MAC)
- En flatfil kan kjøres flere ganger. En oppgave vil oppdateres tilsvarende endringene i flatfilen hver gang, men kun
  ferdigstilles hvis det har gått lengre tid enn 8 timer siden sist oppgaven ble endret.
- Ved lokal utvikling benyttes `src/test/resources/application-test.testset_001.properties` for properties som vanligvis
  vil ligge i yaml/Kubernetes.
- OS - service user srvbokosynk001
- UR - service user srvbokosynk002
- Kjør `java -jar target/okosynk.jar -h` for å se hvilke kommandolinjeparametre som er tilgjengelige
- NB! testdatafilene inneholder binære verdier, så de må editeres med omhu! (F.eks. kan OS-testdatafila inneholde et 
  tegn som fortoner seg som en 'æ', men som _IKKE_ er det, det er derimot en HEX E6.)
- Can some dependencies be updated?
    - `mvn versions:display-dependency-updates`
- Sjekk innholdet i en pod:
    - `kubectl describe jobs okosynk-oor-manually-started-2021-06-30-12-16-ctm66`