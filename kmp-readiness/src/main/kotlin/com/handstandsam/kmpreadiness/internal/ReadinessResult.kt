package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpready.internal.models.Reason

internal data class ReadinessResult(val reasons: List<Reason>, val readinessData: ReadinessData) {
    val readyReasons: List<Reason.ReadyReason> =
        reasons.filterIsInstance<Reason.ReadyReason>()
    val notReadyReasons: List<Reason.NotReadyReason> =
        reasons.filterIsInstance<Reason.NotReadyReason>()

    val headline: String = if (notReadyReasons.isEmpty()) {
        "KMP Ready"
    } else {
        "Not KMP Ready"
    }
}