package com.example.mybudget.ui.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mybudget.R
import com.example.mybudget.data.model.Finance
import com.example.mybudget.ui.Resource
import com.example.mybudget.utils.setVisible
import kotlinx.android.synthetic.main.fragment_init.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class InitFragment : Fragment() {

    private val viewModel by viewModel<InitViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_init, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        subscribeObservers()
    }

    private fun initViews() {
        btnDone.setOnClickListener {
            setFinanceData()
        }
    }

    private fun subscribeObservers() {
        viewModel.getUserEmail().observe(viewLifecycleOwner, Observer {
            tvEmail.setVisible(it != null)
            tvEmail.text = it
        })

        viewModel.saveDataEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        findNavController().navigate(R.id.action_initFragment_to_menu_graph)
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

    private fun setFinanceData() {
        val salary = etSalary.text.toString()
        if (salary.isBlank()) {
            etSalary.error = "field ini tidak boleh kosong"
            etSalary.requestFocus()
            return
        }

        val target = etTarget.text.toString()
        if (target.isBlank()) {
            etTarget.error = "field ini tidak boleh kosong"
            etTarget.requestFocus()
            return
        }

        viewModel.saveFinanceData(Finance(salary, target, salary))
    }

}
