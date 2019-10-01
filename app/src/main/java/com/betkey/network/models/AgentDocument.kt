package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class AgentDocument(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("username")
    var username: String = "",

    @SerializedName("username_canonical")
    var usernameCanonical: String = "",

    @SerializedName("email")
    var email: String = "",

    @SerializedName("email_canonical")
    var emailCanonical: String = "",

    @SerializedName("enabled")
    var enabled: Boolean? = null,

    @SerializedName("salt")
    var salt: String = "",

    @SerializedName("password")
    var password: String = "",

    @SerializedName("locked")
    var locked: Boolean? = null,

    @SerializedName("expired")
    var expired: Boolean? = null,

    @SerializedName("roles")
    var roles: List<String> = listOf(),

    @SerializedName("credentials_expired")
    var credentialsExpired: Boolean? = null,

    @SerializedName("third_party_ids")
    var thirdPartyIds: Map<String, String> = mapOf(),

    @SerializedName("instance")
    var instance: Instance? = null,

    @SerializedName("shop")
    var shop: ShopAgent? = null,

    @SerializedName("first_name")
    var firstName: String = "",

    @SerializedName("last_name")
    var last_name: String = "",

    @SerializedName("phone")
    var phone: String = "",

    @SerializedName("ip")
    var ip: String = "",

    @SerializedName("channel")
    var channel: String = "",

    @SerializedName("token")
    var token: String = ""
)