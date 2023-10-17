# eCommerce

Projeto de estudo Apache Kafka. Curso da Alura. [Kafka: Produtores, consumidores e streams](https://cursos.alura.com.br/course/kafka-introducao-a-streams-em-microservicos)

## Executando a aplicação.

O projeto conta com um `docker-compose` que sobe uma instância do **zookeeper** e do **kafka**.

Faça o comando `docker-compose up -d` para subir os containers

```shell
Creating network "ecommerce_default" with the default driver
Creating ecommerce_zookeeper_1 ... done
Creating ecommerce_kafka_1     ... done
```

Depois faça o comando `docker-compose ps` para checkar se os serviços subiram corretamente:

```shell
        Name                     Command            State                              Ports                            
------------------------------------------------------------------------------------------------------------------------
ecommerce_kafka_1       /etc/confluent/docker/run   Up      0.0.0.0:9092->9092/tcp,:::9092->9092/tcp                    
ecommerce_zookeeper_1   /etc/confluent/docker/run   Up      0.0.0.0:2181->2181/tcp,:::2181->2181/tcp, 2888/tcp, 3888/tcp
```

### Startando o serviço http

No módulo `service-http-ecommerce` start a classe **HttpEcommerceService** ela subira o servidor Jetty para receber as requisições de novas ordens de compra.

Saída esperada
```shell
[main] INFO org.eclipse.jetty.util.log - Logging initialized @425ms to org.eclipse.jetty.util.log.Slf4jLog
[main] INFO org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values: 
	acks = 1
	batch.size = 16384
	bootstrap.servers = [127.0.0.1:9092]
	buffer.memory = 33554432
	client.dns.lookup = default
	...
	[main] INFO org.eclipse.jetty.server.Server - jetty-9.4.23.v20191118; built: 2019-11-18T19:22:48.413Z; git: abbccc65d6cf5e8806dd35881147d618b9b5740b; jvm 17.0.8.1+8-LTS
[main] INFO org.eclipse.jetty.server.handler.ContextHandler - Started o.e.j.s.ServletContextHandler@3febb011{/,null,AVAILABLE}
[main] INFO org.eclipse.jetty.server.AbstractConnector - Started ServerConnector@6842775d{HTTP/1.1,[http/1.1]}{0.0.0.0:8080}
[main] INFO org.eclipse.jetty.server.Server - Started @1175ms
[kafka-producer-network-thread | producer-2] INFO org.apache.kafka.clients.Metadata - [Producer clientId=producer-2] Cluster ID: 4yWLUqp_Tw6LqgH9lfA9JA
[kafka-producer-network-thread | producer-1] INFO org.apache.kafka.clients.Metadata - [Producer clientId=producer-1] Cluster ID: 4yWLUqp_Tw6LqgH9lfA9JA
	
```
Agora realize uma requisição de compra pelo seu browser chamando o endereço
`http://localhost:8080/new?email="email@email.com"&ammount=110`

Você terá as seguintes respostas do serviço HTTP e do FraudDetector, respectivamente:

```shell
sucesso enviando... ECOMMERCE_NEW_ORDER:::partition 0/ offset 80/ timestamp 1697482760996
sucesso enviando... ECOMMERCE_SEND_EMAIL:::partition 0/ offset 80/ timestamp 1697482761033
New order sent successfully.
```

```shell
Processing new order, checking for fraud
"email@email.com"
Order{, orderId='3ae491db-252b-4235-96c1-b93a2a769faa', amount=110.00}
0
80
Approved: Order{, orderId='3ae491db-252b-4235-96c1-b93a2a769faa', amount=110.00}
sucesso enviando... ECOMMERCE_ORDER_APPROVED:::partition 0/ offset 50/ timestamp 1697482766018

```
### Listando os tópicos

Você pode utilizar o seguinte programa [Kafkatool](https://kafkatool.com/download.html) para visualizar os tópicos criados junto ao kafka server.

![img kafkatool](docs/imagens/1-offset-explorer.png)

Comandos do kafka

`/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic <MEU-TOPIC> --from-beginning`

Descrevendo os tópicos
`/bin/kafka-topics --describe --topic ECOMMERCE_NEW_ORDER --zookeeper localhost:22181`

`docker exec -it ecommerce_kafka_1 /bin/kafka-topics --bootstrap-server localhost:9092 --describe`

Saída esperada do comando
```shell
plicationFactor: 1	Configs: 
	Topic: ECOMMERCE_NEW_ORDER	Partition: 0	Leader: 1	Replicas: 1	Isr: 1
Topic: __consumer_offsets	TopicId: GXJxrv2hQUy5Nb_ZYjXdFA	PartitionCount: 50	ReplicationFactor: 1	Configs: compression.type=producer,cleanup.policy=compact,segment.bytes=104857600
	Topic: __consumer_offsets	Partition: 0	Leader: 1	Replicas: 1	Isr: 1
	Topic: __consumer_offsets	Partition: 1	Leader: 1	Replicas: 1	I
```


Alterando um tópico
`docker exec ecommerce_kafka_1 /bin/kafka-topics --alter --bootstrap-server kafka:29092 --topic ECOMMERCE_NEW_ORDER --partitions 3`

Saída 
```shell
[2023-10-06 20:40:01,638] ERROR org.apache.kafka.common.errors.InvalidPartitionsException: Topic already has 3 partitions.

```

Ao executar novamente o comando para descrever um tópico teremos a seguinte saída
`docker exec -it ecommerce_kafka_1 /bin/kafka-topics --bootstrap-server localhost:9092 --describe`

```shell
[estevam@dell-5437 ecommerce]$ docker exec -it ecommerce_kafka_1 /bin/kafka-topics --bootstrap-server localhost:9092 --describe
Topic: ECOMMERCE_NEW_ORDER	TopicId: 2iKS_EWfTCKrahERqrpGtg	PartitionCount: 3	ReplicationFactor: 1	Configs: 
	Topic: ECOMMERCE_NEW_ORDER	Partition: 0	Leader: 1	Replicas: 1	Isr: 1
	Topic: ECOMMERCE_NEW_ORDER	Partition: 1	Leader: 1	Replicas: 1	Isr: 1
	Topic: ECOMMERCE_NEW_ORDER	Partition: 2	Leader: 1	Replicas: 1	Isr: 1
Topic: __consumer_offsets	TopicId: GXJxrv2hQUy5Nb_ZYjXdFA	PartitionCount: 50	ReplicationFactor: 1	Configs: compression.type=producer,cleanup.policy=compact,segment.bytes=104857600

```

Aqui ele mostra o processamento das mensagens pelos tópicos

`docker exec -it ecommerce_kafka_1 /bin/kafka-consumer-groups --all-groups --bootstrap-server localhost:9092 --describe`

```shell
GROUP                 TOPIC               PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                     HOST            CLIENT-ID
FraudeDetectorService ECOMMERCE_NEW_ORDER 0          22              22              0               consumer-1-64ac136e-fdbf-4574-9017-4542ba826e6a /172.19.0.1     consumer-1
FraudeDetectorService ECOMMERCE_NEW_ORDER 1          14              14              0               consumer-1-64ac136e-fdbf-4574-9017-4542ba826e6a /172.19.0.1     consumer-1
FraudeDetectorService ECOMMERCE_NEW_ORDER 2          20              20              0               consumer-1-f59f8ba7-deb6-452b-babf-0dab5607e187 /172.19.0.1     consumer

GROUP                 TOPIC               PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                     HOST            CLIENT-ID
FraudeDetectorService ECOMMERCE_NEW_ORDER 0          22              55              33              consumer-1-64ac136e-fdbf-4574-9017-4542ba826e6a /172.19.0.1     consumer-1
FraudeDetectorService ECOMMERCE_NEW_ORDER 1          14              45              31              consumer-1-64ac136e-fdbf-4574-9017-4542ba826e6a /172.19.0.1     consumer-1
FraudeDetectorService ECOMMERCE_NEW_ORDER 2          20              56              36              consumer-1-f59f8ba7-deb6-452b-babf-0dab5607e187 /172.19.0.1     consumer

```

Apagar um tópicos
`docker exec -it ecommerce_kafka-2_1 /bin/kafka-topics --delete --bootstrap-server localhost:9093 --topic ECOMMERCE_.*`
