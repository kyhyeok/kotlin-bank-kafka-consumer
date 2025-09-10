package common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "kafka")
data class TopicConfig(
    var topics: Map<String, TopicProperties> = emptyMap(),
    var info: KafkaInfo = KafkaInfo()
)

data class TopicProperties(
    var pollingInterval: Long = 5000, // ms
    var concurrency: Int = 1,
    var maxPollRecords: Int = 100,
    var enabled: Boolean = true
)

data class KafkaInfo(
    var bootstrapServers: String = "",
    var consumer: ConsumerConfig = ConsumerConfig()
)

data class ConsumerConfig (
    var groupId: String = "my-consumer-group",
    var autoOffsetReset: String = "earliest",
    var autoCommit: Boolean = false,
)