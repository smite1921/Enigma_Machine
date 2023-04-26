package com.smitpatel.enigmamachine.ui.main

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smitpatel.enigmamachine.R
import com.smitpatel.enigmamachine.databinding.ActivityEnigmaMainBinding
import com.smitpatel.enigmamachine.ui.EnigmaSounds
import com.smitpatel.enigmamachine.ui.SoundEffects
import com.smitpatel.enigmamachine.ui.setting.SettingsFragment
import com.smitpatel.enigmamachine.events.EnigmaEvent
import com.smitpatel.enigmamachine.models.Rotor
import com.smitpatel.enigmamachine.ui.RotorPosition
import com.smitpatel.enigmamachine.viewmodels.EnigmaViewModel

class EnigmaMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnigmaMainBinding

    private val viewModel : EnigmaViewModel by viewModels()

    private val sounds : SoundEffects = SoundEffects
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnigmaMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        setupRenderer()
        lifecycle.addObserver(sounds)
        SoundEffects.lifecycle = lifecycle
        SoundEffects.initialize(context = applicationContext)
    }

    private fun setupRenderer() {
        fun getRotorLabelText(rotor: Rotor.RotorOption) = when (rotor) {
            Rotor.RotorOption.ROTOR_ONE -> "I"
            Rotor.RotorOption.ROTOR_TWO -> "II"
            Rotor.RotorOption.ROTOR_THREE -> "III"
            Rotor.RotorOption.ROTOR_FOUR -> "IV"
            Rotor.RotorOption.ROTOR_FIVE -> "V"
        }
        viewModel.uiState.observe(this) {
            binding.rotors.rotor1.value = it.rotorOnePosition + 1
            binding.rotors.rotor2.value = it.rotorTwoPosition + 1
            binding.rotors.rotor3.value = it.rotorThreePosition + 1

            binding.rotors.rotor1Label.text = getRotorLabelText(it.rotorOneLabel)
            binding.rotors.rotor2Label.text = getRotorLabelText(it.rotorTwoLabel)
            binding.rotors.rotor3Label.text = getRotorLabelText(it.rotorThreeLabel)

            binding.textboxes.textRaw.text = it.rawMessage
            binding.textboxes.textCode.text = it.encodedMessage

            binding.textboxes.scrollview.fullScroll(View.FOCUS_RIGHT)

            val lamps = arrayOf(
                binding.lampboard.lampA, binding.lampboard.lampB,
                binding.lampboard.lampC, binding.lampboard.lampD, binding.lampboard.lampE,
                binding.lampboard.lampF, binding.lampboard.lampG, binding.lampboard.lampH,
                binding.lampboard.lampI, binding.lampboard.lampJ, binding.lampboard.lampK,
                binding.lampboard.lampL, binding.lampboard.lampM, binding.lampboard.lampN,
                binding.lampboard.lampO, binding.lampboard.lampP, binding.lampboard.lampQ,
                binding.lampboard.lampR, binding.lampboard.lampS, binding.lampboard.lampT,
                binding.lampboard.lampU, binding.lampboard.lampV, binding.lampboard.lampW,
                binding.lampboard.lampX, binding.lampboard.lampY, binding.lampboard.lampZ,
            )

            lamps.forEach { lamp ->
                lamp.isPressed = false
            }

            if (it.activeLampboard != -1) {
                lamps[it.activeLampboard].isPressed = true
            }

            if (it.showSettingsChangedToast) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.settings_changed_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
                sounds.playSound(sound = EnigmaSounds.CHANGES)
                viewModel.handleEvent(
                    event = EnigmaEvent.ToastMessageDisplayed
                )
            }
        }
    }

    private fun setupViews() {

        binding.rotors.rotor1.displayedValues = resources.getStringArray(R.array.rotor_values)
        binding.rotors.rotor2.displayedValues = resources.getStringArray(R.array.rotor_values)
        binding.rotors.rotor3.displayedValues = resources.getStringArray(R.array.rotor_values)

        binding.rotors.rotor1.setOnValueChangedListener { _, _, newValue ->
            SoundEffects.playSound(sound = EnigmaSounds.ROTOR)
            viewModel.handleEvent(
                event = EnigmaEvent.RotorStartPositionChanged(
                    start = newValue - 1,
                    rotorPosition = RotorPosition.ONE
                )
            )
        }

        binding.rotors.rotor2.setOnValueChangedListener { _, _, newValue ->
            SoundEffects.playSound(sound = EnigmaSounds.ROTOR)
            viewModel.handleEvent(
                event = EnigmaEvent.RotorStartPositionChanged(
                    start = newValue - 1,
                    rotorPosition = RotorPosition.TWO
                )
            )
        }

        binding.rotors.rotor3.setOnValueChangedListener { _, _, newValue ->
            SoundEffects.playSound(sound = EnigmaSounds.ROTOR)
            viewModel.handleEvent(
                event = EnigmaEvent.RotorStartPositionChanged(
                    start = newValue - 1,
                    rotorPosition = RotorPosition.THREE
                )
            )
        }

        binding.rotors.powerSwitch.setOnClickListener {
            SoundEffects.playSound(sound = EnigmaSounds.DEFAULT)
            SettingsFragment().show(supportFragmentManager, "")
        }

        val buttons = arrayOf(
            binding.keyboard.buttonA, binding.keyboard.buttonB, binding.keyboard.buttonC,
            binding.keyboard.buttonD, binding.keyboard.buttonE, binding.keyboard.buttonF,
            binding.keyboard.buttonG, binding.keyboard.buttonH, binding.keyboard.buttonI,
            binding.keyboard.buttonJ, binding.keyboard.buttonK, binding.keyboard.buttonL,
            binding.keyboard.buttonM, binding.keyboard.buttonN, binding.keyboard.buttonO,
            binding.keyboard.buttonP, binding.keyboard.buttonQ, binding.keyboard.buttonR,
            binding.keyboard.buttonS, binding.keyboard.buttonT, binding.keyboard.buttonU,
            binding.keyboard.buttonV, binding.keyboard.buttonW, binding.keyboard.buttonX,
            binding.keyboard.buttonY, binding.keyboard.buttonZ, binding.keyboard.buttonDelete,
            binding.keyboard.buttonSpacebar
        )

        buttons.forEach { button ->
            button.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        button.isPressed = true
                        button.performClick()
                        when (button.id) {
                            R.id.button_delete -> {
                                SoundEffects.playSound(sound = EnigmaSounds.DELETE)
                                viewModel.handleEvent(
                                    event = EnigmaEvent.InputDeletePressed
                                )
                            }
                            R.id.button_spacebar -> {
                                SoundEffects.playSound(sound = EnigmaSounds.SPACE)
                                viewModel.handleEvent(
                                    event = EnigmaEvent.InputSpacePressed
                                )
                            }
                            else -> {
                                SoundEffects.playSound(sound = EnigmaSounds.KEY)
                                viewModel.handleEvent(
                                    event = EnigmaEvent.InputKeyPressed(
                                        input = letterToNumber(button.text[0])
                                    )
                                )
                            }
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        button.isPressed = false
                        if ((button.id != R.id.button_delete) && (button.id !=R.id.button_spacebar)) {
                            viewModel.handleEvent(
                                EnigmaEvent.InputKeyLifted(
                                    input = letterToNumber(button.text[0])
                                )
                            )
                        }
                    }
                }
                true
            }
        }
    }

    private fun letterToNumber(letter: Char) = letter.code - 65

}