package io.surepass.livensssdkhelper

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import io.surepass.livensssdkhelper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var livenessActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var response: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerActivityForResult()

        binding.btnGetStarted.setOnClickListener {
//            val token = binding.etApiToken.text.toString().trim()
            val token = "YOUR TOKEN"
            val env = "PREPROD"
            openActivity(env, token)
        }
    }

    private fun openActivity(env: String, token: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("token", token)
        intent.putExtra("env", env)
        intent.putExtra("videoUpload", true)
        //optional field
        intent.putExtra("videoPlayBackDisable" , false)

        livenessActivityResultLauncher.launch(intent)
    }

    private fun registerActivityForResult() {
        livenessActivityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val resultCode = result.resultCode
                val data = result.data
                if (resultCode == RESULT_OK && data != null) {
                    val livenessResponse = data.getStringExtra("response")
                    Log.e("MainActivity", "Liveness Response $livenessResponse")
                    showResponse(livenessResponse)
                }
            }
    }

    private fun showResponse(eSignResponse: String?) {
        binding.etResponse.visibility = View.VISIBLE
        binding.btnCopyButton.visibility = View.VISIBLE
        binding.etResponse.setText(eSignResponse.toString())
        response = eSignResponse.toString()
    }

    // handle the touch event
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val v = currentFocus
            // clear focus from text box
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}