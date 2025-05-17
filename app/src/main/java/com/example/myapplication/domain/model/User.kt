package com.example.myapplication.domain.model

import java.io.Serializable

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val profileImage: String? = null,
    val address: Address? = null,
    val drivingLicense: String? = null,
    val dateOfBirth: String? = null,
    val favorites: List<String> = emptyList()
) : Serializable

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String
) : Serializable 