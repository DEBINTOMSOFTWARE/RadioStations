package com.example.radiostations.core.framework

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.math.pow

class RetryInterceptor(
    private val maxRetries: Int = 5,
    private val initialDelay: Long = 1000L,
    private val maxDelay: Long = 16000L,
    private val factor: Double = 2.0
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var response: Response? = null
        var throwable: Throwable? = null

        var currentDelay = initialDelay

        for (attempt in 0 until maxRetries) {
            try {
                response?.close() // Ensure the previous response is closed
                response = chain.proceed(chain.request())
                if (response.isSuccessful) {
                    return response
                }
            } catch (e: IOException) {
                throwable = e
            }

            try {
                Thread.sleep(currentDelay)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw IOException("Thread interrupted", e)
            }

            // Calculate the next delay with exponential backoff
            currentDelay = (initialDelay * factor.pow(attempt.toDouble())).toLong().coerceAtMost(maxDelay)
        }

        throwable?.let { throw it }
        return response ?: throw IOException("Max retries reached without success")
    }
}