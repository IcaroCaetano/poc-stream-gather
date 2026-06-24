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

````
stream.gather(...)
````

Fica entre as etapas do pipeline.

````
stream
   .filter(...)
   .gather(...)
   .map(...)
````

Ordem de aprendizado que eu seguiria

Gatherers.windowFixed()
Gatherers.windowSliding()
Gatherers.fold()
Criar um Gatherer customizado
Entender Initializer, Integrator, Combiner e Finisher

## Gatherers.windowFixed() cria janelas (grupos) de tamanho fixo dentro de um Stream.