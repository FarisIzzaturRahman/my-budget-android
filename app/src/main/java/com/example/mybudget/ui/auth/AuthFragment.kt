package com.example.mybudget.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mybudget.R
import com.example.mybudget.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_auth.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class AuthFragment : Fragment() {

    private val viewModel by viewModel<AuthViewModel>()

    private val googleSignInClient: GoogleSignInClient by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subscribeObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RC_GOOGLE_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignIn(task)
            }
        }
    }

    private fun initListeners() {
        btnSignIn.setOnClickListener {
            requestGoogleSignIn()
        }
    }

    private fun requestGoogleSignIn() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, Constants.RC_GOOGLE_SIGN_IN)
    }

    private fun handleSignIn(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            viewModel.authWithGoogle(account!!)
        } catch (e: ApiException) {
            Log.e("AuthFragment", "handleSignIn: ${e.localizedMessage}", e)
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun subscribeObservers() {
        viewModel.signInEvent.observe(this, Observer {
            it?.let {
                when (it.status) {
                    AuthResource.AuthStatus.AUTHENTICATED -> {
                        viewModel.checkFirstInit()
                    }
                    AuthResource.AuthStatus.ERROR -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    AuthResource.AuthStatus.LOADING -> {
                    }
                    AuthResource.AuthStatus.NOT_AUTHENTICATED -> {

                    }
                }
            }
        })

        viewModel.checkFirstInitEvent.observe(this, Observer {
            it?.let { firstInit ->
                if (firstInit) {
                    findNavController().navigate(R.id.action_authFragment_to_init_graph)
                } else {
                    findNavController().navigate(R.id.action_authFragment_to_menu_graph)
                }
            }
        })
    }
}
