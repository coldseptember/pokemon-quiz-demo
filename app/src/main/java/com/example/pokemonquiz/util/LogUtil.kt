package com.example.pokemonquiz.util

import android.util.Log
import com.example.pokemonquiz.BuildConfig

object LogUtil {

    private const val TAG_PREFIX = "PQ"

    // Logs are only printed in debug builds, silent in release
    private inline val enabled: Boolean get() = BuildConfig.DEBUG

    private fun tag(tag: String): String = "$TAG_PREFIX|$tag"

    // ---- d (debug) ----

    @JvmStatic
    fun d(tag: String, msg: String) {
        if (enabled) Log.d(tag(tag), msg)
    }

    @JvmStatic
    fun d(tag: String, msg: String, tr: Throwable) {
        if (enabled) Log.d(tag(tag), msg, tr)
    }

    // ---- w (warning) ----

    @JvmStatic
    fun w(tag: String, msg: String) {
        if (enabled) Log.w(tag(tag), msg)
    }

    @JvmStatic
    fun w(tag: String, msg: String, tr: Throwable) {
        if (enabled) Log.w(tag(tag), msg, tr)
    }

    // ---- e (error) ----

    @JvmStatic
    fun e(tag: String, msg: String) {
        if (enabled) Log.e(tag(tag), msg)
    }

    @JvmStatic
    fun e(tag: String, msg: String, tr: Throwable) {
        if (enabled) Log.e(tag(tag), msg, tr)
    }
}
