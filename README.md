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

### Gatherers.windowFixed() 

cria janelas (grupos) de tamanho fixo dentro de um Stream.

Em vez de processar um elemento por vez, ele agrupa os elementos em listas de tamanho definido.

#### Exemplo simples

```java
var numeros = List.of(1, 2, 3, 4, 5, 6, 7);

numeros.stream()

       .gather(Gatherers.windowFixed(3))

       .forEach(System.out::println);
```