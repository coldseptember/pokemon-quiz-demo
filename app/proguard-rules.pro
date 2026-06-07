# ============================================================
# ProGuard / R8 Rules — Pokemon Quiz
# ============================================================

# ---- General ----
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations

# ---- Apollo GraphQL (generated adapters / types / queries) ----
-keep class com.example.pokemonquiz.adapter.** { *; }
-keep class com.example.pokemonquiz.selections.** { *; }
-keep class com.example.pokemonquiz.type.** { *; }
-keep class com.example.pokemonquiz.*Query { *; }
-keep class com.example.pokemonquiz.*Query$* { *; }

# ---- Hilt / Dagger ----
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponentManager { *; }

# ---- Kotlin ----
-keep class kotlin.Metadata { *; }
-keep class kotlin.coroutines.Continuation { *; }

# ---- Compose ----
-keep class androidx.compose.runtime.** { *; }

# ---- Navigation Compose ----
-keep class * extends androidx.navigation.NavType

# ---- ViewModels (Hilt-injected, may be accessed via reflection) ----
-keep class * extends androidx.lifecycle.ViewModel { *; }

# ---- Application class ----
-keep class com.example.pokemonquiz.PokemonQuizApp { *; }

# ---- Dagger-generated factories (keep to avoid runtime linkage errors) ----
-keep class * extends dagger.internal.Factory { *; }
-keep class * extends dagger.internal.DoubleCheck { *; }
-keep class * extends dagger.internal.Preconditions { *; }
