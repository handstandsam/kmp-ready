package com.handstandsam.kmpreadiness.internal.models

import com.handstandsam.kmpreadiness.internal.ReadinessData



internal sealed class ReadinessResult {
    data class Ready(val readyReason: ReadyReason, val readinessData: ReadinessData) : ReadinessResult()
    data class NotReady(val reason: String, val readinessData: ReadinessData) : ReadinessResult()
}