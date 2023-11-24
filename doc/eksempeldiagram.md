```mermaid
graph TB
    sq[Square shape] --> ci((Circle shape))

    subgraph A
        od>Odd shape]-- Two line<br/>edge comment --> ro
        di{Diamond with <br/> line break} -.-> ro(Rounded<br>square<br>shape)
        di==>ro2(Rounded square shape)
    end

%% Notice that no text in shape are added here instead that is appended further down
    e --> od3>Really long text with linebreak<br>in an Odd shape]

%% Comments after double percent signs
    e((Inner / circle<br>and some odd <br>special characters)) --> f(,.?!+-*ز)

    cyr[Cyrillic]-->cyr2((Circle shape Начало));

    classDef green fill:#4b2,stroke:#333,stroke-width:2px;
    classDef orange fill:#c63,stroke:#333,stroke-width:4px;
    class sq,e green
    class di orange
```

```mermaid
classDiagram
    CliMain <|-- AveryLongClass : Cool
    CliMain : int chimp
    CliMain : main()
    CliMain --> Interface01 : Down
    Class03 *-- Class04
    Class05 o-- Class06
    Class07 .. Class08
    Class09 --> C2 : Where am i?
    Class09 --* C3
    Class09 --|> Class07
    Class07 : equals()
    Class07 : Object[] elementData
    AveryLongClass : someMethod()
    Interface01 <|.. AveryLongClass
    Interface01 : size()
    Interface01 : int chimp
