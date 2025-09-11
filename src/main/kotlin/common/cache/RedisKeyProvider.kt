package org.bank.common.catch

object RedisKeyProvider {
    private const val HISTORY_CATCH_KEY = "history"

    fun historyCatchKey(ulid: String): String {
        return "$HISTORY_CATCH_KEY:$ulid"
    }
}