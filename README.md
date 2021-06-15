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

## Lokal testing
1. Kopiér `web/src/test/resources/environment-test.properties.default` til `environment-test.properties` og editér innholdet i henhold til hva som ønskes testet.
0. Pakk ut `domain/src/test/resources/os.input.valid.per.2018-05-04.zip` i samme directory. Hvorvidt navnet på fila da er korrekt, avhenger av no.nav.okosynk.OkosynkDomainConstants.OS_FS_INPUT_FILE_NAME i domain-modulen, evt. filnavnet som implisitt er angitt i `osFtpBaseUrl.url`-property'en hvis man skal teste FTP.
0. Pakk ut `domain/src/test/resources/ur.input.valid.per.2018-05-04.zip` i samme directory. Hvorvidt navnet på fila da er korrekt, avhenger av no.nav.okosynk.OkosynkDomainConstants.UR_FS_INPUT_FILE_NAME i domain-modulen, evt. filnavnet som implisitt er angitt i `urFtpBaseUrl.url`-property'en hvis man skal teste FTP.
0. Kjør java -jar app.jar
5. Resultatet kan kontrolleres...

    1. ... ved å se på loggene. Se særlig etter strengen `STATISTIKK`. Loggen konfigureres i `okosynk/web/src/test/resources/logback-test.xml`.
    0. ... ved å kjøre SQL mot oppgave-databasen  i det riktige `t`-miljøet, f.eks. `t4`. DataSource finner du i app-preprod.yaml eller app-prod.yaml
    1. Nyttge SQL-statements:   
            
            ```
            SELECT COUNT(*)
                FROM t_oppgave o
                WHERE o.k_fagomrade = 'OKO'
                    AND o.opprettet_av = 'srvokosynk'
                    AND o.k_oppgave_t = 'OKO_UR';
            ```
            
            ```
           SELECT e.NUMMER, o.K_UNDERKATEGORI, COUNT(*) AS antall
           FROM T_OPPGAVE o, T_ORG_ENHET e
           WHERE
               o.K_OPPGAVE_T = 'OKO_OS'
               AND o.ENDRET_AV = 'srvokosynk'
               AND o.DATO_ENDRET >= to_timestamp('2017-09-20 0800','YYYY-MM-DD HH24MI')
               AND o.ORG_ENHET_ID_ANSV = e.ORG_ENHET_ID
           GROUP BY e.nummer, o.K_UNDERKATEGORI;
        
       ```
        
       ```
           CREATE TYPE strings AS TABLE OF VARCHAR2(40);
           /
           CREATE TYPE numbers AS TABLE OF NUMBER(10);
           /
           SELECT
               TO_CHAR(dato_endret   , 'YYYY-MM-DD') AS Dato_endret
             , COUNT(*)                              AS Count
             , MIN(TO_CHAR(dato_opprettet, 'YYYY-MM-DD HH24:MI:SS')) AS Earliest_insertion
             , MAX(TO_CHAR(dato_opprettet, 'YYYY-MM-DD HH24:MI:SS')) AS Latest_insertion
             , MIN(TO_CHAR(dato_endret   , 'YYYY-MM-DD HH24:MI:SS')) AS Earliest_update
             , MAX(TO_CHAR(dato_endret   , 'YYYY-MM-DD HH24:MI:SS')) AS Latest_update
             , CAST(COLLECT(DISTINCT k_oppgave_t ) AS strings) AS oppgave_typer
             , CAST(COLLECT(DISTINCT opprettet_av) AS strings) AS opprettet_av
             , CAST(COLLECT(DISTINCT endret_av   ) AS strings) AS endret_av
             , CAST(COLLECT(DISTINCT versjon     ) AS numbers) AS versjoner
           FROM     t_oppgave
           GROUP BY TO_CHAR(dato_endret, 'YYYY-MM-DD')
           ORDER BY 1 DESC
           /
           DROP TYPE strings;
           /
           DROP TYPE numbers;
    
           ```
            
           ```
           CREATE TYPE strings AS TABLE OF VARCHAR2(40);
           /
           CREATE TYPE numbers AS TABLE OF NUMBER(10);
           /
           SELECT    TO_CHAR(dato_opprettet, 'YYYY-MM-DD') AS Dato_opprettet
                   , TO_CHAR(dato_endret, 'YYYY-MM-DD') AS Dato_endret
                   , COUNT(*) AS Count
                   , MIN(TO_CHAR(dato_opprettet, 'YYYY-MM-DD HH24:MI:SS')) AS Earliest_insertion
                   , MAX(TO_CHAR(dato_opprettet, 'YYYY-MM-DD HH24:MI:SS')) AS Latest_insertion
                   , MIN(TO_CHAR(dato_endret   , 'YYYY-MM-DD HH24:MI:SS')) AS Earliest_update
                   , MAX(TO_CHAR(dato_endret   , 'YYYY-MM-DD HH24:MI:SS')) AS Latest_update
                   , CAST(COLLECT(DISTINCT k_oppgave_t ) AS strings) AS oppgave_typer
                   , CAST(COLLECT(DISTINCT opprettet_av) AS strings) AS opprettet_av
                   , CAST(COLLECT(DISTINCT versjon     ) AS numbers) AS versjoner
           FROM     t_oppgave
           GROUP BY TO_CHAR(dato_opprettet, 'YYYY-MM-DD'), TO_CHAR(dato_endret, 'YYYY-MM-DD')
           ORDER BY 1 DESC, 2 DESC
           /
           DROP TYPE strings;
           /
           DROP TYPE numbers;
            
           ```
            
           ```

0. Noter
    1. Okosynk må gå mot oppgave-applikasjonen i et t-miljø, for å ha tilgang til en service gateway som kan oversette sitt
       SAML-token til en LTPA-token.
    0. En flatfil kan kjøres flere ganger. En oppgave vil oppdateres tilsvarende endringene i flatfilen hver gang, men kun ferdigstilles hvis det har gått lengre tid enn 8 timer siden sist oppgaven ble endret.

# Bygg og deployment
Ved innsjekking til master-greina på GitHub bygges og deployeres okosynk implisitt til både preprod og prod.
Dette skjer på GitHub vha. action scriptene
`<PROJECT ROOT>/.github/workflows/deploy-dev-prod.yaml` 
og
`<PROJECT ROOT>/.github/workflows/issue-deploy.yml`.
 Hvis dette ikke er ønskelig, bør man vurdere å arbeide på en egen grein.

## Sjekk hvordan det står til i drift

```
kubectl config use-context "<riktig cluster>" (enten "preprod-fss" eller "prod-fss")
kubectl config set-context "<riktig cluster>" --namespace="<riktig namespace>" (<riktig namespace> er "oppgavehandtering" uavhengig av om det er preprod eller prod)
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

`kubectl get jobs`

og da får man opp noe som ligner på det her:

```
NAME                 DESIRED   SUCCESSFUL   AGE
okosynk-1536469200   1         1            2d
okosynk-1536555600   1         1            1d
okosynk-1536642000   1         1            4h
```

En cronjob i Kubernetes vil opprette en ny `job` for hver kjøring. Standard oppførsel
er at disse jobbene blir liggende igjen etter at de er ferdige, slik at man kan lese ut
logger osv. De blir automatisk slettet etter en tid. (Man kan konfigurere og fine-tune
når de skal slettes, men Kubernetes har en fin default.)

Hvis det ligger veldig mange jobber inne i clusteret, kan man f.eks. kjøre

`kubectl get jobs | grep okosynk`

for å kun få okosynk-jobbene. (Det er usikkert hvordan
`grep` vil fungere fra et Windows-image. Antakeligvis har kubectl et kommandolinjeflagg
for å filtrere jobber.)

I lista over jobber kan man se når alle jobbene sist kjørte, og antall forsøk de trengte
for å være successful - i dette tilfellet var det enkelt, alle jobbene kjørte fint på
første forsøk.

### Hvordan lese logger fra kjøringene

Forhåpentligvis vil loggene ende opp i Kibana (`https://logs.adeo.no`) men man kan også
lese dem direkte fra Kubernetes. Først må man få en liste av pods tilhørende okosynk:

`kubectl get pods`

```
NAME                       READY     STATUS      RESTARTS   AGE
okosynk-1536469200-qz4qq   0/1       Completed   0          2d
okosynk-1536555600-fwg6m   0/1       Completed   0          1d
okosynk-1536642000-j6ccz   0/1       Completed   0          4h
```

Her ser vi at disse podene er "Completed", altså gikk jobben bra. "Age" viser når
hver pod begynte å kjøre. Hvis vi skal sjekke loggene til den siste pod-en, kan vi
kjøre

`kubectl logs okosynk-1536642000-j6ccz`

og da kommer loggene til pod-en opp i terminalen.<BR/>
Og for å finne ut hvorvidt jobbene er vellykka fullførte:

`kubectl logs okosynk-1556078400-gfdhr | grep -i fullført`


Følgene kommando er heller ikke å forakte:<BR/>

`kubectl describe pod okosynk-1536642000-j6ccz`

### Start en batch akkurat nå uavhengig av hva cron schedule tilsier

|                                                              | Preprod              | or   | Prod              |                             | Note |
| :----------------------------------------------------------- | :------------------- | ---- | :---------------- | :-------------------------- | ---- |
| ```kubectl config use-context```                             | ```preprod-fss``` 3) | or   | ```prod-fss``` 3) |                             |      |
| ```kubectl config set-context```                             | ```preprod-fss``` 3) | or   | ```prod-fss``` 3) | ```--namespace="oppgavehandtering"``` |      |
| ```kubectl create job --from=cronjob/okosynk "oor-manually-started-2019-03-11-13-07"``` |                      |      |                   |                             |      |

### Slett en jobb

```kubectl delete job oor-manually-started-2020-01-10-18-18```

### General practical commands
- Which ports are being listened to (e.g. to see whether the SFTP server is running and loistening to the expected port)
<BR/>
```sudo lsof -i -P | grep -i "listen"``` (MAC)
<BR/>
and
<BR/>
```netstat -an -ptcp | grep LISTEN``` (MAC)

### Logging
Hver batch har sin egen logg-fil, i tillegg logger hver batch alle tjenestekall til sensitiv logg.

### Properties
Ved lokal utvikling benyttes `environment-test.properties` for properties som vanligvis vil ligge i yaml/Kubernetes.
