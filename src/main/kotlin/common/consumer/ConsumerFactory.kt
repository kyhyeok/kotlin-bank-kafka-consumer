package common.consumer

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.stereotype.Component

@Component
class ConsumerFactory(
    @Qualifier("factoryHandlerMapper") private val factoryHandlerMapper: Map<String, ConcurrentKafkaListenerContainerFactory<String, Any>>,
    private val logger: Logger = LoggerFactory.getLogger(ConsumerFactory::class.java)
) {
    @EventListener(ApplicationReadyEvent::class)
    fun startConsumers() {
        factoryHandlerMapper.forEach { (topicName, factory) ->
            try {
            factory.createContainer(topicName).start()
                logger.info("Started consumer for topic $topicName")
            } catch (e: Exception) {
                logger.info("Failed to start consumer for topic $topicName", e.message)
            }
        }
    }
}