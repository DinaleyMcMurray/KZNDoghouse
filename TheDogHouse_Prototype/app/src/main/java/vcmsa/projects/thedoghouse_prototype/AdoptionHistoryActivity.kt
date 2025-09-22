package vcmsa.projects.thedoghouse_prototype

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import android.text.Editable
import android.text.TextWatcher
import vcmsa.projects.thedoghouse_prototype.R

class AdoptionHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private val firestore = FirebaseFirestore.getInstance()
    private var adoptionList = mutableListOf<AdoptionHistory>()
    private lateinit var adapter: AdoptionHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adoption_history)

        recyclerView = findViewById(R.id.recycleradoptionhistory)
        searchEditText = findViewById(R.id.etSearch)

        adapter = AdoptionHistoryAdapter(this, adoptionList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadAdoptions()

        // Optional search/filter
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
