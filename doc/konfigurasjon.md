# Konfigurasjon av Okosynk
Konfigurasjon er satt opp ved at man oppretter en singleton av klassen OkosynkConfiguration

```mermaid
classDiagram
    CliMain --|> OkosynkConfiguration
    OkosynkConfiguration : addVaultProperties()
    
    OkosynkConfiguration : getString()
    OkosynkConfiguration : System configuration    
    OkosynkConfiguration --|> SystemConfiguration    
    OkosynkConfiguration --|> CompositeConfiguration    
    OkosynkConfiguration --|> EnvironmentConfiguration    
    OkosynkConfiguration --|> Vault
    
    CompositeConfiguration ..|> file    
    SystemConfiguration ..|> Systemenv
    EnvironmentConfiguration ..|> Naiserator
        
    Vault : kv/preprod/fss/okosynkos/sftpcredentials
    Vault : username
    Vault : private key
    Vault : kv/preprod/fss/okosynkos/oppgavecredentials
    Vault : username
    Vault : password
    
    CliMain : createOkosynkConfiguration()
    Naiserator : FTPBASEURL_URL
    Naiserator : OPPGAVE_URL
    Naiserator : PDL_URL
    Naiserator : SHOULD_RUN_OS_OR_UR
    Systemenv : NAIS_APP_NAME
    file : okosynk.configuration
