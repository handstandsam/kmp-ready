package com.handstandsam.kmpreadiness.internal

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.api.AndroidBasePlugin
import com.handstandsam.kmpreadiness.internal.deptraversal.DepTraversal
import com.handstandsam.kmpreadiness.internal.models.NotReadyReasonType
import com.handstandsam.kmpreadiness.internal.models.ReadinessResult
import com.handstandsam.kmpreadiness.internal.models.ReadyReasonType
import com.handstandsam.kmpreadiness.internal.models.Reason
import kotlinx.coroutines.runBlocking
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

internal class ReadinessCalculator(private val target: Project) {

    fun Project.hasExtension(extensionClass: Class<*>): Boolean {
        return target.extensions.findByType(extensionClass) != null
    }

    private fun computeReadinessData(): ReadinessData {
        val computedDependencies = DepTraversal.getGavsForProject(target)
        val appliedGradlePlugins = AppliedGradlePlugins(
            java = target.hasExtension(JavaPluginExtension::class.java),
            kotlinMultiplatform = target.hasExtension(KotlinMultiplatformExtension::class.java),
            androidLibrary = target.hasExtension(LibraryExtension::class.java),
            androidApplication = target.hasExtension(ApplicationExtension::class.java),
            kotlinJvm = target.hasExtension(KotlinJvmProjectExtension::class.java),
            plugins = target.plugins.map { it::class.java.name }
        )
        val sourceSetSearcherResult = SourceSetSearcher().searchSourceSets(target)
        val kmpDependenciesAnalysisResult = runBlocking {
            DependenciesReadinessProcessor(FileUtil.projectDirOutputFile(target)).process(computedDependencies)
        }

        val kmpReadinessData = ReadinessData(
            projectName = target.path,
            dependencyAnalysis = kmpDependenciesAnalysisResult,
            gradlePlugins = appliedGradlePlugins,
            sourceSets = sourceSetSearcherResult,
        )
        return kmpReadinessData
    }

    fun computeReadinessResult(): ReadinessResult {
        val kmpReadinessData = computeReadinessData()
        return computeReadiness(kmpReadinessData)
    }

    internal fun MutableList<Reason>.addReadyReason(readyReasonType: ReadyReasonType, details: String? = null) {
        this.add(
            Reason.ReadyReason(
                type = readyReasonType,
                details = details,
            )
        )
    }

    internal fun MutableList<Reason>.addNotReadyReason(
        notReadyReasonType: NotReadyReasonType,
        details: String? = null
    ) {
        this.add(
            Reason.NotReadyReason(
                type = notReadyReasonType,
                details = details,
            )
        )
    }

    fun computeReadiness(readinessData: ReadinessData): ReadinessResult {
        val reasons = mutableListOf<Reason>()
        readinessData.apply {
            if (dependencyAnalysis.hasOnlyMultiplatformCompatibleDependencies) {
                reasons.addReadyReason(ReadyReasonType.HasOnlyMultiplatformCompatibleDependencies)
            } else {
                reasons.addNotReadyReason(
                    NotReadyReasonType.IncompatibleDependencies,
                    buildString {
                        dependencyAnalysis.incompatible.forEach {
                            appendLine("* $it")
                        }
                    }
                )
            }

            if (readinessData.sourceSets.hasOnlyKotlinFiles) {
                reasons.addReadyReason(ReadyReasonType.HasOnlyKotlinFiles)
            } else {
                reasons.addNotReadyReason(
                    NotReadyReasonType.HasJavaFiles,
                    buildString {
                        sourceSets.javaFiles.forEach { file ->
                            appendLine(" * $file")
                        }
                    }
                )
            }
            if(gradlePlugins.hasCompatiblePlugin()){
                if (gradlePlugins.kotlinMultiplatform) {
                    reasons.addReadyReason(ReadyReasonType.MultiplatformPluginAlreadyEnabled)
                }
                if (gradlePlugins.kotlinJvm) {
                    reasons.addReadyReason(ReadyReasonType.KotlinPluginEnabled)
                }
            }else{
                reasons.addNotReadyReason(NotReadyReasonType.DoesNotHaveKotlinJvmOrMultiplatformPlugin, buildString {
                    appendLine("Applied Plugins")
                    gradlePlugins.plugins.forEach { plugin: String ->
                        appendLine(" * $plugin")
                    }
                })
                if (gradlePlugins.androidLibrary) {
                    reasons.addNotReadyReason(NotReadyReasonType.IsAndroidLibrary)
                }
                if (gradlePlugins.androidApplication) {
                    reasons.addNotReadyReason(NotReadyReasonType.IsAndroidApplication)
                }
                if(gradlePlugins.java){
                    reasons.addNotReadyReason(NotReadyReasonType.HasJavaPlugin)
                }

            }

        }

        return ReadinessResult(
            reasons = reasons,
            readinessData = readinessData
        )
    }
}