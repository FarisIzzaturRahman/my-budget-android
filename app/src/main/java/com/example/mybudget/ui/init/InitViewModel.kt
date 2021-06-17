package com.example.mybudget.ui.init

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.mybudget.data.UserRepository
import com.example.mybudget.data.model.Finance
import com.example.mybudget.ui.Resource

class InitViewModel(private val userRepository: UserRepository) : ViewModel() {

    val saveDataEvent: MediatorLiveData<Resource<Any>> = MediatorLiveData()

    fun getUserEmail(): LiveData<String> {
        return userRepository.getUserEmail()
    }

    fun saveFinanceData(finance: Finance) {
        saveDataEvent.addSource(userRepository.updateFinanceData(finance)) {
            saveDataEvent.postValue(it)
        }
    }
}