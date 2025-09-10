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
}