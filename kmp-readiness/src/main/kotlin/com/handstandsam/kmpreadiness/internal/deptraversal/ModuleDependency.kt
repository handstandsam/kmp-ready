package com.handstandsam.kmpreadiness.internal.deptraversal

internal data class ModuleDependency(
    val path: String,
) : Dependency {
    override val name: String get() = path
}
