package com.handstandsam.kmpreadiness.internal.deptraversal

internal data class ArtifactDependency(
    val group: String,
    val artifact: String,
    val version: String,
) : Dependency {
    override val name: String get() = "$group:$artifact:$version"
}
