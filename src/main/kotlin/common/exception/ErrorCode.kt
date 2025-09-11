package org.bank.common.exception

interface CodeInterface {
    val code: Int
    var message: String
}

enum class ErrorCode(
    override val code: Int,
    override var message: String
) : CodeInterface {
    FAILED_TO_CONNECT_MONGO(-100, "failed to connect mongo"),
    FAILED_TO_FIND_TOPIC_HANDLER(-101, "failed to find topic handler"),
    FAILED_TO_FIND_MONGO_TEMPLATE(-102, "failed to find mongo template"),
}