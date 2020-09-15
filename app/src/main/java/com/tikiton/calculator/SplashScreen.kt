package com.tikiton.calculator

import android.os.Bundle
import android.content.Intent
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        developer.movementMethod = LinkMovementMethod.getInstance();

        // Wait 1500 ms
        logo.animate().setDuration(1500).alpha(1f).withEndAction {
            // Start app & close splash screen with transition
            val mainActivityIntent = Intent(baseContext, MainActivity::class.java);
            startActivity(mainActivityIntent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}