package de.christophhaefner.qrcodetoclipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import org.json.JSONArray

private const val PREFS = "history"
private const val KEY = "scans"
private const val MAX_ENTRIES = 100

/** The last [MAX_ENTRIES] scanned values, newest first. */
object History {

    fun load(context: Context): List<String> {
        val json = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null)
            ?: return emptyList()
        val array = JSONArray(json)
        return List(array.length()) { array.getString(it) }
    }

    /** Puts [value] on top; a value that is already in the list is moved up instead of duplicated. */
    fun add(context: Context, value: String) {
        val entries = (listOf(value) + load(context).filter { it != value }).take(MAX_ENTRIES)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, JSONArray(entries).toString())
            .apply()
    }
}

fun Context.copyToClipboard(value: String) {
    getSystemService(ClipboardManager::class.java)
        .setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name), value))
}
