package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class AdoptionHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private val firestore = FirebaseFirestore.getInstance()
    private var adoptionList = mutableListOf<AdoptionHistory>()
    private lateinit var adapter: AdoptionHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adoption_history)

        // Apply insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Drawer + Toolbar setup
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val navView: NavigationView = findViewById(R.id.navigation_view)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // ✅ Handle navigation clicks
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dog_management -> {
                    startActivity(Intent(this, DogManagementActivity::class.java))
                }
                R.id.nav_volunteer_management -> {
                    startActivity(Intent(this, VolunteerManagementActivity::class.java))
                }
                R.id.nav_events_management -> {
                    startActivity(Intent(this, EventsManagementActivity::class.java))
                }
                R.id.nav_adoption_history -> {
                    // already here, just close drawer
                }
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // RecyclerView setup
        recyclerView = findViewById(R.id.recycleradoptionhistory)
        searchEditText = findViewById(R.id.etSearch)

        adapter = AdoptionHistoryAdapter(this, adoptionList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadAdoptions()

        // Search
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterAdoptions(s.toString())
            }
        })
    }

    private fun loadAdoptions() {
        firestore.collection("Adoptions")
            .get()
            .addOnSuccessListener { snapshot: QuerySnapshot ->
                adoptionList.clear()
                for (doc in snapshot.documents) {
                    val adoption = AdoptionHistory(
                        dogName = doc.getString("dogName") ?: "",
                        sex = doc.getString("sex") ?: "",
                        age = doc.getString("age") ?: "",
                        ownerName = doc.getString("ownerName") ?: "",
                        address = doc.getString("address") ?: "",
                        contactNumber = doc.getString("contactNumber") ?: "",
                        email = doc.getString("email") ?: "",
                        idNumber = doc.getString("idNumber") ?: ""
                    )
                    adoptionList.add(adoption)
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun filterAdoptions(query: String) {
        val filtered = adoptionList.filter {
            it.dogName.contains(query, ignoreCase = true) ||
                    it.ownerName.contains(query, ignoreCase = true)
        }
        adapter = AdoptionHistoryAdapter(this, filtered)
        recyclerView.adapter = adapter
    }
}


//package vcmsa.projects.thedoghouse_prototype
//
//import android.os.Bundle
//import android.widget.EditText
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.QuerySnapshot
//import android.text.Editable
//import android.text.TextWatcher
//import androidx.core.view.GravityCompat
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import com.google.android.material.appbar.MaterialToolbar
//import com.google.android.material.navigation.NavigationView
//import vcmsa.projects.thedoghouse_prototype.R
//
//class AdoptionHistoryActivity : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var searchEditText: EditText
//    private val firestore = FirebaseFirestore.getInstance()
//    private var adoptionList = mutableListOf<AdoptionHistory>()
//    private lateinit var adapter: AdoptionHistoryAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_adoption_history)
//
//
//        // Apply edge-to-edge insets to your root view
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        // ===== Drawer + Toolbar setup (From Ntobeko2) =====
//        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
//        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
//        val navView: NavigationView = findViewById(R.id.navigation_view)
//
//        setSupportActionBar(toolbar)
//
//        // Open drawer on nav icon or swipe
//        toolbar.setNavigationOnClickListener {
//            drawerLayout.openDrawer(GravityCompat.START)
//        }
//
//
//        recyclerView = findViewById(R.id.recycleradoptionhistory)
//        searchEditText = findViewById(R.id.etSearch)
//
//        adapter = AdoptionHistoryAdapter(this, adoptionList)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = adapter
//
//        loadAdoptions()
//
//        // Optional search/filter
//        searchEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            override fun afterTextChanged(s: Editable?) {
//                filterAdoptions(s.toString())
//            }
//        })
//    }
//
//    private fun loadAdoptions() {
//        firestore.collection("Adoptions")
//            .get()
//            .addOnSuccessListener { snapshot: QuerySnapshot ->
//                adoptionList.clear()
//                for (doc in snapshot.documents) {
//                    val adoption = AdoptionHistory(
//                        dogName = doc.getString("dogName") ?: "",
//                        sex = doc.getString("sex") ?: "",
//                        age = doc.getString("age") ?: "",
//                        ownerName = doc.getString("ownerName") ?: "",
//                        address = doc.getString("address") ?: "",
//                        contactNumber = doc.getString("contactNumber") ?: "",
//                        email = doc.getString("email") ?: "",
//                        idNumber = doc.getString("idNumber") ?: ""
//                    )
//                    adoptionList.add(adoption)
//                }
//                adapter.notifyDataSetChanged()
//            }
//    }
//
//    private fun filterAdoptions(query: String) {
//        val filtered = adoptionList.filter {
//            it.dogName.contains(query, ignoreCase = true) ||
//                    it.ownerName.contains(query, ignoreCase = true)
//        }
//        adapter = AdoptionHistoryAdapter(this, filtered)
//        recyclerView.adapter = adapter
//    }
//}
