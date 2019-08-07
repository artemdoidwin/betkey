package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Transaction (

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("owner_type")
    var ownerType: Int? = null,

    @SerializedName("owner_id")
    var ownerId: String? = null,

    @SerializedName("owner_short_id")
    var ownerShortId: Int? = null,

    @SerializedName("owner_username")
    var ownerUsername: String? = null,

    @SerializedName("owner_phone")
    var ownerPhone: String? = null,

    @SerializedName("owner_email")
    var ownerEmail: String? = null,

    @SerializedName("method")
    var method: Int? = null,

    @SerializedName("type")
    var type: Int? = null,

    @SerializedName("ticket_id")
    var ticketId: String? = null,

    @SerializedName("payment_id")
    var paymentId: String? = null,

    @SerializedName("currency")
    var currency: String? = null,

    @SerializedName("amount")
    var amount: String? = null,

    @SerializedName("old_balance")
    var oldBalance: String? = null,

    @SerializedName("new_balance")
    var newBalance: String? = null,

    @SerializedName("status")
    var status: Int? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("created")
    var created: Int? = null,

    @SerializedName("updated")
    var updated: Int? = null,

    @SerializedName("platform_id")
    var platformId: String? = null,

    @SerializedName("platform_name")
    var platformName: String? = null,

    @SerializedName("platform_unit_id")
    var platformUnitId: String? = null,

    @SerializedName("platform_unit_name")
    var platformUnitName: String? = null,

    @SerializedName("psp_id")
    var pspId: String? = null,

    @SerializedName("psp_payment_id")
    var pspPaymentId: String? = null,

    @SerializedName("psp_name")
    var pspName: String? = null,

    @SerializedName("psp_status")
    var pspStatus: String? = null,

    @SerializedName("psp_created")
    var pspCreated: String? = null,

    @SerializedName("psp_error_code")
    var pspErrorCode: String? = null,

    @SerializedName("psp_error_message")
    var pspErrorMessage: String? = null

)
