# Pokemon Quiz Demo

A Pokemon species search app built with Kotlin and Jetpack Compose. Search Pokemon by name, browse species details, paginated results with color-coded cards.

## Tech Stack

| Layer | Choice | Reason |
|---|---|---|
| Language | Kotlin 2.0+ | Required by spec; modern, null-safe, first-class Android support |
| UI | Jetpack Compose + Material 3 | Required by spec; declarative UI, less boilerplate than XML |
| DI | Hilt | Official Android DI; integrates with ViewModel + Navigation Compose |
| Networking | Apollo GraphQL | Native GraphQL client for Android; generates type-safe Kotlin models from `.graphql` schemas |
| Images | Coil (Compose) | Lightweight image loading with Compose integration; handles caching and placeholder states |
| Navigation | Navigation Compose | Type-safe route args, back-stack management, integrates with Hilt ViewModels |
| Build | Gradle KTS + Version Catalog | Centralized dependency management via `libs.versions.toml` |

### Version Compliance

| Requirement | Actual |
|---|---|
| AGP < 9.0 | 8.7.3 |
| Compose BOM >= 2025 | 2025.01.00 |
| Kotlin 2.x | 2.0.21 |
| minSdk (last 2 years) | 26 (Android 8.0) |
| targetSdk | 35 |

## Project Structure

```
app/src/main/java/com/example/pokemonquiz/
├── MainActivity.kt              # Single Activity, NavHost entry point
├── PokemonQuizApp.kt            # Hilt Application class
│
├── data/
│   ├── PokemonRepository.kt     # GraphQL data layer, wraps Apollo calls
│   └── LoggingInterceptor.kt    # Debug-only HTTP request/response logger
│
├── di/
│   └── NetworkModule.kt         # Hilt module: ApolloClient singleton
│
├── ui/
│   ├── theme/
│   │   ├── Theme.kt             # Light blue theme (Material 3)
│   │   └── Typography.kt        # Type scale definitions
│   │
│   ├── components/
│   │   └── LoadingErrorContent.kt  # Reusable loading/error/empty-state wrapper
│   │
│   └── screens/
│       ├── splash/SplashScreen.kt  # First-launch welcome screen
│       ├── search/
│       │   ├── SearchScreen.kt     # Search bar + paginated result list
│       │   └── SearchViewModel.kt  # Debounce, pagination, state management
│       └── detail/
│           ├── DetailScreen.kt     # Species detail with abilities
│           └── DetailViewModel.kt  # Detail loading and state
│
└── util/
    ├── Config.kt                # Centralized constants (page size, etc.)
    └── LogUtil.kt               # Debug-only logging wrapper (tag prefix: PQ|)
```

### Architecture: MVVM

- **Screen** — Composable, receives state from ViewModel, dispatches user events
- **ViewModel** — Manages UI state via `MutableStateFlow`, handles debounce (400ms), pagination logic
- **Repository** — Single source of GraphQL queries; catches exceptions and returns fallback values (empty/null) to prevent crashes

## Key Design Decisions

- **Splash only on first launch** — `SharedPreferences` flag avoids showing splash on subsequent opens
- **Debounce 400ms** — `LaunchedEffect` + `delay` in ViewModel; cancels previous job on new input
- **Pagination via offset/limit** — `Config.PAGE_SIZE = 20`; `canLoadMore` set to `false` when result count < page size
- **Debug-only logging** — `LogUtil` gates all output behind `BuildConfig.DEBUG`; no logs in release builds
