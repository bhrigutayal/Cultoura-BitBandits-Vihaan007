package com.tourismclient.cultoura.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.android.volley.VolleyError
import com.tourismclient.cultoura.R
import com.tourismclient.cultoura.databinding.ActivitySignUpBinding
import com.tourismclient.cultoura.models.User
import com.tourismclient.cultoura.network.ApiUrl
import com.tourismclient.cultoura.utils.Constants
import com.tourismclient.cultoura.utils.SharedPreferences
import com.tourismclient.cultoura.utils.VolleyRequest
import org.json.JSONObject

class SignUpActivity : BaseActivity() {
    private var binding : ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        // This is used to align the xml view to this class
        setContentView(binding?.root)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupActionBar()

        // Click event for sign-up button.
        binding!!.btnSignUp.setOnClickListener {
            registerUser()
        }
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding!!.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        }

        binding!!.toolbarSignUpActivity.setNavigationOnClickListener { backPressedCallback.handleOnBackPressed() }
    }

    /**
     * A function to register a user to our app using the Firebase.
     * For more details visit: https://firebase.google.com/docs/auth/android/custom-auth
     */
    private fun registerUser() {
        // Here we get the text from editText and trim the space
        val name: String = binding!!.etName.text.toString().trim { it <= ' ' }
        val email: String = binding!!.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding!!.etPassword.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {
            val volleyRequest = VolleyRequest()
            val jsonObject = JSONObject()
            jsonObject.put("username",email)
            jsonObject.put("password",password)
            jsonObject.put("role","USER")
            volleyRequest.makePOSTRequest(
                ApiUrl.SIGNUP,
                jsonObject,
                this@SignUpActivity,
                false
            )
            volleyRequest.setVolleyRequestListener(object : VolleyRequest.VolleyRequestListener {
                override fun onDataLoaded(jsonObject: JSONObject) {
                    userRegisteredSuccess()
                    SignInActivity().signInRegisteredUser(email,password)
                }

                override fun onError(error: VolleyError) {
                    showErrorSnackBar("Error Occurred")
                }
            })
        }
    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to be called the user is registered successfully and entry is made in the firestore database.
     */
    fun userRegisteredSuccess() {

        Toast.makeText(
            this@SignUpActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()

        // Hide the progress dialog
        hideProgressDialog()

        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */

        // Finish the Sign-Up Screen
        finish()
    }
}
