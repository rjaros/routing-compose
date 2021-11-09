package app.softwork.routingcompose

import androidx.compose.runtime.*
import kotlin.reflect.*


public abstract class Router {
    public abstract fun navigate(to: String)

    @Composable
    public abstract fun getPath(initPath: String): State<String>

    @Composable
    public operator fun invoke(
        initRoute: String,
        routing: @Composable NavBuilder.() -> Unit
    ) {
        require(initRoute.startsWith("/")) { "initRoute must start with a slash." }
        // Provide [RouterCompositionLocal] to composables deeper in the composition.
        CompositionLocalProvider(
            RouterCompositionLocal provides this
        ) {
            val path by getPath(initRoute)
            require(path.startsWith("/")) { "path must start with a slash." }
            val node by derivedStateOf { NavBuilder(path) }
            node.routing()
        }
    }

    public companion object {
        private val RouterCompositionLocal: ProvidableCompositionLocal<Router> =
            staticCompositionLocalOf { error("Router not defined, cannot provide through RouterCompositionLocal.") }

        /**
         * Provide the router implementation through a CompositionLocal so deeper level
         * Composables in the composition can have access to the current router.
         *
         * This is particularly useful for [NavLink], so we can have a single Composable
         * agnostic of the top level router implementation.
         *
         * To use this composition, you need to invoke any [Router] implementation first.
         */
        public val current: Router
            @Composable
            get() = RouterCompositionLocal.current
    }
}
