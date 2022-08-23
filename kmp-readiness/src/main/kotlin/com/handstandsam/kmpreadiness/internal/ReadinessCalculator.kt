package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.deptraversal.DepTraversal
import com.handstandsam.kmpreadiness.internal.models.ReadinessResult
import kotlinx.coroutines.runBlocking
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

internal class ReadinessCalculator(private val target: Project) {

    fun computeReadinessResult(): ReadinessResult {
        val computedDependencies = DepTraversal.getGavsForProject(target)
        val appliedGradlePlugins = AppliedGradlePlugins(
            java = target.plugins.any { it::class.java == org.gradle.api.plugins.JavaPlugin::class.java },
            multiplatform = target.plugins.any { it::class.java == KotlinMultiplatformPluginWrapper::class.java },
            kotlin = target.plugins.any { it::class.java == KotlinPluginWrapper::class.java }
        )
        val sourceSetSearcherResult = SourceSetSearcher().searchSourceSets(target)
        val kmpDependenciesAnalysisResult = runBlocking {
            DependenciesReadinessProcessor(FileUtil.projectDirOutputFile(target)).process(computedDependencies)
        }

        val kmpReadinessData = ReadinessData(
            projectName = target.path,
            dependencyAnalysis = kmpDependenciesAnalysisResult,
            gradlePlugins = appliedGradlePlugins,
            sourceSets = sourceSetSearcherResult
        )
        return kmpReadinessData.computeReadiness()
    }
}