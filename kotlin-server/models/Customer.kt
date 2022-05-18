package com.example.models

import kotlinx.serialization.Serializable

@Serializable
// this annotation + the json() enabled in Serialization.kt will enable this class being serialized
data class Customer (val id: String, val firstName: String, val lastName: String, val age: Int)

// This will act as our database
val customerStorage = mutableListOf<Customer>()