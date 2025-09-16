package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val videoView = findViewById<VideoView>(R.id.videoView)
        val path = "android.resource://${packageName}/${R.raw.mainpage}"
        videoView.setVideoURI(Uri.parse(path))
        videoView.setOnPreparedListener { it.isLooping = true }
        videoView.start()

        // Button to go to MedsActivity
//        val medsButton = findViewById<Button>(R.id.btnMeds)
//        medsButton.setOnClickListener {
//            val intent = Intent(this, MedsActivity::class.java)
//            startActivity(intent)
//        }

        // on main layout -> Go to RegisterActivity
        val mainLayout = findViewById<android.view.View>(R.id.main)
        mainLayout.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}

