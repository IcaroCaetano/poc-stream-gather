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

```
public static <T> Gatherer<T, Void, T> repeat(int times) {
```


#### 1.

```
public static <T>
````

Isso declara que o método é genérico.
Ou seja, ele funciona para qualquer tipo.


Por exemplo:

```
repeat(3)
```

pode ser usado em um:

```
Stream<String>
```

ou

```
Stream<Integer>
```

ou 

```
Stream<Order>
```

ou seja:

```
T = Order
```


Ou seja, o compilador descobre automaticamente qual é o tipo.

#### Visualizando

Nosso Gatherer:

```
Gatherer<T, Void, T>
```

significa:

```
Recebe um T

↓

Não guarda estado

↓

Produz um T
```

Outro exemplo:

```
Gatherer<Order, List<Order>, Invoice>
```

Seria:

```
Recebe Order

↓

Guarda uma List<Order>

↓

Produz Invoice
```

### Por que existe esse parâmetro de estado?

Imagine que você queira criar um Gatherer que agrupe 3 elementos.

Entrada:

````
1
2
3
4
5
6
````

Quando chega o primeiro elemento:

````
Estado:

[1]
````
Segundo:

````
Estado:

[1,2]
````

Terceiro:

````
Estado:

[1,2,3]
````

Agora ele pode emitir:

````
[1,2,3]
````

Sem um estado interno, isso seria impossível.

Gatherer<T, Void, T>:

### Resumindo:

- primeiro T: tipo de entrada do Stream;

- Void: não há estado interno;

- segundo T: tipo de saída do Gatherer.


### Gatherer.ofSequential(

Ela é a responsável por "montar" o Gatherer e introduz os conceitos de Initializer, Integrator, Combiner e Finisher.

"Crie um Gatherer que será executado de forma sequencial."

Ou seja, ele informa à API de Streams que esse Gatherer não precisa se preocupar com processamento paralelo.

#### O que acontece internamente?

Quando você escreve:

````
Gatherer.ofSequential(
    initializer,
    integrator
);
````
é como se estivesse dizendo:

````
Crie um Gatherer composto por:

✓ Initializer
✓ Integrator

Não preciso de Combiner.
Não preciso lidar com execução paralela.
````
#### Comparando com Gatherer.of()


### Gatherer.Downstream<? super T> downstream 
é o mecanismo pelo qual um Gatherer produz resultados para a próxima etapa do Stream. Com ele você pode:

- Enviar um elemento (downstream.push(element)).
- Enviar vários elementos (chamando push() várias vezes).
- Não enviar nenhum elemento (não chamando push()).
- Verificar se o pipeline ainda deseja receber elementos pelo valor booleano retornado por push().
- Encerrar o processamento antecipadamente retornando false do integrador.

É por isso que o Downstream é considerado o componente central de um Gatherer: ele define como os elementos 
transformados fluem para o restante da pipeline do Stream.

```
Stream
   │
   ▼
Gatherer
   │
   ▼
map()
   │
   ▼
filter()
   │
   ▼
forEach()
```

### O método push()

O método mais importante é:

````
downstream.push(valor);
````

#### Ele envia um elemento para o próximo estágio do Stream.

Exemplo:

````
downstream.push("Java");
````

É equivalente a dizer:

"Próximo estágio, aqui está um novo elemento."

#### Você pode enviar vários elementos

````
downstream.push(element);
downstream.push(element.toUpperCase());
downstream.push(element + "!");
````

#### Ou nenhum elemento

Você simplesmente não chama push().

````
if (element.length() > 3) {
    downstream.push(element);
}
````

#### O retorno de push()

Pouca gente percebe isso.

push() retorna um boolean:

````
boolean continuar = downstream.push(element);
````

Esse retorno informa se o próximo estágio ainda deseja receber elementos.

Exemplo:

````
if (!downstream.push(element)) {
    return false;
}
````

Se retornar false, significa que alguém interrompeu o processamento (por exemplo, uma operação 
terminal como findFirst() já encontrou o resultado e não precisa de mais elementos).

### O return true do Integrator

Observe este código:

````
(state, element, downstream) -> {

    downstream.push(element);

    return true;
}
````

O true indica:

Continue processando os próximos elementos do Stream.

Já:

``return false;``

significa:
````Pare o Gatherer agora.````

#### O que significa <? super T>?

É um uso de contravariância em generics.

````
Gatherer.Downstream<? super T>
````

Significa que o downstream aceita:

- T
- qualquer supertipo de T

- Exemplo:
- 
````
T = Integer
````

O downstream pode ser:

````
Downstream<Integer>
Downstream<Number>
Downstream<Object>
````

Assim você pode fazer:

````
downstream.push(10);
````

independentemente de ele estar preparado para receber Integer, Number ou Object.