    package com.example.mybudget.data.model

data class Finance(
    var salary: String? = null,
    var target: String? = null,
    var balance: String? = null,
    var itemList: List<Item>? = ArrayList()
)