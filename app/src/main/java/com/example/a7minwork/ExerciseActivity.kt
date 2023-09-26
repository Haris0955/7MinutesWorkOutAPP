package com.example.a7minwork

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minwork.databinding.ActivityExerciseBinding
import com.example.a7minwork.databinding.DialogCustomBackConfirmationBinding
import java.util.Locale

//implement TextToSpeechListener
class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    // Variable for Rest Timer and later on we will initialize it.
    private var restingTimer: CountDownTimer? = null

    // Variable for timer progress. As initial value the rest progress is set to 0. As we are about
    // to start.
    private var restingProgress = 0
    private var restTimerDuration: Long = 10

    // Variable for Exercise Timer and later on we will initialize it.
    private var exerciseTimer: CountDownTimer? = null

    // Variable for the exercise timer progress. As initial value the exercise progress
    // is set to 0. As we are about to start
    private var exerciseProgress = 0
    private var exerciseTimerDuration: Long = 30

    private var exerciseList: ArrayList<ExerciseModel>? = null // We will initialize the list later.
    private var currentExercisePosition = -1 // Current Position of Exercise.

    private var tts: TextToSpeech? =
        null // Variable for Text to Speech which will be initialized later on

    //Declaring the variable of the media player for playing a notification sound when the exercise is about to start
    private var player: MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    private var binding: ActivityExerciseBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        //then set support action bar and get toolBarExerciser using the binding variable
        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null) {
            //sets the "up" button to be displayed in the action bar.
            // The "up" button is typically used to navigate up within the app's hierarchy.
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        exerciseList = Constants.getDefaultExerciseList()

        tts = TextToSpeech(this, this)


        if (currentExercisePosition < exerciseList!!.size - 1) {
            setupRestingView()
        } else {
            Toast.makeText(
                this@ExerciseActivity, "Congratulations! You have completed the 7 minutes workout.",
                Toast.LENGTH_SHORT
            ).show()
        }

        setUpExerciseStatusRV()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        // create a binding variable
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        /*Set the screen content from a layout resource.
         The resource will be inflated, adding all top-level views to the screen.*/
        // bind to the dialog
        customDialog.setContentView(dialogBinding.root)
        //to ensure that the user clicks one of the button and that the dialog is
        //not dismissed when surrounding parts of the screen is clicked
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener {
            //We need to specify that we are finishing this activity if not the player
            // continues beeping even after the screen is not visibile
            this@ExerciseActivity.finish()
            customDialog.dismiss() // Dialog will be dismissed
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }
        //Start the dialog and display it on screen.
        customDialog.show()
    }

    private fun setUpExerciseStatusRV() {
        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter

    }

    private fun setupRestingView() {

        try {
            player = MediaPlayer.create(applicationContext, R.raw.start)
            player?.isLooping = false // Sets the player to be looping or non-looping.
            player?.start() // Starts Playback.
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding?.FLRestingView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.FLExerciseView?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE

        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition + 1].name

        if (restingTimer != null) {
            restingTimer?.cancel()
            restingProgress = 0
        }

        setRestingProgressBar()
    }

    //Function is used to set the progress of timer using the progress
    private fun setRestingProgressBar() {

        // Sets the current progress
        binding?.RestingProgressBar?.progress = restingProgress

        // Here we have started a timer of 10 seconds so the 10000 is milliseconds is 10 seconds
        // and the countdown interval is 1 second so it 1000.
        restingTimer = object : CountDownTimer(restTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                // It is increased by 1
                restingProgress++

                // Indicates progress bar progress
                binding?.RestingProgressBar?.progress = 10 - restingProgress

                // Current progress is set to text view in terms of seconds.
                binding?.tvTimer?.text = (10 - restingProgress).toString()
            }

            override fun onFinish() {
                // When the 10 seconds will complete this will be executed.
                currentExercisePosition++

                // When we are getting an updated position of exercise set that item in the list as
                // selected and notify the adapter class
                // Current Item is selected
                exerciseList!![currentExercisePosition].isSelected = true

                // Notified the current item to adapter class to reflect it into UI.
                exerciseAdapter!!.notifyDataSetChanged()

                setupExerciseView()
            }
        }.start()
    }

    private fun setupExerciseView() {

        // Here according to the view make it visible as this is Exercise View so exercise view is
        // visible and rest view is not.
        binding?.FLRestingView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.FLExerciseView?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE

        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE

        /**
         * Here firstly we will check if the timer is running and it is not null then cancel the running timer and start the new one.
         * And set the progress to the initial value which is 0.
         */
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].name)

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].image)
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].name

        setExerciseProgressBar()
    }

    /**
     * Function is used to set the progress of the timer using the progress for Exercise View for 30 Seconds
     */
    private fun setExerciseProgressBar() {

        binding?.ExerciseProgressBar?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.ExerciseProgressBar?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {

                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    exerciseList!![currentExercisePosition].isSelected =
                        false // exercise is completed so selection is set to false
                    exerciseList!![currentExercisePosition].isCompleted =
                        true // updating in the list that this exercise is completed
                    exerciseAdapter!!.notifyDataSetChanged() // Notifying the adapter class.
                    setupRestingView()
                } else {

                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

    override fun onDestroy() {

        super.onDestroy()
        //Here in the Destroy function we will reset the rest timer if it is running.
        if (restingTimer != null) {
            restingTimer?.cancel()
            restingProgress = 0
        }

        //Here in the Destroy function we will reset the exercise timer if it is running.
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        //Shutting down the Text to Speech feature when activity is destroyed

        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

        //When the activity is destroyed if the media player instance is not null then stop it
        if (player != null) {
            player!!.stop()
        }

        binding = null
    }

    //Function is used to speak the text that we pass to it.
    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_MISSING_DATA) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                Log.e("TTS", "Initialization Failed!")
            }
        }
    }
}