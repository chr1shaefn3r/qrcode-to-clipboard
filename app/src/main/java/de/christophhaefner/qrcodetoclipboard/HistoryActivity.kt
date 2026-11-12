package de.christophhaefner.qrcodetoclipboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.divider.MaterialDividerItemDecoration

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        val entries = History.load(this)
        findViewById<View>(R.id.empty).visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE

        val list = findViewById<RecyclerView>(R.id.list)
        list.adapter = Adapter(entries) { copyToClipboard(it) } // the system shows its own "copied" confirmation
        list.addItemDecoration(
            MaterialDividerItemDecoration(this, MaterialDividerItemDecoration.VERTICAL).apply {
                isLastItemDecorated = false
            }
        )
        ViewCompat.setOnApplyWindowInsetsListener(list) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.updatePadding(left = bars.left, right = bars.right, bottom = bars.bottom)
            insets
        }
    }

    private class Adapter(
        private val entries: List<String>,
        private val onClick: (String) -> Unit,
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemCount() = entries.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val entry = entries[position]
            (holder.itemView as TextView).text = entry
            holder.itemView.setOnClickListener { onClick(entry) }
        }
    }
}
