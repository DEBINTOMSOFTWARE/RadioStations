package com.example.radiostations.core.utils

sealed class Resource<out T> {
    class Initial<T>() : Resource<T>()
    data class Success<out T>(val data: T?) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}