package common.consumer.handler

import common.consumer.handler.repository.HistoryMongoRepository
import common.interfaces.Handler
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.bank.common.catch.RedisClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.ComponentScan
import org.springframework.kafka.support.Acknowledgment

@ComponentScan
class BankTransactionHandler(
    private val logger: Logger = LoggerFactory.getLogger(BankTransactionHandler::class.java),
    private val historyMongoRepository: HistoryMongoRepository,
    private val redisClient: RedisClient
): Handler {
    override fun handle(
        record: ConsumerRecord<String, Any>,
        acknowledged: Acknowledgment
    ) {
        TODO("Not yet implemented")
    }

    override fun handleDLQ(
        record: ConsumerRecord<String, Any>,
        acknowledged: Acknowledgment
    ) {
        TODO("Not yet implemented")
    }

}