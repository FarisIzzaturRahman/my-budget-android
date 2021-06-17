package com.example.mybudget.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mybudget.data.model.Finance
import com.example.mybudget.data.model.Item
import com.example.mybudget.data.prefs.AppPreferenceHelper
import com.example.mybudget.ui.Resource
import com.example.mybudget.ui.auth.AuthResource
import com.example.mybudget.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository(
    private val appPreferenceHelper: AppPreferenceHelper,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private val usersRef = firestore.collection(Constants.USERS)

    fun checkUser(): LiveData<AuthResource<FirebaseUser>> {
        val currentUserLiveData: MutableLiveData<AuthResource<FirebaseUser>> = MutableLiveData()

        auth.currentUser?.let {
            currentUserLiveData.postValue(AuthResource.authenticated(it))
        } ?: kotlin.run {
            currentUserLiveData.postValue(AuthResource.notAuthenticated())
        }

        currentUserLiveData.value = auth.currentUser?.let { AuthResource.authenticated(it) }
            ?: AuthResource.notAuthenticated()

        return currentUserLiveData
    }

    fun checkFirstInit(): LiveData<Boolean> {
        val firstInitLiveData: MutableLiveData<Boolean> = MutableLiveData()
        firstInitLiveData.value = appPreferenceHelper.firstInit
        return firstInitLiveData
    }

    fun authWithGoogle(account: GoogleSignInAccount): LiveData<AuthResource<FirebaseUser>> {
        val currentUserLiveData: MutableLiveData<AuthResource<FirebaseUser>> = MutableLiveData()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        currentUserLiveData.value = AuthResource.loading(null)

        auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                currentUserLiveData.postValue(AuthResource.authenticated(authTask.result?.user))
            } else {
                currentUserLiveData.postValue(
                    AuthResource.error(
                        authTask.exception!!.message!!,
                        null
                    )
                )
            }
        }

        return currentUserLiveData
    }

    fun getUserEmail(): LiveData<String> {
        val currentUserEmailLiveData: MutableLiveData<String> = MutableLiveData()
        currentUserEmailLiveData.value = auth.currentUser?.email
        return currentUserEmailLiveData
    }

    fun updateFinanceData(finance: Finance): LiveData<Resource<Any>> {
        val financeLiveData: MutableLiveData<Resource<Any>> = MutableLiveData()
        val firebaseUser = auth.currentUser!!
        val uidRef = usersRef.document(firebaseUser.uid)

        financeLiveData.value = Resource.loading(null)

        uidRef.set(finance).addOnCompleteListener { dataTask ->
            if (dataTask.isSuccessful) {
                appPreferenceHelper.firstInit = false
                financeLiveData.postValue(Resource.success(null))
            } else {
                financeLiveData.postValue(Resource.error(dataTask.exception!!.message!!, null))
            }
        }

        return financeLiveData
    }

    fun getFinanceData(): LiveData<Resource<Finance>> {
        val financeLiveData: MutableLiveData<Resource<Finance>> = MutableLiveData()
        val firebaseUser = auth.currentUser!!
        val uidRef = usersRef.document(firebaseUser.uid)

        financeLiveData.value = Resource.loading(null)

        uidRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                financeLiveData.postValue(Resource.error(it.message!!, null))
                return@addSnapshotListener
            }

            if (documentSnapshot!!.exists()) {
                val finance = documentSnapshot.toObject(Finance::class.java)
                financeLiveData.postValue(Resource.success(finance))
            }
        }

        return financeLiveData
    }

    fun updateSalary(salary: String) {
        val firebaseUser = auth.currentUser!!
        val uidRef = usersRef.document(firebaseUser.uid)

        uidRef.update(Constants.FIELD_SALARY, salary)
    }

    fun updateTarget(target: String) {
        val firebaseUser = auth.currentUser!!
        val uidRef = usersRef.document(firebaseUser.uid)

        uidRef.update(Constants.FIELD_TARGET, target)
    }

    fun addItem(item: Item, currentBalance: Int) {
        val firebaseUser = auth.currentUser!!
        val uidRef = usersRef.document(firebaseUser.uid)

        uidRef.update(Constants.FIELD_ITEM_LIST, FieldValue.arrayUnion(item))
        uidRef.update(Constants.FIELD_BALANCE, (currentBalance - item.price!!.toInt()).toString())
    }
}