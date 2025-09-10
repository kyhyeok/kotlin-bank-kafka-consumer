package common.types.message

import common.types.dto.History
import common.types.entity.TransactionHistoryDocument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bank.common.json.BigDecimalSerializer
import org.bank.common.json.LocalDateTimeSerializer
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class TransactionMessage(
    @SerialName("fromUlid")
    val fromUlid: String,

    @SerialName("fromName")
    val fromName: String,

    @SerialName("fromAccountUlid")
    val fromAccountUlid: String,

    @SerialName("toUlid")
    val toUlid: String,

    @SerialName("toName")
    val toName: String,

    @SerialName("toAccountUlid")
    val toAccountUlid: String,

    @SerialName("value")
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal,

    @SerialName("time")
    @Serializable(with = LocalDateTimeSerializer::class)
    var time: LocalDateTime = LocalDateTime.now()
) {
    fun toEntity(): TransactionHistoryDocument {
        return TransactionHistoryDocument(
            fromUlid = fromUlid,
            toUlid = toUlid,
            value = value,
            time = time
        )
    }

    fun toDto(fromUser: String, toUser: String): History {
        return History(
            fromUlid = fromUlid,
            fromUser = fromUser,
            toUlid = toUser,
            toUser = toUser,
            value = value,
            time = time
        )
    }
}