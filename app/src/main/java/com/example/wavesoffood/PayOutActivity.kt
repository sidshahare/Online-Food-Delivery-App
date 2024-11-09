

package com.example.wavesoffood

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wavesoffood.databinding.ActivityPayOutBinding
import com.example.wavesoffood.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class PayOutActivity : AppCompatActivity() {
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String
    private lateinit var foodItemName: ArrayList<String>
    private lateinit var foodItemPrice: ArrayList<String>
    private lateinit var foodItemQuantity: ArrayList<Int>
    private lateinit var foodItemImage: ArrayList<String>
    private lateinit var foodItemDescription: ArrayList<String>
    private lateinit var foodItemIngredients: ArrayList<String>

    private lateinit var binding: ActivityPayOutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        userId = auth.currentUser?.uid ?: ""

        // Retrieve data from the intent
        intent?.let {
            foodItemName = it.getStringArrayListExtra("FoodItemName") ?: arrayListOf()
            foodItemPrice = it.getStringArrayListExtra("FoodItemPrice") ?: arrayListOf()
            foodItemQuantity = it.getIntegerArrayListExtra("FoodItemQuantity") ?: arrayListOf()
            foodItemImage = it.getStringArrayListExtra("FoodItemImage") ?: arrayListOf()
            foodItemDescription = it.getStringArrayListExtra("FoodItemDescription") ?: arrayListOf()
            foodItemIngredients = it.getStringArrayListExtra("FoodItemIngredients") ?: arrayListOf()
        }

        setUserData()

        totalAmount = calculateTotalAmount().toString()
        binding.totalAmount.setText("₹ $totalAmount"?:"")

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.PlaceMyOrder.setOnClickListener {
            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            phone = binding.phone.text.toString().trim()
            if (name.isBlank() || address.isBlank() || phone.isBlank()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        databaseReference.child("users").child(userId).child("OrderHistory")
            .child(orderDetails.itemPushKey!!)
            .setValue(orderDetails)
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid ?: ""
        val time = System.currentTimeMillis()
        val itemPushKey = databaseReference.child("OrderDetails").push().key
        val orderDetails = OrderDetails(
            userUid = userId,
            userName = name,
            foodNames = foodItemName,
            foodPrices = foodItemPrice,
            foodImages = foodItemImage,
            foodQuantities = foodItemQuantity,
            foodDescriptions = foodItemDescription,
            foodIngredients = foodItemIngredients,
            address = address,
            totalPrice = totalAmount,
            phoneNumber = phone,
            orderAccepted = false,
            paymentReceived = false,
            itemPushKey = itemPushKey,
            currentTime = time
        )
        val orderReference = databaseReference.child("OrderDetails").child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomSheet()
            bottomSheetDialog.show(supportFragmentManager, "Test")
            removeItemFromCart()
            addOrderToHistory(orderDetails)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to Order", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeItemFromCart() {
        val cartItemsReference = databaseReference.child("users").child(userId).child("CartItems")
        cartItemsReference.removeValue()
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (i in foodItemPrice.indices) {
            val price = foodItemPrice[i]
            val priceIntValue = price.replace("₹", "").trim().toIntOrNull() ?: 0
            val quantity = foodItemQuantity[i]
            totalAmount += priceIntValue * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
        val user = auth.currentUser
        if (user != null) {
            val userReference: DatabaseReference = databaseReference.child("users").child(userId)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userName = snapshot.child("name").getValue(String::class.java)
                        val userAddress = snapshot.child("address").getValue(String::class.java)
                        val userPhone = snapshot.child("phone").getValue(String::class.java)

                        binding.name.setText(userName)
                        binding.address.setText(userAddress)
                        binding.phone.setText(userPhone)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        }
    }
}
