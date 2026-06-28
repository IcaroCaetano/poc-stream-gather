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

Gatherers.windowFixed() OK
Gatherers.windowSliding()
Gatherers.fold()
Criar um Gatherer customizado
Entender Initializer, Integrator, Combiner e Finisher

### Gatherers.windowFixed() 

Criar janelas (grupos) de tamanho fixo dentro de um Stream.

Em vez de processar um elemento por vez, ele agrupa os elementos em listas de tamanho definido.

#### Exemplo simples

```
var numeros = List.of(1, 2, 3, 4, 5, 6, 7);

numeros.stream()

       .gather(Gatherers.windowFixed(3))

       .forEach(System.out::println);
```

Saida:

```
[1, 2, 3]
[4, 5, 6]
[7]
````

Saida Billing Service:

```
Enviando lote
Order[id=1, customer=João, amount=150.0]
Order[id=2, customer=Maria, amount=220.0]
Order[id=3, customer=José, amount=300.0]
----------------
Enviando lote
Order[id=4, customer=Pedro, amount=180.0]
Order[id=5, customer=Ana, amount=90.0]
Order[id=6, customer=Lucas, amount=450.0]
----------------
Enviando lote
Order[id=7, customer=Carla, amount=120.0]
----------------

Process finished with exit code 0
`````

### Então por que windowFixed() existe?

Porque a equipe do Java percebeu que alguns padrões são extremamente comuns.
Eles já entregaram algumas implementações prontas.
Eles usaram o Gather para criar uma operacao comum que já é bem usada. Por essa razão serve
de exemplo para aplicação do Gather.



### Depois do Stream Gatherers

A linguagem passou a dizer:
"Além das ferramentas prontas, agora você pode construir as suas próprias ferramentas e encaixá-las no pipeline do Stream."

```
filter

detectDuplicates

rateLimiter

batchProcessor

windowFixed

windowSliding

map
```

## Criando um Gather proprio

### Primeiro, vamos entender a estrutura de um Gatherer

Um Gatherer é composto por quatro partes:

````
                Stream
                   │
                   ▼
           Initializer
                   │
                   ▼
     Integrator (para cada elemento)
                   │
                   ▼
            Combiner (paralelo)
                   │
                   ▼
             Finisher
                   │
                   ▼
          Próxima etapa do Stream
````


### Vamos criar um Gatherer simples

Imagine que queremos uma nova operação no Stream chamada:

````
.repeat(3)
````

Como ela não existe, podemos implementá-la com um Gatherer.

Entrada:

````
Java
25
````

Saída:

````
Java
Java
Java
25
25
25
````

### Decompondo 

Isso declara que o método é genérico.
Ou seja, ele funciona para qualquer tipo.


```
public static <T>
````

Por exemplo:

```
repeat(3)
```