package com.handstandsam.kmpreadiness.internal

import org.gradle.api.Project

internal fun Project.isRootProject(): Boolean = this == rootProject