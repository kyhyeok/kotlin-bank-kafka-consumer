package common.config


import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import common.interfaces.Handler
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.bank.common.exception.CustomException
import org.bank.common.exception.ErrorCode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.AcknowledgingMessageListener
import org.springframework.kafka.listener.ContainerProperties

@Configuration
@EnableKafka
class KafkaConsumerConfig(
    private val topicConfig: TopicConfig,
    private val logger: Logger = LoggerFactory.getLogger(KafkaConsumerConfig::class.java)
) {
    @Bean
    fun consumerConfigs(): Map<String, Any> {
        val props: MutableMap<String, Any> = HashMap()

        // 고정값
        props[ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG] = "500" // 연결이 시패하는 경우에 대해서 재연결 시도 간격 ms
        props[ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG] = "5000" // 최대 재연결 간격 ms
        props[ConsumerConfig.RETRY_BACKOFF_MS_CONFIG] = "500" // 실패한 요청에 대해서 재시도 연결 ms

        props[ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG] = "30000" // 컨슈머와 브로거와의 세션을 유지하는 최대 시간
        props[ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG] = "3000" // healthcheck 전송 간격

        // config value
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = topicConfig.info.bootstrapServers // 연결하고자 하는 서버 url
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] =
            topicConfig.info.consumer.autoOffsetReset // 초기 로프셋 위치 설정 (earliest, latest, none)중 보통 earliest를 많이 적용
        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = topicConfig.info.consumer.autoCommit // auto commit
        props[ConsumerConfig.GROUP_ID_CONFIG] = topicConfig.info.consumer.groupId

        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserialize::class.java

        return props
    }

    @Bean(name = ["factoryHandlerMapper"])
    fun factoryHandlerMapper(): Map<String, ConcurrentKafkaListenerContainerFactory<String, Any>> {
        val factoryMap = HashMap<String, ConcurrentKafkaListenerContainerFactory<String, Any>>()

        topicConfig.topics.forEach { (name, properties) ->
            if (properties.enabled) {
                val handler: Handler

                when (name) {
                    "transactions" -> handler =
                    // 필요한 경우에 따라 핸들러 매핑
                    else -> {
                        throw CustomException(ErrorCode.FAILED_TO_FIND_TOPIC_HANDLER)
                    }
                }

                factoryMap[name] = createKafkaListenerContainerFactory(name, handler, properties)
            }

            return factoryMap

        }


        private fun createKafkaListenerContainerFactory(
            topicName: String,
            handler: Handler,
            properties: TopicProperties,
        ): ConcurrentKafkaListenerContainerFactory<String, Any> {
            val config = consumerConfigs().toMutableMap()
            config[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = properties.maxPollRecords

            val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()

            factory.consumerFactory = DefaultKafkaConsumerFactory<String, Any>(config)
            factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
            factory.setAutoStartup(true)
            factory.containerProperties.pollTimeout = properties.pollingInterval

            val container = factory.createContainer(topicName)

            container.setupMessageListener(AcknowledgingMessageListener { record, acknowledgement ->
                try {
                    handler.handle(record, acknowledgement)
                } catch (e: Exception) {
                    handler.handleDLQ(record, acknowledgement)
                }
            })

            return factory
        }
    }