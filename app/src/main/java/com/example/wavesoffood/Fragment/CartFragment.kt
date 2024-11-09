package com.example.wavesoffood.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.CongratsBottomSheet
import com.example.wavesoffood.PayOutActivity
import com.example.wavesoffood.R
import com.example.wavesoffood.adapter.CartAdapter
import com.example.wavesoffood.databinding.CartItemBinding
import com.example.wavesoffood.databinding.FragmentCartBinding
import com.example.wavesoffood.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener





class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescriptions: MutableList<String>
    private lateinit var foodImages: MutableList<String>
    private lateinit var foodQuantity: MutableList<Int>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        retrieveCartItems()

        binding.proceedButton.setOnClickListener {
            getOrderItemDetails()
        }

        return binding.root
    }

    private fun getOrderItemDetails() {
        val orderIdRef = database.reference.child("users").child(userId).child("CartItems")
        val foodNames: MutableList<String> = mutableListOf()
        val foodPrices: MutableList<String> = mutableListOf()
        val foodDescriptions: MutableList<String> = mutableListOf()
        val foodImages: MutableList<String> = mutableListOf()
        val foodQuantity = cartAdapter.getUpdatedItemQuantity()
        val foodIngredients: MutableList<String> = mutableListOf()

        orderIdRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val orderItems = foodSnapshot.getValue(CartItems::class.java)
                    orderItems?.foodName?.let { foodNames.add(it) }
                    orderItems?.foodPrice?.let { foodPrices.add(it) }
                    orderItems?.foodDescription?.let { foodDescriptions.add(it) }
                    orderItems?.foodImage?.let { foodImages.add(it) }
                    orderItems?.foodIngredients?.let { foodIngredients.add(it) }
                }
                orderNow(foodNames, foodPrices, foodDescriptions, foodImages, foodIngredients, foodQuantity)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Order failed!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun orderNow(
        foodNames: MutableList<String>,
        foodPrices: MutableList<String>,
        foodDescriptions: MutableList<String>,
        foodImages: MutableList<String>,
        foodIngredients: MutableList<String>,
        foodQuantity: MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayOutActivity::class.java).apply {
                putStringArrayListExtra("FoodItemName", ArrayList(foodNames))
                putStringArrayListExtra("FoodItemPrice", ArrayList(foodPrices))
                putStringArrayListExtra("FoodItemDescription", ArrayList(foodDescriptions))
                putStringArrayListExtra("FoodItemIngredients", ArrayList(foodIngredients))
                putIntegerArrayListExtra("FoodItemQuantity", ArrayList(foodQuantity))
                putStringArrayListExtra("FoodItemImage", ArrayList(foodImages))
            }
            startActivity(intent)
        }
    }


    private fun retrieveCartItems() {
        val foodRef = database.getReference("users").child(userId).child("CartItems")

        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodDescriptions = mutableListOf()
        foodImages = mutableListOf()
        foodQuantity = mutableListOf()
        foodIngredients = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (isAdded && context != null) { // Ensure the fragment is attached
                    for (foodSnapshot in snapshot.children) {
                        val cartItems = foodSnapshot.getValue(CartItems::class.java)

                        cartItems?.foodName?.let { foodNames.add(it) }
                        cartItems?.foodPrice?.let { foodPrices.add(it) }
                        cartItems?.foodDescription?.let { foodDescriptions.add(it) }
                        cartItems?.foodImage?.let { foodImages.add(it) }
                        cartItems?.foodQuantity?.let { foodQuantity.add(it) }
                        cartItems?.foodIngredients?.let { foodIngredients.add(it) }
                    }
                    setAdapter()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded && context != null) { // Ensure the fragment is attached
                    Toast.makeText(context, "Data not found", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setAdapter() {
        if (isAdded && context != null) { // Ensure the fragment is attached
            cartAdapter = CartAdapter(requireContext(), foodNames, foodPrices, foodImages, foodDescriptions, foodIngredients, foodQuantity)
            binding.cartRecycleView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.cartRecycleView.adapter = cartAdapter
        }
    }

}