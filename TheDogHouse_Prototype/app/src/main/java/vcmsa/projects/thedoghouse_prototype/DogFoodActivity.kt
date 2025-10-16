package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem // Needed for NavigationView listener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import android.app.DatePickerDialog // 🛑 NEW
import android.app.TimePickerDialog // 🛑 NEW
import android.util.Log
import androidx.activity.enableEdgeToEdge
import java.text.SimpleDateFormat
import java.util.Calendar // 🛑 NEW
import java.util.Locale

class DogFoodActivity : AppCompatActivity() {

    private lateinit var donorNameEditText: EditText
    private lateinit var dogFoodNameEditText: EditText
    private lateinit var dropOffDateEditText: EditText
    private lateinit var dropOffTimeEditText: EditText
    private lateinit var submitButton: Button

    private lateinit var fundsButton: Button
    private lateinit var dogFoodButton: Button
    private lateinit var medicationButton: Button

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "DogFoodDonation"

    // 🛑 NEW: Calendar instance to hold selected date/time
    private val dropOffCalendar = Calendar.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dog_food)

        auth = FirebaseAuth.getInstance()

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Correct drawer toggle
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        donorNameEditText = findViewById(R.id.DonorName)
        dogFoodNameEditText = findViewById(R.id.DogFoodName)
        dropOffDateEditText = findViewById(R.id.editTextDate)
        dropOffTimeEditText = findViewById(R.id.editTextTime2)
        submitButton = findViewById(R.id.button4)

        fundsButton = findViewById(R.id.button1)
        dogFoodButton = findViewById(R.id.button2)
        medicationButton = findViewById(R.id.button3)

        // 🛑 NEW: Set up click listeners for date and time pickers 🛑
        dropOffDateEditText.setOnClickListener {
            showDatePicker()
        }

        dropOffTimeEditText.setOnClickListener {
            showTimePicker()
        }
        // 🛑 END NEW PICKER LISTENERS 🛑

        submitButton.setOnClickListener {
            // Get current user and check authentication
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Please log in to submit a donation.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val donorName = donorNameEditText.text.toString().trim()
            val dogFoodName = dogFoodNameEditText.text.toString().trim()
            val date = dropOffDateEditText.text.toString().trim()
            val time = dropOffTimeEditText.text.toString().trim()
            val userId = currentUser.uid // Use the non-nullable uid here

            if (donorName.isEmpty() || dogFoodName.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val donationData = hashMapOf(
                    "donorName" to donorName,
                    "dogFoodName" to dogFoodName,
                    "dropOffDate" to date,
                    "dropOffTime" to time,
                    "timestamp" to Date(),
                    "userId" to userId
                )

                // Saving to the 'DogFoodDonations' subcollection
                firestore.collection("Users")
                    .document(currentUser.uid)
                    .collection("DogFoodDonations")
                    .add(donationData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Dog food donation submitted and saved to your history!", Toast.LENGTH_LONG).show()
                        clearFields()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to submit: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Failed to save donation", e)
                    }
            }
        }

        fundsButton.setOnClickListener {
            startActivity(Intent(this, FundsDonationsActivity::class.java))
            finish()
        }

        dogFoodButton.setOnClickListener {
            Toast.makeText(this, "You are already on Dog Food page", Toast.LENGTH_SHORT).show()
        }

        medicationButton.setOnClickListener {
            startActivity(Intent(this, MedsDonationActivity::class.java))
            finish()
        }

        // Hook up toolbar icon to open drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Handle nav item clicks
        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.nav_account -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                }
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                R.id.nav_newsletter -> {
                    startActivity(Intent(this, NewsletterActivity::class.java))
                }
                R.id.nav_fundsdonation -> {
                    startActivity(Intent(this, FundsDonationsActivity::class.java))
                    finish()
                }
                R.id.nav_volunteer -> {
                    startActivity(Intent(this, VolunteerActivity::class.java))
                    finish()
                }
                R.id.nav_adoption -> {
                    startActivity(Intent(this, ViewAdoptionActivity::class.java))
                    finish()
                }
                R.id.nav_help -> {
                    startActivity(Intent(this, HelpActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    // ----------------------------------------------------------------------
    // --- DATE/TIME PICKER LOGIC -------------------------------------------
    // ----------------------------------------------------------------------

    private fun showDatePicker() {
        val year = dropOffCalendar.get(Calendar.YEAR)
        val month = dropOffCalendar.get(Calendar.MONTH)
        val day = dropOffCalendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Update the shared Calendar instance
                dropOffCalendar.set(selectedYear, selectedMonth, selectedDay)

                // Format the date for display (e.g., 14 Oct 2025)
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                dropOffDateEditText.setText(dateFormat.format(dropOffCalendar.time))
            },
            year,
            month,
            day
        )
        // Prevent selecting dates in the past
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val hour = dropOffCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = dropOffCalendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                // Update the shared Calendar instance with the selected time
                dropOffCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                dropOffCalendar.set(Calendar.MINUTE, selectedMinute)

                // Format the time for display (e.g., 2:30 PM)
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                dropOffTimeEditText.setText(timeFormat.format(dropOffCalendar.time))
            },
            hour,
            minute,
            false // 'false' for 12-hour format with AM/PM
        )
        timePickerDialog.show()
    }

    private fun clearFields() {
        donorNameEditText.text.clear()
        dogFoodNameEditText.text.clear()
        dropOffDateEditText.text.clear()
        dropOffTimeEditText.text.clear()
        // Reset the calendar after clearing fields
        dropOffCalendar.time = Date()
    }
}