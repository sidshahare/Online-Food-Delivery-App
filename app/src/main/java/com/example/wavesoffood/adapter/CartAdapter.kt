package com.example.wavesoffood.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener



class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemsPrice: MutableList<String>,
    private val cartItemsImage: MutableList<String>,
    private val cartDescription: MutableList<String>,
    private val cartIngredient: MutableList<String>,
    private val cartQuantity: MutableList<Int>

) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val auth = FirebaseAuth.getInstance()

    init {
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val cartItemNumber = cartItems.size

        itemsQuantities = IntArray(cartItemNumber) { 1 }

        cartItemsReference = database.getReference("users").child(userId).child("CartItems")

    }

    companion object {
        private var itemsQuantities: IntArray = intArrayOf()
        private lateinit var cartItemsReference: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = cartItems.size
    fun getUpdatedItemQuantity(): MutableList<Int> {
        val itemQuantity= mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity
    }

    inner class CartViewHolder(private val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            if (position >= 0 && position < cartItems.size) {
                binding.apply {
                    val quantity = itemsQuantities[position]
                    cartFoodName.text = cartItems[position]
                    cartItemPrice.text = cartItemsPrice[position]
                    cartItemQuantity.text = quantity.toString()

                    val uriString = cartItemsImage[position]
                    val uri = Uri.parse(uriString)
                    Glide.with(context).load(uri).into(cartImage)

                    minusButton.setOnClickListener {
                        decQuantity(position)
                    }
                    plusButton.setOnClickListener {
                        incQuantity(position)
                    }
                    deleteButton.setOnClickListener {
                        val itemPosition = adapterPosition
                        if (itemPosition != RecyclerView.NO_POSITION) {
                            deleteItem(itemPosition)
                        }
                    }
                }
            }
        }

        private fun decQuantity(position: Int) {
            if (itemsQuantities[position] > 1) {
                itemsQuantities[position]--
                cartQuantity[position]= itemsQuantities[position]
                binding.cartItemQuantity.text = itemsQuantities[position].toString()
            }
        }

        private fun incQuantity(position: Int) {
            if (itemsQuantities[position] < 10) {
                itemsQuantities[position]++
                cartQuantity[position]= itemsQuantities[position]
                binding.cartItemQuantity.text = itemsQuantities[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
            getUniqueKeyAtPosition(position) { uniqueKey ->
                uniqueKey?.let {
                    removeItem(position, it)
                }
            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if (position >= 0 && position < cartItems.size) { // Ensure the position is valid
                cartItemsReference.child(uniqueKey).removeValue().addOnSuccessListener {
                    cartItems.removeAt(position)
                    cartItemsPrice.removeAt(position)
                    cartItemsImage.removeAt(position)
                    cartDescription.removeAt(position)
                    cartIngredient.removeAt(position)
                    cartQuantity.removeAt(position)

                    Toast.makeText(context, "Item removed successfully", Toast.LENGTH_SHORT).show()

                    itemsQuantities = itemsQuantities.filterIndexed { index, _ -> index != position }.toIntArray()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartItems.size)
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Invalid item position", Toast.LENGTH_SHORT).show()
            }
        }


        private fun getUniqueKeyAtPosition(positionRetrieve: Int, onCompletion: (String?) -> Unit) {
            cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey: String? = null
                    snapshot.children.forEachIndexed { index, childSnapshot ->
                        if (index == positionRetrieve) {
                            uniqueKey = childSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onCompletion(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error retrieving item key", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}