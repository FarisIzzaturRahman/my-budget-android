package com.example.mybudget.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mybudget.data.UserRepository
import com.example.mybudget.data.model.Finance
import com.example.mybudget.ui.Resource

class MenuViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getUserEmail(): LiveData<String> {
        return userRepository.getUserEmail()
    }

    fun getFinanceData(): LiveData<Resource<Finance>> {
        return userRepository.getFinanceData()
    }

    fun updateSalary(salary: String) {
        userRepository.updateSalary(salary)
    }

    fun updateTarget(target: String) {
        userRepository.updateTarget(target)
    }
}