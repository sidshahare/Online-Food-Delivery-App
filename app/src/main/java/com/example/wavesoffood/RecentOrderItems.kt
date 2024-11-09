

package com.example.wavesoffood

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.adapter.RecentBuyAdapter
import com.example.wavesoffood.databinding.ActivityRecentOrderItemsBinding
import com.example.wavesoffood.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class RecentOrderItems : AppCompatActivity() {
    private val binding: ActivityRecentOrderItemsBinding by lazy {
        ActivityRecentOrderItemsBinding.inflate(layoutInflater)
    }

    private var allFoodNames: ArrayList<String> = arrayListOf()
    private var allFoodPrices: ArrayList<String> = arrayListOf()
    private var allFoodImages: ArrayList<String> = arrayListOf()
    private var allFoodQuantities: ArrayList<Int> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        val recentOrderItems = intent.getSerializableExtra("RecentBuyOrderItem") as? ArrayList<OrderDetails>
        recentOrderItems?.let { orderDetails ->

            if (orderDetails.isNotEmpty()) {
                val recentOrder: OrderDetails = orderDetails[0]

                recentOrder.foodNames?.let { names ->
                    allFoodNames = ArrayList(names)
                }

                recentOrder.foodPrices?.let { prices ->
                    allFoodPrices = ArrayList(prices)
                }

                recentOrder.foodQuantities?.let { quantities ->
                    allFoodQuantities = ArrayList(quantities)
                }

                recentOrder.foodImages?.let { images ->
                    allFoodImages = ArrayList(images)
                }
            }
        }
        setAdapter()
    }

    private fun setAdapter() {
        val rv = binding.recyclerViewRecentBuy
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = RecentBuyAdapter(this, allFoodNames, allFoodPrices, allFoodImages, allFoodQuantities)
        rv.adapter = adapter
    }
}
