# poc-stream-gather

## Primeiro: o que é Stream Gatherers?

O objetivo é permitir a criação de operações intermediárias customizadas para Streams.

Antes, o Stream possuía operações prontas:

```
map()
filter()
flatMap()
sorted()
distinct()
limit()
````

Mas quando precisávamos de algo mais complexo, éramos obrigados a:

- criar um Collector
- armazenar estado externo
- utilizar loops imperativos

Os Gatherers permitem criar novas operações intermediárias stateful.


## A nova API:

