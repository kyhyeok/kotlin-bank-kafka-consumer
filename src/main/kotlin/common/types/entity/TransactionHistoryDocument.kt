package common.types.entity

import kotlinx.serialization.Serializable
import org.bank.common.json.BigDecimalSerializer
import org.bank.common.json.LocalDateTimeSerializer
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
@Document(collection = "transaction_history")
class TransactionHistoryDocument(
    val fromUlid: String,
    val toUlid: String,

    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal,

    @Serializable(with = LocalDateTimeSerializer::class)
    val time: LocalDateTime
)