package vcmsa.projects.thedoghouse_prototype

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NewsletterActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsletterAdapter
    private lateinit var newsletterList: MutableList<NewsletterItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_newsletter)

        // Apply system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerNewsletters)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Dummy data (replace with Firebase/DB later)
        newsletterList = mutableListOf(
            NewsletterItem("August Newsletter", "Highlights of adoptions and donations", "2025-08-30"),
            NewsletterItem("July Newsletter", "Vaccination drive and volunteer spotlight", "2025-07-28"),
            NewsletterItem("June Newsletter", "Shelter upgrades and rescue stories", "2025-06-25")
        )

        adapter = NewsletterAdapter(newsletterList)
        recyclerView.adapter = adapter

        // Subscription form
      //  val etEmail = findViewById<EditText>(R.id.etNewsletterEmail)
        //val btnSubscribe = findViewById<Button>(R.id.btnSubscribe)

       // btnSubscribe.setOnClickListener {
         //   val email = etEmail.text.toString().trim()

           // if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
               // Toast.makeText(this, "Subscribed with: $email", Toast.LENGTH_LONG).show()
             //   etEmail.text.clear()
           // } else {
             //   Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_LONG).show()
            //}
       // }
    }
}
