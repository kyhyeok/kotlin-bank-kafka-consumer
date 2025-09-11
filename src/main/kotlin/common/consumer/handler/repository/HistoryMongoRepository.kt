package common.consumer.handler.repository

import common.config.MongoTableCollector
import common.types.entity.TransactionHistoryDocument
import org.bank.common.exception.CustomException
import org.bank.common.exception.ErrorCode
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class HistoryMongoRepository(
    private val template: HashMap<String, MongoTemplate>
) {

    fun saveTransactionHistory(event: TransactionHistoryDocument) {
        val template = template(MongoTableCollector.BANK)
        template.save(event)
    }

    private fun template(c: MongoTableCollector): MongoTemplate {
        val table = template[c.table]

        table?.let { return it }

        throw CustomException(ErrorCode.FAILED_TO_FIND_MONGO_TEMPLATE)
    }
}