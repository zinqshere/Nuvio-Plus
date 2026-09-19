package com.nuvio.app.core.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthRepositoryErrorTest {
    @Test
    fun authenticationErrorMessagePreservesUsefulCauseText() {
        val error = IllegalStateException("Invalid login credentials")
        val message = error.safeTestAuthErrorDescription()

        assertEquals("Invalid login credentials", message)
    }

    @Test
    fun authenticationErrorMessageDoesNotReturnBlankText() {
        val error = IllegalStateException("   ")
        val message = error.safeTestAuthErrorDescription()

        assertTrue(message == null)
    }

    private fun Throwable.safeTestAuthErrorDescription(): String? =
        generateSequence(this) { it.cause }
            .mapNotNull { it.message?.trim() }
            .firstOrNull { it.isNotBlank() }
}