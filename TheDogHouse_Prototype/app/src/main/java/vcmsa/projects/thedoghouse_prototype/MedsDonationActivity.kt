package vcmsa.projects.thedoghouse_prototype

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem // 🛑 FIX: Needed for setNavigationItemSelectedListener
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView // 🛑 FIX: Needed for NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MedsDonationActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var donorNameEditText: EditText
    private lateinit var medicationNameEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var dropOffDateEditText: EditText
    private lateinit var dropOffTimeEditText: EditText
    private lateinit var submitButton: Button

    // Navigation buttons
    private lateinit var fundsButton: Button
    private lateinit var dogFoodButton: Button
    private lateinit var medicationButton: Button

    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "MedsDonation"

    // Calendar instance to hold selected date/time for pickers
    private val dropOffCalendar = Calendar.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_meds_donation)

        // Initialize Auth
        auth = FirebaseAuth.getInstance()

        // Initialize drawer and toolbar
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Correct drawer toggle
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Initialize form fields
        donorNameEditText = findViewById(R.id.DonorName)
        medicationNameEditText = findViewById(R.id.MedicationName)
        quantityEditText = findViewById(R.id.Quantity)
        dropOffDateEditText = findViewById(R.id.editTextDate)
        dropOffTimeEditText = findViewById(R.id.editTextTime2)
        submitButton = findViewById(R.id.button4)

        fundsButton = findViewById(R.id.button1)
        dogFoodButton = findViewById(R.id.button2)
        medicationButton = findViewById(R.id.button3)

        // 🛑 Date and Time Picker Click Listeners 🛑
        dropOffDateEditText.setOnClickListener {
            showDatePicker()
        }

        dropOffTimeEditText.setOnClickListener {
            showTimePicker()
        }

        // ⚡️ Submit logic ⚡️
        submitButton.setOnClickListener {
            saveMedsDonation()
        }

        // Navigation buttons
        fundsButton.setOnClickListener {
            startActivity(Intent(this, FundsDonationsActivity::class.java))
        }
        dogFoodButton.setOnClickListener {
            startActivity(Intent(this, DogFoodActivity::class.java))
        }
        medicationButton.setOnClickListener {
            Toast.makeText(this, "You are already on the Medication page", Toast.LENGTH_SHORT).show()
        }

        // NAV DRAWER CLICK HANDLER
        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem -> // 🛑 FIX: Added explicit type MenuItem
            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_newsletter -> startActivity(Intent(this, NewsletterActivity::class.java))
                R.id.nav_volunteer -> startActivity(Intent(this, VolunteerActivity::class.java))
                R.id.nav_adoption -> startActivity(Intent(this, ViewAdoptionActivity::class.java))
                R.id.nav_fundsdonation -> drawerLayout.closeDrawers()
                R.id.nav_account -> startActivity(Intent(this, EditProfileActivity::class.java))
                R.id.nav_logout -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_help -> {startActivity(Intent(this, HelpActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
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
        // Set the minimum date to today (or later)
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
            false // 'false' for 12-hour format (true for 24-hour)
        )
        timePickerDialog.show()
    }

    // ----------------------------------------------------------------------
    // --- FIREBASE SAVING FUNCTION -----------------------------------------
    // ----------------------------------------------------------------------

    private fun saveMedsDonation() {
        val donorName = donorNameEditText.text.toString().trim()
        val medicationName = medicationNameEditText.text.toString().trim()
        val quantity = quantityEditText.text.toString().trim()
        val dropOffDate = dropOffDateEditText.text.toString().trim()
        val dropOffTime = dropOffTimeEditText.text.toString().trim()
        val userId = auth.currentUser?.uid

        if (donorName.isEmpty() || medicationName.isEmpty() || quantity.isEmpty() || dropOffDate.isEmpty() || dropOffTime.isEmpty()) {
            Toast.makeText(this, "Please fill in all donation fields.", Toast.LENGTH_LONG).show()
            return
        }

        if (userId == null) {
            Toast.makeText(this, "User not authenticated. Please log in.", Toast.LENGTH_LONG).show()
            return
        }

        // Create the donation map
        val donationData = hashMapOf(
            "donorName" to donorName,
            "medicationName" to medicationName,
            "quantity" to quantity,
            "dropOffDate" to dropOffDate,
            "dropOffTime" to dropOffTime,
            "timestamp" to Date(),
            "userId" to userId
        )

        // Path: /Users/{userId}/MedsDonations/{documentId}
        firestore.collection("Users")
            .document(userId)
            .collection("MedsDonations")
            .add(donationData)
            .addOnSuccessListener {
                Log.d(TAG, "Meds Donation successfully saved with ID: ${it.id}")
                Toast.makeText(this, "Medication Donation submitted successfully! Drop-off scheduled.", Toast.LENGTH_LONG).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding Meds Donation: ", e)
                Toast.makeText(this, "Error submitting donation: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun clearFields() {
        donorNameEditText.text.clear()
        medicationNameEditText.text.clear()
        quantityEditText.text.clear()
        dropOffDateEditText.text.clear()
        dropOffTimeEditText.text.clear()
        // Reset the calendar to the current time after submission
        dropOffCalendar.time = Date()
    }
}