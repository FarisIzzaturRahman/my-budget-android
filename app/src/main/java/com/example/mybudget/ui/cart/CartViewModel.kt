package com.example.mybudget.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mybudget.data.UserRepository
import com.example.mybudget.data.model.Finance
import com.example.mybudget.data.model.Item
import com.example.mybudget.ui.Resource

class CartViewModel(private val userRepository: UserRepository): ViewModel() {

    fun getFinanceData(): LiveData<Resource<Finance>> {
        return userRepository.getFinanceData()
    }

    fun addItem(item: Item, currentBalance: Int) {
        userRepository.addItem(item, currentBalance)
    }
}