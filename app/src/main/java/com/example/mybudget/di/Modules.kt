package com.example.mybudget.di

import com.example.mybudget.R
import com.example.mybudget.data.UserRepository
import com.example.mybudget.data.prefs.AppPreferenceHelper
import com.example.mybudget.ui.auth.AuthViewModel
import com.example.mybudget.ui.cart.CartViewModel
import com.example.mybudget.ui.init.InitViewModel
import com.example.mybudget.ui.menu.MenuViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppPreferenceHelper(androidApplication()) }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { UserRepository(get(), get(), get()) }
}

val authModule = module {
    single<GoogleSignInOptions> {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(androidApplication().getString(R.string.default_web_client_id))
            .requestProfile()
            .requestEmail()
            .build()
    }
    single { GoogleSignIn.getClient(androidApplication(), get()) }
    viewModel { AuthViewModel(get()) }
}

val initModule = module {
    viewModel { InitViewModel(get()) }
}

val menuModule = module {
    viewModel { MenuViewModel(get()) }
}

val cartModule = module {
    viewModel { CartViewModel(get()) }
}

