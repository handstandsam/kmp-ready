package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.models.Gav

internal data class KmpReadyResult(
    val gav: Gav,
    val isReady: Boolean
)
