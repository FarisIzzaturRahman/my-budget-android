package com.example.mybudget.ui.cart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mybudget.R
import com.example.mybudget.data.model.Item
import com.example.mybudget.data.model.Time
import com.example.mybudget.ui.Resource
import com.example.mybudget.utils.CommonUtils
import kotlinx.android.synthetic.main.dialog_cart_input.view.*
import kotlinx.android.synthetic.main.fragment_cart.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class CartFragment : Fragment() {

    private val viewModel by viewModel<CartViewModel>()
    private val itemAdapter: ItemAdapter by lazy {
        ItemAdapter()
    }
    private val monthAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.months)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        iniViews()
        subscribeObservers()
    }

    private fun iniViews() {
        rvItem.adapter = itemAdapter
        rvItem.setHasFixedSize(true)

        spinnerMonth.adapter = monthAdapter

        val currentTime = CommonUtils.getCurrentTimeInIndo()
        spinnerMonth.setSelection(monthAdapter.getPosition(currentTime.month), false)
        itemAdapter.filterItems(currentTime)
    }

    private fun initListeners() {
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        fabAdd.setOnClickListener {
            showInputDialog()
        }

        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent?.getChildAt(0) as? TextView)?.setTextColor(Color.WHITE)
                itemAdapter.filterItems(Time(CommonUtils.getCurrentTimeInIndo().year, monthAdapter.getItem(position)))
            }

        }
    }

    private fun showInputDialog() {
        val view = View.inflate(requireContext(), R.layout.dialog_cart_input, null)

        AlertDialog.Builder(requireContext())
            .setView(view)
            .setPositiveButton("Tambah") { dialog, which ->
                val name = view.etName.text.toString()
                val price = view.etPrice.text.toString()

                viewModel.addItem(
                    Item(name, price, CommonUtils.getCurrentTimeInIndo()),
                    tvBalance.text.toString().replace("IDR ", "").toInt()
                )
            }
            .setNegativeButton("Batal") { dialog, which ->
                dialog.cancel()
            }
            .show()
    }

    private fun subscribeObservers() {
        viewModel.getFinanceData().observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        val data = it.data!!
                        tvBalance.text = getString(R.string.money_format, data.balance)

                        data.itemList?.let { itemList ->
                            itemAdapter.submitAllList(itemList)
                        }
                    }
                    Resource.Status.ERROR -> {
                    }
                    Resource.Status.LOADING -> {
                    }
                }
            }
        })
    }
}
