import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.handstandsam.kmpready.internal.SearchRemote
import com.handstandsam.kmpready.internal.models.Gav
import com.handstandsam.kmpready.internal.models.KotlinToolingMetadataResult
import com.handstandsam.shoppingapp.multiplatform.Platform
import com.handstandsam.shoppingapp.multiplatform.ui.ButtonType
import com.handstandsam.shoppingapp.multiplatform.ui.ShoppingAppButton
import com.handstandsam.shoppingapp.multiplatform.ui.WrappedPreformattedText
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {

    val scope = CoroutineScope(Dispatchers.Unconfined)

    data class MainState(
        val inputTextValue: String = "org.jetbrains.kotlinx:kotlinx-datetime:0.3.2",
        val result: String = ""
    )

    @Composable
    fun ShoppingApp() {

        val state = mutableStateOf(MainState())

        Div {
            Text("Value: ${state.value.inputTextValue}")
            Input(InputType.Text) {
                onInput {
                    state.value = state.value.copy(inputTextValue = it.value)
                }
            }

            ShoppingAppButton(label = "GetInfo", buttonType = ButtonType.PRIMARY) {
                val searchRemote = SearchRemote(HttpClient(Platform().ktorHttpClientEngine))
                val gav = Gav.fromString(state.value.inputTextValue)
                if (gav != null) {
                    val url = SearchRemote.getUrlForKotlinToolingMetadata("https://repo.maven.apache.org/maven2/", gav)
                    scope.launch {
                        val searchResult: KotlinToolingMetadataResult = searchRemote.searchForInRepo(
                            kotlinToolingMetadataUrlString = url
                        )
                        println("Got: $searchResult")
                        when (searchResult) {
                            is KotlinToolingMetadataResult.NotFound -> {
                                state.value = state.value.copy(result = "not found")
                            }
                            is KotlinToolingMetadataResult.Success -> {
                                state.value = state.value.copy(result = "${searchResult.json}")
                            }
                        }
                    }
                } else {
                    state.value = state.value.copy(result = "Can't parse GAV ${state.value.inputTextValue}")
                }
            }
            WrappedPreformattedText(state.value.result)
        }
    }

    /** Entry Point */
    renderComposable(rootElementId = "root") {
        ShoppingApp()
    }
}