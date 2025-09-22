package vcmsa.projects.thedoghouse_prototype

import android.content.Intent
<<<<<<< HEAD
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
=======
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
>>>>>>> d50d9fb5fe108f7b409db4de23e2609b01338cb7
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FundsDonationsActivity : AppCompatActivity() {

    private var selectedAmount: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_funds_donations)

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

<<<<<<< HEAD
        // Find views
        val etAmount = findViewById<EditText>(R.id.editAmount)
        val btn100 = findViewById<Button>(R.id.buttonR100)
        val btn500 = findViewById<Button>(R.id.buttonR500)
        val btn1000 = findViewById<Button>(R.id.buttonR1000)

        val btnPayPal = findViewById<ImageView>(R.id.btnPayPal)
        val btnVisa = findViewById<ImageView>(R.id.btnVisa)
        val btnEft = findViewById<ImageView>(R.id.btnEft)

        val linkPayPal = findViewById<TextView>(R.id.linkPayPal)
        val linkVisa = findViewById<TextView>(R.id.linkVisa)
        val linkEft = findViewById<TextView>(R.id.linkEft)

        // ==== Amount Selection ====
        btn100.setOnClickListener {
            etAmount.setText("100")
            selectedAmount = "100"
        }

        btn500.setOnClickListener {
            etAmount.setText("500")
            selectedAmount = "500"
        }

        btn1000.setOnClickListener {
            etAmount.setText("1000")
            selectedAmount = "1000"
        }

        // ==== Payment Options ====
        btnPayPal.setOnClickListener {
            openPayment("https://www.paypal.com/pay?amount=$selectedAmount")
        }
        linkPayPal.setOnClickListener {
            openPayment("https://www.paypal.com/pay?amount=$selectedAmount")
        }

        btnVisa.setOnClickListener {
            openPayment("https://www.visa.com/pay?amount=$selectedAmount")
        }
        linkVisa.setOnClickListener {
            openPayment("https://www.visa.com/pay?amount=$selectedAmount")
        }

        btnEft.setOnClickListener {
            openPayment("https://www.bank.com/eft?amount=$selectedAmount")
        }
        linkEft.setOnClickListener {
            openPayment("https://www.bank.com/eft?amount=$selectedAmount")
        }
=======
        val DogFoodButton = findViewById<Button>(R.id.DogFoodBtn)
        val MedsButton = findViewById<Button>(R.id.MedsBtn)

        DogFoodButton.setOnClickListener {
            val intent = Intent(this, DogFoodActivity::class.java)
            startActivity(intent)
        }

        MedsButton.setOnClickListener {
            val intent = Intent(this, MedsDonationActivity::class.java)
            startActivity(intent)
        }


>>>>>>> d50d9fb5fe108f7b409db4de23e2609b01338cb7
    }

    // Open browser with payment link
    private fun openPayment(url: String) {
        if (selectedAmount.isEmpty()) {
            // fallback: let user type amount manually
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("amount=", "amount=0")))
            startActivity(intent)
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
}
