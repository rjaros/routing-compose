package app.softwork.routingcompose

import androidx.compose.runtime.*

/**
 * This [Router] implementation uses `/#/path` to persistent the current route in [window.location.hash].
 *
 * Every request will always request `GET /`, so your server needs only to listen and serve this endpoint,
 * or using a SaaS `/index.html`.
 */
@Composable
public fun HashRouter(
    initPath: String,
    routeBuilder: @Composable RouteBuilder.() -> Unit
) {
    Router.internalGlobalRouter = HashRouter().apply { route(initPath, routeBuilder) }
}

internal class HashRouter : Router {
    override val currentPath: Path
        get() = Path.from(currentHash.value)

    private val currentHash: MutableState<String> = mutableStateOf(window.location.hash.currentURL() ?: "")

    @Composable
    override fun getPath(initPath: String): State<String> {
        LaunchedEffect(Unit) {
            currentHash.value = window.location.hash.currentURL() ?: initPath
            window.onhashchange = {
                currentHash.value = window.location.hash.currentURL() ?: ""
                Unit
            }
        }
        return currentHash
    }

    private fun String.currentURL() = removePrefix("#")
        .removePrefix("/")
        .ifBlank { null }

    override fun navigate(to: String, hide: Boolean, replace: Boolean) {
        if (hide) {
            currentHash.value = to.currentURL() ?: ""
        } else if (window.location.hash.currentURL() == to.currentURL()) {
            currentHash.value = to.removePrefix("#")
        } else {
            window.location.hash = to
        }
    }
}
