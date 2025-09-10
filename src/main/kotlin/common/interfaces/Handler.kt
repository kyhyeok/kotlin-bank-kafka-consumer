package common.interfaces

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.support.Acknowledgment

interface Handler {
    fun handle(record: ConsumerRecord<String, Any>, acknowledged: Acknowledgment)

    fun handleDLQ(record: ConsumerRecord<String, Any>, acknowledged: Acknowledgment)
}