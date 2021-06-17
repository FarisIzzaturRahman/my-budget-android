package com.example.mybudget.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mybudget.R
import com.example.mybudget.ui.Resource
import com.example.mybudget.utils.setVisible
import kotlinx.android.synthetic.main.dialog_menu_input.view.*
import kotlinx.android.synthetic.main.fragment_menu.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class MenuFragment : Fragment() {

    private val viewModel by viewModel<MenuViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListeners()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.getUserEmail().observe(viewLifecycleOwner, Observer {
            tvEmail.setVisible(it != null)
            tvEmail.text = it
        })

        viewModel.getFinanceData().observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        val data = it.data!!
                        tvSalary.text = getString(R.string.money_format, data.salary)
                        tvTarget.text = getString(R.string.money_format, data.target)
                    }
                    Resource.Status.ERROR -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.LOADING -> {
                    }
                }
            }
        })
    }

    private fun initViews() {
        btnCart.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_cart_graph)
        }
    }

    private fun initListeners() {
        llSalary.setOnClickListener {
            showInputDialog(true)
        }

        llTarget.setOnClickListener {
            showInputDialog(false)
        }
    }

    private fun showInputDialog(isSalary: Boolean) {
        val view = View.inflate(requireContext(), R.layout.dialog_menu_input, null)
        view.tvTitle.text =
            if (isSalary) "Ubah gaji per bulan" else "Ubah target pengeluaran"

        AlertDialog.Builder(requireContext())
            .setView(view)
            .setPositiveButton("Ubah") { dialog, which ->
                val value = view.etInput.text.toString()
                if (isSalary) viewModel.updateSalary(value) else viewModel.updateTarget(value)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, which ->
                dialog.cancel()
            }
            .show()
    }
}
