package de.christophhaefner.qrcodetoclipboard

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

class HistoryActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val entries = History.load(this)
        val list = findViewById<ListView>(R.id.list)
        list.emptyView = findViewById<View>(R.id.empty)
        list.adapter = ArrayAdapter(this, R.layout.item_history, entries)
        list.setOnItemClickListener { _, _, position, _ ->
            copyToClipboard(entries[position])
            // Android 13+ shows its own confirmation when something is copied.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                Toast.makeText(this, R.string.copied_toast, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
