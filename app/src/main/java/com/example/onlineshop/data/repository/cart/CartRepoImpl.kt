package com.example.onlineshop.data.repository.cart

import android.util.Log
import com.example.onlineshop.data.modal.CartItem
import com.example.onlineshop.data.repository.authentication.UserAuthentication

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CartRepoImpl(
    private val auth: UserAuthentication,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
): CartRepo {

    private fun getCartReference(userId: String): CollectionReference {
        return db.collection("users").document(userId).collection("cart")
    }

    override suspend fun addToCart(cartItem: CartItem) {
        val userId = auth.getUid()
        getCartReference(userId).document(cartItem.productId).set(cartItem).await()
    }

    override suspend fun removeFromCart(cartItemId: String) {
        val userId = auth.getUid()
        getCartReference(userId).document(cartItemId).delete().await()
    }

    override suspend fun getCartItems(userId: String): Flow<List<CartItem>> {
        return callbackFlow {
            val listener = getCartReference(userId).addSnapshotListener { snapshot, _ ->
                val cartItems = snapshot?.toObjects(CartItem::class.java) ?: emptyList()
                trySend(cartItems).isSuccess
            }
            awaitClose { listener.remove() }
        }
    }

    override suspend fun clearCart(userId: String) {
        val cartReference = getCartReference(userId)
        val batch = db.batch()
        cartReference.get().await().documents.forEach {
            batch.delete(it.reference)
        }
        batch.commit().await()
    }

    override suspend fun updateCartItem(cartItem: CartItem) {
        val userId = auth.getUid()
        getCartReference(userId).document(cartItem.productId).set(cartItem).await()
    }
}
