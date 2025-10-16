package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    // Threshold distance in pixels to count as a swipe
    private val SWIPE_THRESHOLD = 150
    private var x1: Float = 0.0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Find root layout (the LinearLayout itself)
        val rootLayout = findViewById<android.widget.LinearLayout>(R.id.root_layout)

        // Apply window inset padding
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find views (optional, in case you want to modify text/image later)
        val headerImage = findViewById<ImageView>(R.id.header_image)
        val swipeText = findViewById<TextView>(R.id.swipe_text)

        // Swipe detection
        rootLayout.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = event.x
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val x2 = event.x
                    val deltaX = x2 - x1

                    if (deltaX > SWIPE_THRESHOLD) {
                        // Swipe right → go to LoginActivity
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    }
                    true
                }
                else -> false
            }
        }
    }
}


//package vcmsa.projects.thedoghouse_prototype
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.os.Bundle
//import android.view.MotionEvent
//import android.view.View
//import android.widget.Button // Import Button class
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import kotlin.math.abs
//
//class MainActivity : AppCompatActivity() {
//
//    // Define a threshold distance for a swipe to be registered (in pixels)
//    private val SWIPE_THRESHOLD = 150
//
//    private var x1: Float = 0.0f // Variable to hold the starting X coordinate
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//
//        // Find the root ConstraintLayout
//        val mainLayout = findViewById<View>(R.id.background_image).parent as View
//
//        // Edge-to-edge padding logic
//        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        // Use SWIPE DETECTION LOGIC
//        mainLayout.setOnTouchListener { view, motionEvent ->
//            when (motionEvent.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    x1 = motionEvent.x
//                    true // Consume the event
//                }
//
//                MotionEvent.ACTION_UP -> {
//                    val x2 = motionEvent.x
//                    val deltaX = x2 - x1
//
//                    // Check for a RIGHT swipe (positive deltaX)
//                    if (deltaX > SWIPE_THRESHOLD) {
//                        val intent = Intent(this, LoginActivity::class.java)
//                        startActivity(intent)
//                    }
//                    true // Consume the event
//                }
//                else -> false // Let other touch events pass
//            }
//        }
//    }
//}