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
