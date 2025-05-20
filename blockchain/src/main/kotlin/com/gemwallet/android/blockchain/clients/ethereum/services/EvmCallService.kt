package com.gemwallet.android.blockchain.clients.ethereum.services

import com.gemwallet.android.blockchain.clients.ethereum.EvmMethod
import com.gemwallet.android.blockchain.clients.ethereum.services.EvmRpcClient.EvmNumber
import com.gemwallet.android.blockchain.rpc.model.JSONRpcRequest
import com.gemwallet.android.blockchain.rpc.model.JSONRpcResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface EvmCallService {
    @POST("/")
    suspend fun callString(@Body request: JSONRpcRequest<List<Any>>): Result<JSONRpcResponse<String?>>

    @POST("/")
    suspend fun callNumber(@Body request: JSONRpcRequest<List<Any>>): Result<JSONRpcResponse<EvmNumber?>>

    @POST("/")
    suspend fun callBatch(@Body request: List<JSONRpcRequest<List<Any>>>): Result<List<JSONRpcResponse<String>>>
}

suspend fun EvmCallService.callString(contract: String, hexData: String): String? {
    val params = mapOf(
        "to" to contract,
        "data" to hexData
    )
    val request = JSONRpcRequest.create(
        EvmMethod.Call,
        listOf(
            params,
            "latest"
        )
    )
    return callString(request).getOrNull()?.result
}

suspend fun EvmCallService.batch(requests: List<JSONRpcRequest<List<Any>>>): List<String> {
    val result =  callBatch(requests)
    return result.getOrNull()?.map { it.result } ?: emptyList()
}

fun EvmCallService.createCallRequest(to: String, data: String, tag: String? = null): JSONRpcRequest<List<Any>> {
    val params = mapOf(
        "to" to to,
        "data" to data
    )
    return JSONRpcRequest.create(
        EvmMethod.Call,
        listOf(
            params,
            tag
        ).filterNotNull()
    )
}