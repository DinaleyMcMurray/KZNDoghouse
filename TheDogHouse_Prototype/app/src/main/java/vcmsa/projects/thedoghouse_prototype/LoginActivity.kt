package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // Hardcoded admin credentials (temporary)
    private val ADMIN_EMAIL = "admin@gmail.com"
    private val ADMIN_PASSWORD = "AdminPassword123"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        supportActionBar?.hide()


        setContentView(R.layout.activity_login)

        // ✅ The ScrollView is your root view; use that instead of a non-existent ConstraintLayout ID
        val rootLayout = findViewById<View>(android.R.id.content)

//        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // ✅ Firebase Auth
        auth = FirebaseAuth.getInstance()

        // ✅ Find views by their IDs from XML
        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val signUpButton = findViewById<Button>(R.id.signupBtn)

        // ✅ Login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Admin login check
            if (email == ADMIN_EMAIL && password == ADMIN_PASSWORD) {
                Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AdminHomeActivity::class.java))
                finish()
                return@setOnClickListener
            }

            // ✅ Regular Firebase login
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Login failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        // ✅ Sign-up button click
        signUpButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}


//package vcmsa.projects.thedoghouse_prototype
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.os.Bundle
//import android.view.View // <--- ADD THIS MISSING IMPORT
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.google.firebase.auth.FirebaseAuth
//
//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var auth: FirebaseAuth
//
//    // Hardcoded admin credentials (temporary)
//    private val ADMIN_EMAIL = "admin@gmail.com"
//    private val ADMIN_PASSWORD = "AdminPassword123"
//
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        // FIX: Find the root layout using the correct ID from the XML: R.id.main_constraint_layout
//        val rootLayout = findViewById<View>(R.id.main_constraint_layout)
//
//        // Ensure rootLayout is not null before setting the listener
//        if (rootLayout != null) {
//            ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
//                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//                // Apply system window insets (like status bar height) to the padding of the view
//                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//                insets
//            }
//        } else {
//            // This is a safety check; should only run if the XML is severely broken
//            Toast.makeText(this, "Error: Root layout not found in XML!", Toast.LENGTH_LONG).show()
//        }
//
//        auth = FirebaseAuth.getInstance()
//
//        val emailEditText = findViewById<EditText>(R.id.etEmail)
//        val passwordEditText = findViewById<EditText>(R.id.etPassword)
//        val loginButton = findViewById<Button>(R.id.btnLogin)
//        val signUpButton = findViewById<Button>(R.id.signupBtn)
//
//        loginButton.setOnClickListener {
//            val email = emailEditText.text.toString().trim()
//            val password = passwordEditText.text.toString().trim()
//
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // Admin Login Check
//            if (email == ADMIN_EMAIL && password == ADMIN_PASSWORD) {
//                Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show()
//                startActivity(Intent(this, AdminHomeActivity::class.java))
//                finish()
//                return@setOnClickListener
//            }
//
//            // Regular User Login with Firebase
//            auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this, HomeActivity::class.java))
//                        finish()
//                    } else {
//                        Toast.makeText(
//                            this,
//                            "Login failed: ${task.exception?.message}",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//        }
//
//        signUpButton.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }
//    }
//}