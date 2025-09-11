package common.consumer.handler

import common.consumer.handler.repository.HistoryMongoRepository
import common.interfaces.Handler
import common.jsonutil.JsonUtil
import common.types.dto.History
import common.types.message.TransactionMessage
import kotlinx.serialization.builtins.ListSerializer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.bank.common.catch.RedisClient
import org.bank.common.catch.RedisKeyProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.ComponentScan
import org.springframework.kafka.support.Acknowledgment

@ComponentScan
class BankTransactionHandler(
    private val logger: Logger = LoggerFactory.getLogger(BankTransactionHandler::class.java),
    private val historyMongoRepository: HistoryMongoRepository,
    private val redisClient: RedisClient
) : Handler {
    override fun handle(
        record: ConsumerRecord<String, Any>,
        acknowledged: Acknowledgment?
    ) {
        acknowledged?.let { act ->
            {
                val rawMessage = record.value()
                logger.info("Received message: {}", rawMessage)

                try {
                    val messageString = when (rawMessage) {
                        is String -> rawMessage
                        else -> rawMessage.toString()
                    }

                    val data = JsonUtil.decodeFromJson(messageString, TransactionMessage.serializer())

                    val entity = data.toEntity()
                    historyMongoRepository.saveTransactionHistory(entity)

                    handleRedisData(data.fromUlid, data.toDto())
                    handleRedisData(data.toUlid, data.toDto())

                    act.acknowledge()

                    logger.info("message processed: {}", rawMessage)
                } catch (e: Exception) {
                    logger.error("message processing message: {}", e.message)
                }
            }
        }
    }

    fun handleRedisData(ulid: String, dto: History) {
        val cacheKey = RedisKeyProvider.historyCatchKey(ulid)
        val cachedValue = redisClient.get(cacheKey)

        if (cachedValue != null) {
            val cachedData: List<History> =
                JsonUtil.decodeFromJson(cachedValue, ListSerializer(History.serializer()))

            val updatedHistoryList = if (cachedData.size >= 30) {
                val mutableList = cachedData.toMutableList()
                mutableList.removeAt(mutableList.size - 1)
                mutableList.add(0, dto)
                mutableList
            } else {
                val mutableList = cachedData.toMutableList()
                mutableList.add(0, dto)
                mutableList
            }

            val updatedData = JsonUtil.encodeToJson(updatedHistoryList, ListSerializer(History.serializer()))

            redisClient.set(cacheKey, updatedData)
        }

    }

    override fun handleDLQ(
        record: ConsumerRecord<String, Any>,
        acknowledged: Acknowledgment?
    ) {
        TODO("Not yet implemented")
    }

}