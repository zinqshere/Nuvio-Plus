package com.nuvio.app.features.player

/**
 * Android-only player volume state.
 *
 * 0f..1f is handled by Android's STREAM_MUSIC volume. Values above 1f keep
 * the system stream at its maximum and enable additional player-side gain.
 */
internal object AndroidPlayerVolumeBoost {
    @Volatile
    var fraction: Float = 1f
        private set

    private val listeners = LinkedHashSet<(Float) -> Unit>()

    @Synchronized
    fun setFraction(value: Float) {
        fraction = value.coerceIn(0f, 2f)
        listeners.toList().forEach { it(fraction) }
    }

    @Synchronized
    fun addListener(listener: (Float) -> Unit): () -> Unit {
        listeners += listener
        listener(fraction)
        return { removeListener(listener) }
    }

    @Synchronized
    private fun removeListener(listener: (Float) -> Unit) {
        listeners -= listener
    }
}
