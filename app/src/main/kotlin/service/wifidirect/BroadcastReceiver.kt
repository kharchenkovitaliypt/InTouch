package com.vitaliykharchenko.intouch.service.wifidirect

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

typealias IntentAction = String

fun Context.broadcastFlow(vararg actions: IntentAction): Flow<Unit> =
    this.broadcastFlow(actions.map { it to { Unit } })

fun <T> Context.broadcastFlow(
    vararg actions: Pair<IntentAction, (Intent) -> T>
): Flow<T> =
    this.broadcastFlow(actions.toList())

fun <T> Context.broadcastFlow(
    actions: List<Pair<IntentAction, (Intent) -> T>>
): Flow<T> {
    val intentFilter = IntentFilter()
    actions.forEach { (action, _) ->
        intentFilter.addAction(action)
    }
    return callbackFlow<T> {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
//                println("WifiDirectBroadcastReceiver.onReceive() intent: $intent, bundle: ${intent.extras}")

                actions.find { (action, _) -> action == intent.action }
                    ?.let { (_, transform) -> transform(intent) }
                    ?.let(this@callbackFlow::offer)
            }
        }
        registerReceiver(receiver, intentFilter)

        awaitClose {
            unregisterReceiver(receiver)
        }
    }
}