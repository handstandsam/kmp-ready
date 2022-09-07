import com.handstandsam.kmpreadiness.internal.DependenciesReadinessProcessor
import kotlinx.coroutines.runBlocking
import java.io.File

internal fun main(): Unit = runBlocking {

    val depsToProcess: List<Gav> = """
        javax.inject:javax.inject:1
        org.json:json:20180813
    """.trimIndent().lines()
        .map { Gav.fromString(it)!! }

    val processor = DependenciesReadinessProcessor(File(""))
    processor.process(depsToProcess)
}

