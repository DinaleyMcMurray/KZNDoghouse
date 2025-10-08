package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button // Import Button class
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    // Define a threshold distance for a swipe to be registered (in pixels)
    private val SWIPE_THRESHOLD = 150

    private var x1: Float = 0.0f // Variable to hold the starting X coordinate

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Find the root ConstraintLayout
        val mainLayout = findViewById<View>(R.id.background_image).parent as View

        // Edge-to-edge padding logic
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Use SWIPE DETECTION LOGIC
        mainLayout.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = motionEvent.x
                    true // Consume the event
                }

                MotionEvent.ACTION_UP -> {
                    val x2 = motionEvent.x
                    val deltaX = x2 - x1

                    // Check for a RIGHT swipe (positive deltaX)
                    if (deltaX > SWIPE_THRESHOLD) {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    true // Consume the event
                }
                else -> false // Let other touch events pass
            }
        }
    }
}