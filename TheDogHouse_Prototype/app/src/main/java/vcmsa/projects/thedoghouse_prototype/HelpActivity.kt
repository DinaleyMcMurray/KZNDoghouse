package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val btnContactManager: Button = findViewById(R.id.btnContactManager)

        btnContactManager.setOnClickListener {
            // Replace with manager's phone number
            val phoneNumber = "27716215128" // South Africa +27
            val message = "Hello, I need assistance with the app."

            try {
                val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.whatsapp")
                    data = Uri.parse(url)
                }
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                // If WhatsApp is not installed, open Play Store
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
                }
                startActivity(intent)
            }
        }
    }
}
