package com.example.camperpowerdistribution

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.ColorUtils

class PopUpMenu : AppCompatActivity() {

    private var numComponents = 0
    private var numChecks = 0
    var components = HashMap<String, Double>(15)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0,0)
        setContentView(R.layout.activity_pop_up_menu)

        var componentList = findViewById<LinearLayout>(R.id.componentList)


        val bundle = intent.extras
        numComponents = bundle!!.getInt("numComponents")
        while(numComponents > 0){
            numComponents--
            var titleKey = "cT$numComponents"
            var usedKey = "cU$numComponents"

            components[bundle!!.getString(titleKey) as String] = bundle!!.getDouble(usedKey)
        }
        var templateText = findViewById<TextView>(R.id.templateText)
        var templateLayout = findViewById<LinearLayout>(R.id.templateLayout)
        var templateInput = findViewById<EditText>(R.id.templateInput)

        for(k in components){

            var newLayout = LinearLayout(this)
            componentList.addView(newLayout)
            newLayout.layoutParams = templateLayout.layoutParams
            newLayout.gravity = templateLayout.gravity
            newLayout.background = templateLayout.background
            newLayout.orientation = templateLayout.orientation

            var newText = TextView(this)
            newText.setText(k.key)
            newText.textSize = 18f
            newText.gravity = templateText.gravity
            newText.textAlignment = View.TEXT_ALIGNMENT_CENTER
            newText.setTextColor(Color.parseColor("#000000"))
            newText.layoutParams = templateText.layoutParams
            newText.visibility = View.VISIBLE
            newLayout.addView(newText)

            //componentList.addView(newText)

            var newInput = EditText(this)
            newLayout.addView(newInput)
            newInput.layoutParams = templateInput.layoutParams
            newInput.gravity = Gravity.CENTER
            newInput.setText(components[k.key].toString())
            newInput.background = templateInput.background





            newLayout.visibility = View.VISIBLE
        }


        // Set the Status bar appearance for different API levels
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(this, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.window.statusBarColor = Color.TRANSPARENT
                setWindowFlag(this, false)
            }
        }

        val popup_window_background = findViewById<View>(R.id.popup_window_background)

        // Fade animation for the background of Popup Window
        val alpha = 100 //between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 250 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()



        val popup_window_view_with_border = findViewById<View>(R.id.popup_window_view_with_border)

        // Fade animation for the Popup Window
        popup_window_view_with_border.alpha = 0f
        popup_window_view_with_border.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

    }

    override fun onBackPressed() {
        val popup_window_background = findViewById<View>(R.id.popup_window_background)
        val popup_window_view_with_border = findViewById<View>(R.id.popup_window_view_with_border)


        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 250 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(
                animator.animatedValue as Int
            )
        }



        // Fade animation for the Popup Window when you press the back button
        popup_window_view_with_border.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }

    private fun setWindowFlag(activity: Activity, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        } else {
            winParams.flags = winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        }
        win.attributes = winParams
    }

    fun okClicked(view: View){
        val popup_window_background = findViewById<View>(R.id.popup_window_background)
        val popup_window_view_with_border = findViewById<View>(R.id.popup_window_view_with_border)


        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 250 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(
                animator.animatedValue as Int
            )
        }



        // Fade animation for the Popup Window when you press the back button
        popup_window_view_with_border.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }

    fun backClicked(view: View){
        val popup_window_background = findViewById<View>(R.id.popup_window_background)
        val popup_window_view_with_border = findViewById<View>(R.id.popup_window_view_with_border)


        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 250 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(
                animator.animatedValue as Int
            )
        }



        // Fade animation for the Popup Window when you press the back button
        popup_window_view_with_border.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }
}