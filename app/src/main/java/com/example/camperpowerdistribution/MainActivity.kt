package com.example.camperpowerdistribution

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.FlexboxLayout
import org.w3c.dom.Text
import java.io.*


class MainActivity : AppCompatActivity() {

    var genericComponents = mutableListOf<TextView>()
    var components = HashMap<TextView, ElectronicComponent>(30)
    var colors = HashMap<View, Int>(15)
    var circuits = HashMap<FlexboxLayout, AmpUsage>(8)
    var dupedComponents = mutableListOf<TextView>()
    var numDupedItems = 0
    var main1Cap = 30.0
    var main2Cap = 30.0
    var main1Used = 0.0
    var main2Used = 0.0
    var lastCustomMBR = 0.0
    var lastCustomRefer = 0.0
    var lastCustomGFI = 0.0
    var lastCustomMicro = 0.0
    var lastCustomWaterHeater = 0.0
    var lastCustomAC = 0.0
    var lastCustomConverter = 0.0
    var file: File = File("invalidPath")
    var main1CapColor: Drawable = ColorDrawable(0xFFFFFF)
    var main2CapColor: Drawable = ColorDrawable(0xFFFFFF)
    var mbrCapColor: Drawable = ColorDrawable(0xFFFFFF)
    var referCapColor: Drawable = ColorDrawable(0xFFFFFF)
    var gfiCapColor: Drawable = ColorDrawable(0xFFFFFF)
    var microCapColor: Drawable = ColorDrawable(0xFFFFFF)
    var waterHeaterCapColor: Drawable = ColorDrawable(0xFFFFFF)
    var acCapColor: Drawable = ColorDrawable(0xFFFFFF)
    var converterCapColor: Drawable = ColorDrawable(0xFFFFFF)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        file = File(this.filesDir, "saved_state.txt")
        file.createNewFile()


        //Non-Droppable Views
        val main2Converter: FlexboxLayout = findViewById(R.id.main2Converter)
        val main2AC: FlexboxLayout = findViewById(R.id.main2AC)
        val main1Micro: FlexboxLayout = findViewById(R.id.main1Micro)
        val main2WaterHeater: FlexboxLayout = findViewById(R.id.main2WaterHeater)

        var main1AmpUsageM: TextView = findViewById(R.id.mainC1Cap)
        val main2AmpUsageM: TextView = findViewById(R.id.mainC2Cap)
        var powerCheck: CheckBox = findViewById(R.id.powerCheck)
        var mainCircuit1Title: TextView = findViewById(R.id.mainTitle1)
        var mainCircuit2Title: TextView = findViewById(R.id.mainTitle2)

        var circuitMBRCap: TextView = findViewById(R.id.MBRUsage)
        var circuitReferCap: TextView = findViewById(R.id.referUsage)
        var circuitGFICap: TextView = findViewById(R.id.GFIUsage)
        var circuitMicroCap: TextView = findViewById(R.id.microUsage)
        var circuitWaterHeaterCap: TextView = findViewById(R.id.waterHeaterUsage)
        var circuitACCap: TextView = findViewById((R.id.ACUsage))
        var circuitConverterCap: TextView = findViewById(R.id.converterUsage)

        main1CapColor = main1AmpUsageM.background
        main2CapColor = main2AmpUsageM.background
        mbrCapColor = circuitMBRCap.background
        referCapColor = circuitReferCap.background
        gfiCapColor = circuitGFICap.background
        microCapColor = circuitMicroCap.background
        waterHeaterCapColor = circuitWaterHeaterCap.background
        acCapColor = circuitACCap.background
        converterCapColor = circuitConverterCap.background

        var customInputMBR: EditText = findViewById(R.id.MBRCustomInputField)
        var customInputRefer: EditText = findViewById(R.id.referCustomInputField)
        var customInputGFI: EditText = findViewById(R.id.GFICustomInputField)
        var customInputMicro: EditText = findViewById(R.id.microCustomInputField)
        var customInputWaterHeater: EditText = findViewById(R.id.waterHeaterCustomInputField)
        var customInputAC: EditText = findViewById(R.id.ACCustomInputField)
        var customInputConverter: EditText = findViewById(R.id.converterCustomInputField)



        //Droppable Views
        var source: FlexboxLayout = findViewById(R.id.source)
        var main1MBR: FlexboxLayout = findViewById(R.id.main1MBR)
        var main1Refer: FlexboxLayout = findViewById(R.id.main1Refer)
        val main1GFI: FlexboxLayout = findViewById(R.id.main1GFI)

        colors[main1AmpUsageM] = (main1AmpUsageM.background as ColorDrawable).color
        colors[main2AmpUsageM] = (main2AmpUsageM.background as ColorDrawable).color
        colors[main1MBR] = (main1MBR.background as ColorDrawable).color
        colors[main1Refer] = (main1Refer.background as ColorDrawable).color
        colors[main1GFI] = (main1GFI.background as ColorDrawable).color
        colors[main1Micro] = (main1Micro.background as ColorDrawable).color
        colors[main2WaterHeater] = (main2WaterHeater.background as ColorDrawable).color
        colors[main2AC] = (main2AC.background as ColorDrawable).color
        colors[main2Converter] = (main2Converter.background as ColorDrawable).color

        circuits[main1MBR] = AmpUsage(15.0, 0.0, circuitMBRCap)
        circuits[main1Refer] = AmpUsage(15.0, 0.0, circuitReferCap)
        circuits[main1GFI] = AmpUsage(15.0, 0.0, circuitGFICap)
        circuits[main1Micro] = AmpUsage(15.0, 0.0, circuitMicroCap)
        circuits[main2WaterHeater] = AmpUsage(15.0, 0.0, circuitWaterHeaterCap)
        circuits[main2AC] = AmpUsage(20.0, 0.0, circuitACCap)
        circuits[main2Converter] = AmpUsage(15.0, 0.0, circuitConverterCap)


        components[findViewById(R.id.toaster)] = ElectronicComponent(12.6)
        components[findViewById(R.id.kettle)] = ElectronicComponent(12.5)
        components[findViewById(R.id.towHeatHigh)] = ElectronicComponent(11.3)
        components[findViewById(R.id.towHeatLow)] = ElectronicComponent(9.2)
        components[findViewById(R.id.vacuum)] = ElectronicComponent(3.9)
        components[findViewById(R.id.workComp)] = ElectronicComponent(2.5)
        components[findViewById(R.id.laptop)] = ElectronicComponent(1.5)
        components[findViewById(R.id.compMon)] = ElectronicComponent(1.5)
        components[findViewById(R.id.vornadoHeatFan)] = ElectronicComponent(0.5)
        components[findViewById(R.id.vornadoHeatLow)] = ElectronicComponent(9.2)
        components[findViewById(R.id.vornadoHeatHigh)] = ElectronicComponent(12.0)
        components[findViewById(R.id.vornadoFanLow)] = ElectronicComponent(0.3)
        components[findViewById(R.id.vornadoFanMed)] = ElectronicComponent(0.5)
        components[findViewById(R.id.vornadoFanHigh)] = ElectronicComponent(0.8)
        //components[findViewById(R.id.tv)] = ElectronicComponent(1.5)
        //components[findViewById(R.id.batChar)] = ElectronicComponent(6.0)
        //components[findViewById(R.id.tankHeat)] = ElectronicComponent(2.0)
        //components[findViewById(R.id.refrigerator)] = ElectronicComponent(2.0)

        //Generic power consumers
        genericComponents.add(0, findViewById(R.id.laptop))
        /*
        genericComponents.add(0, findViewById(R.id.g1))
        components[findViewById(R.id.g1)] = ElectronicComponent(1.0)
        genericComponents.add(1, findViewById(R.id.g2))
        components[findViewById(R.id.g2)] = ElectronicComponent(2.0)
        genericComponents.add(2, findViewById(R.id.g3))
        components[findViewById(R.id.g3)] = ElectronicComponent(3.0)
        genericComponents.add(3, findViewById(R.id.g4))
        components[findViewById(R.id.g4)] = ElectronicComponent(4.0)
        genericComponents.add(4, findViewById(R.id.g5))
        components[findViewById(R.id.g5)] = ElectronicComponent(5.0)
        genericComponents.add(5, findViewById(R.id.g6))
        components[findViewById(R.id.g6)] = ElectronicComponent(6.0)
        genericComponents.add(6, findViewById(R.id.g7))
        components[findViewById(R.id.g7)] = ElectronicComponent(7.0)
        genericComponents.add(7, findViewById(R.id.g8))
        components[findViewById(R.id.g8)] = ElectronicComponent(8.0)
        genericComponents.add(8, findViewById(R.id.g9))
        components[findViewById(R.id.g9)] = ElectronicComponent(9.0)
        genericComponents.add(9, findViewById(R.id.g10))
        components[findViewById(R.id.g10)] = ElectronicComponent(10.0)
        genericComponents.add(10, findViewById(R.id.g11))
        components[findViewById(R.id.g11)] = ElectronicComponent(11.0)
        genericComponents.add(11, findViewById(R.id.g12))
        components[findViewById(R.id.g12)] = ElectronicComponent(12.0)
        genericComponents.add(12, findViewById(R.id.g13))
        components[findViewById(R.id.g13)] = ElectronicComponent(13.0)
        genericComponents.add(13, findViewById(R.id.g14))
        components[findViewById(R.id.g14)] = ElectronicComponent(14.0)
        genericComponents.add(14, findViewById(R.id.g15))
        components[findViewById(R.id.g15)] = ElectronicComponent(15.0)
        */

        //Non-Droppable
        main2Converter.setOnDragListener(inertDragListener)
        main2AC.setOnDragListener(inertDragListener)
        main1Micro.setOnDragListener(inertDragListener)
        main2WaterHeater.setOnDragListener(inertDragListener)

        mainCircuit1Title.setOnDragListener(inertDragListener)
        mainCircuit2Title.setOnDragListener(inertDragListener)
        powerCheck.setOnDragListener(inertDragListener)
        main1AmpUsageM.setOnDragListener(inertDragListener)
        main2AmpUsageM.setOnDragListener(inertDragListener)

        findViewById<TextView>(R.id.MBRTitle).setOnDragListener(inertDragListener)
        circuitMBRCap.setOnDragListener(inertDragListener)
        findViewById<TextView>(R.id.referTitle).setOnDragListener(inertDragListener)
        circuitReferCap.setOnDragListener(inertDragListener)
        findViewById<TextView>(R.id.GFITitle).setOnDragListener(inertDragListener)
        circuitGFICap.setOnDragListener(inertDragListener)
        findViewById<TextView>(R.id.microTitle).setOnDragListener(inertDragListener)
        circuitMicroCap.setOnDragListener(inertDragListener)
        findViewById<TextView>(R.id.waterHeaterTitle).setOnDragListener(inertDragListener)
        circuitWaterHeaterCap.setOnDragListener(inertDragListener)
        findViewById<TextView>(R.id.ACTitle).setOnDragListener(inertDragListener)
        circuitACCap.setOnDragListener(inertDragListener)
        findViewById<TextView>(R.id.converterTitle).setOnDragListener(inertDragListener)
        circuitConverterCap.setOnDragListener(inertDragListener)

        customInputMBR.setOnDragListener(inertDragListener)
        customInputRefer.setOnDragListener(inertDragListener)
        customInputGFI.setOnDragListener(inertDragListener)
        customInputMicro.setOnDragListener(inertDragListener)
        customInputWaterHeater.setOnDragListener(inertDragListener)
        customInputAC.setOnDragListener(inertDragListener)
        customInputConverter.setOnDragListener(inertDragListener)

        this.findViewById<View>(android.R.id.content).rootView.setOnDragListener(inertDragListener)

        //Droppable
        source.setOnDragListener(dragListener)
        findViewById<View>(R.id.sourceScroll).setOnDragListener(sourceScrollListener)
        main1MBR.setOnDragListener(dragListener)
        main1Refer.setOnDragListener(dragListener)
        main1GFI.setOnDragListener(dragListener)


        for(i in components){
            i.key.setOnLongClickListener{
                val clipText = "This is our ClipData text"
                val item = ClipData.Item(clipText)
                val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                val data = ClipData(clipText, mimeTypes, item)

                val dragShadowBuilder = View.DragShadowBuilder(it)
                it.startDragAndDrop(data, dragShadowBuilder, it, 0)

                //it.visibility = View.INVISIBLE
                true
            }
        }

        //Custom Load Inputs
        customInputMBR.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var customInput: Double = 0.0

                if (customInputMBR.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputMBR.text.toString().toDouble()
                }
                circuits[main1MBR]!!.ampsUsed -= (lastCustomMBR - customInput)
                main1Used -= (lastCustomMBR - customInput)
                lastCustomMBR = customInput
                if (circuits[main1MBR]!!.ampsUsed <= circuits[main1MBR]!!.ampCapacity) {
                    circuits[main1MBR]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main1MBR]!!.ampsUsed) + "/" +
                            circuits[main1MBR]!!.ampCapacity.toString()
                    circuits[main1MBR]!!.userInterface.setTextColor(Color.parseColor("#000000"))

                } else {
                    circuits[main1MBR]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main1MBR]!!.ampsUsed) + "/" +
                            circuits[main1MBR]!!.ampCapacity.toString() + " OVERLOAD!"
                    blink(main1MBR)

                    circuits[main1MBR]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))


                }

                if (main1Used < main1Cap) {
                    main1AmpUsageM.text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                            main1Cap.toString()
                    main1AmpUsageM.setTextColor(Color.parseColor("#000000"))
                } else {
                    main1AmpUsageM.text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                            main1Cap.toString() + " OVERLOAD!"
                    blink(main1AmpUsageM)

                    main1AmpUsageM.setTextColor(Color.parseColor("#FF0000"))
                }


            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        customInputRefer.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var customInput: Double = 0.0

                if (customInputRefer.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputRefer.text.toString().toDouble()
                }
                circuits[main1Refer]!!.ampsUsed -= (lastCustomRefer - customInput)
                main1Used -= (lastCustomRefer - customInput)
                lastCustomRefer = customInput
                if (circuits[main1Refer]!!.ampsUsed <= circuits[main1Refer]!!.ampCapacity) {
                    circuits[main1Refer]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main1Refer]!!.ampsUsed) + "/" +
                            circuits[main1Refer]!!.ampCapacity.toString()
                    circuits[main1Refer]!!.userInterface.setTextColor(Color.parseColor("#000000"))

                } else {
                    circuits[main1Refer]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main1Refer]!!.ampsUsed) + "/" +
                            circuits[main1Refer]!!.ampCapacity.toString() + " OVERLOAD!"

                    circuits[main1Refer]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))


                }

                if (main1Used < main1Cap) {
                    main1AmpUsageM.text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                            main1Cap.toString()
                    main1AmpUsageM.setTextColor(Color.parseColor("#000000"))
                } else {
                    main1AmpUsageM.text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                            main1Cap.toString() + " OVERLOAD!"
                    blink(main1AmpUsageM)
                    main1AmpUsageM.setTextColor(Color.parseColor("#FF0000"))
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        customInputGFI.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var customInput: Double = 0.0

                if (customInputGFI.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputGFI.text.toString().toDouble()
                }
                circuits[main1GFI]!!.ampsUsed -= (lastCustomGFI - customInput)
                main1Used -= (lastCustomGFI - customInput)
                lastCustomGFI = customInput
                if (circuits[main1GFI]!!.ampsUsed <= circuits[main1GFI]!!.ampCapacity) {
                    circuits[main1GFI]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main1GFI]!!.ampsUsed) + "/" +
                            circuits[main1GFI]!!.ampCapacity.toString()
                    circuits[main1GFI]!!.userInterface.setTextColor(Color.parseColor("#000000"))

                } else {
                    circuits[main1GFI]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main1GFI]!!.ampsUsed) + "/" +
                            circuits[main1GFI]!!.ampCapacity.toString() + " OVERLOAD!"
                    blink(main1GFI)

                    circuits[main1GFI]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))


                }

                if (main1Used < main1Cap) {
                    main1AmpUsageM.text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                            main1Cap.toString()
                    main1AmpUsageM.setTextColor(Color.parseColor("#000000"))
                } else {
                    main1AmpUsageM.text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                            main1Cap.toString() + " OVERLOAD!"
                    blink(main1AmpUsageM)

                    main1AmpUsageM.setTextColor(Color.parseColor("#FF0000"))
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        customInputMicro.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var customInput: Double = 0.0

                if (customInputMicro.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputMicro.text.toString().toDouble()
                }
                circuits[main1Micro]!!.ampsUsed -= (lastCustomMicro - customInput)
                main1Used -= (lastCustomMicro - customInput)
                lastCustomMicro = customInput
                if (circuits[main1Micro]!!.ampsUsed <= circuits[main1Micro]!!.ampCapacity) {
                    circuits[main1Micro]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main1Micro]!!.ampsUsed) + "/" +
                            circuits[main1Micro]!!.ampCapacity.toString()
                    circuits[main1Micro]!!.userInterface.setTextColor(Color.parseColor("#000000"))

                } else {
                    circuits[main1Micro]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main1Micro]!!.ampsUsed) + "/" +
                            circuits[main1Micro]!!.ampCapacity.toString() + " OVERLOAD!"
                    blink(main1Micro)

                    circuits[main1Micro]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))


                }

                if (main1Used < main1Cap) {
                    main1AmpUsageM.text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                            main1Cap.toString()
                    main1AmpUsageM.setTextColor(Color.parseColor("#000000"))
                } else {
                    main1AmpUsageM.text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                            main1Cap.toString() + " OVERLOAD!"
                    blink(main1AmpUsageM)

                    main1AmpUsageM.setTextColor(Color.parseColor("#FF0000"))
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        customInputWaterHeater.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var customInput: Double = 0.0

                if (customInputWaterHeater.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputWaterHeater.text.toString().toDouble()
                }
                circuits[main2WaterHeater]!!.ampsUsed -= (lastCustomWaterHeater - customInput)
                main2Used -= (lastCustomWaterHeater - customInput)
                lastCustomWaterHeater = customInput
                if (circuits[main2WaterHeater]!!.ampsUsed <= circuits[main2WaterHeater]!!.ampCapacity) {
                    circuits[main2WaterHeater]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main2WaterHeater]!!.ampsUsed) + "/" +
                            circuits[main2WaterHeater]!!.ampCapacity.toString()
                    circuits[main2WaterHeater]!!.userInterface.setTextColor(Color.parseColor("#000000"))

                } else {
                    circuits[main2WaterHeater]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main2WaterHeater]!!.ampsUsed) + "/" +
                            circuits[main2WaterHeater]!!.ampCapacity.toString() + " OVERLOAD!"
                    blink(main2WaterHeater)

                    circuits[main2WaterHeater]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))


                }

                if (main2Used < main2Cap) {
                    main2AmpUsageM.text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                            main2Cap.toString()
                    main2AmpUsageM.setTextColor(Color.parseColor("#000000"))
                } else {
                    main2AmpUsageM.text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                            main2Cap.toString() + " OVERLOAD!"
                    blink(main2AmpUsageM)

                    main2AmpUsageM.setTextColor(Color.parseColor("#FF0000"))
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        customInputAC.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var customInput: Double = 0.0

                if (customInputAC.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputAC.text.toString().toDouble()
                }
                circuits[main2AC]!!.ampsUsed -= (lastCustomAC - customInput)
                main2Used -= (lastCustomAC - customInput)
                lastCustomAC = customInput
                if (circuits[main2AC]!!.ampsUsed <= circuits[main2AC]!!.ampCapacity) {
                    circuits[main2AC]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main2AC]!!.ampsUsed) + "/" +
                            circuits[main2AC]!!.ampCapacity.toString()
                    circuits[main2AC]!!.userInterface.setTextColor(Color.parseColor("#000000"))

                } else {
                    circuits[main2AC]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main2AC]!!.ampsUsed) + "/" +
                            circuits[main2AC]!!.ampCapacity.toString() + " OVERLOAD!"
                    blink(main2AC)

                    circuits[main2AC]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))


                }

                if (main2Used < main2Cap) {
                    main2AmpUsageM.text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                            main2Cap.toString()
                    main2AmpUsageM.setTextColor(Color.parseColor("#000000"))
                } else {
                    main2AmpUsageM.text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                            main2Cap.toString() + " OVERLOAD!"
                    blink(main2AmpUsageM)

                    main2AmpUsageM.setTextColor(Color.parseColor("#FF0000"))
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        customInputConverter.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var customInput: Double = 0.0

                if (customInputConverter.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputConverter.text.toString().toDouble()
                }
                circuits[main2Converter]!!.ampsUsed -= (lastCustomConverter - customInput)
                main2Used -= (lastCustomConverter - customInput)
                lastCustomConverter = customInput
                if (circuits[main2Converter]!!.ampsUsed <= circuits[main2Converter]!!.ampCapacity) {
                    circuits[main2Converter]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main2Converter]!!.ampsUsed) + "/" +
                            circuits[main2Converter]!!.ampCapacity.toString()
                    circuits[main2Converter]!!.userInterface.setTextColor(Color.parseColor("#000000"))

                } else {
                    circuits[main2Converter]!!.userInterface.text = "Amps vs Rated Amps: " +
                            round(circuits[main2Converter]!!.ampsUsed) + "/" +
                            circuits[main2Converter]!!.ampCapacity.toString() + " OVERLOAD!"
                    blink(main2Converter)

                    circuits[main2Converter]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))


                }

                if (main2Used < main2Cap) {
                    main2AmpUsageM.text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                            main2Cap.toString()
                    main2AmpUsageM.setTextColor(Color.parseColor("#000000"))
                } else {
                    main2AmpUsageM.text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                            main2Cap.toString() + " OVERLOAD!"
                    blink(main2AmpUsageM)

                    main2AmpUsageM.setTextColor(Color.parseColor("#FF0000"))
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })

        reset()
    }
    /*
    var circuits = HashMap<FlexboxLayout, AmpUsage>(8)
    var main1Cap = 30.0
    var main2Cap = 30.0
    var main1Used = 0.0
    var main2Used = 0.0
    var lastCustomMBR = 0.0
    var lastCustomRefer = 0.0
    var lastCustomGFI = 0.0
    var lastCustomMicro = 0.0
    var lastCustomWaterHeater = 0.0
    var lastCustomAC = 0.0
    var lastCustomConverter = 0.0*/

    override fun onPause() {
        super.onPause()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted


        //Save the stuff in the circuits hashmap to the bundle individually
        var j = 0

        for(i in circuits){
            var myKey = "kID$j"
            var ampUNum = "aUNum$j"
            var ampCNum = "aCNum$j"
            var ampUINum = "aUINum$j"
            j++

            savedInstanceState.putInt(myKey, i.key.id)
            savedInstanceState.putDouble(ampUNum, circuits[i.key]!!.ampsUsed)
            savedInstanceState.putDouble(ampCNum, circuits[i.key]!!.ampCapacity)
            savedInstanceState.putInt(ampUINum, circuits[i.key]!!.userInterface.id)
        }


        savedInstanceState.putDouble("main1Cap", main1Cap)
        savedInstanceState.putDouble("main2Cap", main2Cap)
        savedInstanceState.putDouble("main1Used", main1Used)
        savedInstanceState.putDouble("main2Used", main2Used)
        savedInstanceState.putDouble("lastCustomMBR", lastCustomMBR)
        savedInstanceState.putDouble("lastCustomRefer", lastCustomRefer)
        savedInstanceState.putDouble("lastCustomGFI", lastCustomGFI)
        savedInstanceState.putDouble("lastCustomMicro", lastCustomMicro)
        savedInstanceState.putDouble("lastCustomWaterHeater", lastCustomWaterHeater)
        savedInstanceState.putDouble("lastCustomAC", lastCustomAC)
        savedInstanceState.putDouble("lastCustomConverter", lastCustomConverter)

        //Save the states of the checkboxes
        savedInstanceState.putBoolean(R.id.powerCheck.toString(), findViewById<CheckBox>(R.id.powerCheck).isChecked)
        savedInstanceState.putBoolean(R.id.chargerCheck.toString(), findViewById<CheckBox>(R.id.chargerCheck).isChecked)
        savedInstanceState.putBoolean(R.id.ACCheck.toString(), findViewById<CheckBox>(R.id.ACCheck).isChecked)
        savedInstanceState.putBoolean(R.id.converterCheck.toString(), findViewById<CheckBox>(R.id.converterCheck).isChecked)
        savedInstanceState.putBoolean(R.id.waterHeatCheck.toString(), findViewById<CheckBox>(R.id.waterHeatCheck).isChecked)
        savedInstanceState.putBoolean(R.id.microCheck.toString(), findViewById<CheckBox>(R.id.microCheck).isChecked)
        savedInstanceState.putBoolean(R.id.referTVCheck.toString(), findViewById<CheckBox>(R.id.referTVCheck).isChecked)
        savedInstanceState.putBoolean(R.id.referFrigeCheck.toString(), findViewById<CheckBox>(R.id.referFrigeCheck).isChecked)

        //Save the state of the custom inputs
        savedInstanceState.putString(R.id.MBRCustomInputField.toString(), findViewById<EditText>(R.id.MBRCustomInputField).text.toString())
        savedInstanceState.putString(R.id.referCustomInputField.toString(), findViewById<EditText>(R.id.referCustomInputField).text.toString())
        savedInstanceState.putString(R.id.GFICustomInputField.toString(), findViewById<EditText>(R.id.GFICustomInputField).text.toString())
        savedInstanceState.putString(R.id.microCustomInputField.toString(), findViewById<EditText>(R.id.microCustomInputField).text.toString())
        savedInstanceState.putString(R.id.waterHeaterCustomInputField.toString(), findViewById<EditText>(R.id.waterHeaterCustomInputField).text.toString())
        savedInstanceState.putString(R.id.ACCustomInputField.toString(), findViewById<EditText>(R.id.ACCustomInputField).text.toString())
        savedInstanceState.putString(R.id.converterCustomInputField.toString(), findViewById<EditText>(R.id.converterCustomInputField).text.toString())

        //Save the locations of the dragables
        savedInstanceState.putInt("compSize", components.size)
        var i = 0
        for(c in components){
            var parKey = "compP$i"
            var compKey = "comp$i"
            var usedKey = "compUsed$i"
            i++
            savedInstanceState.putInt(parKey, (c.key.parent as View).id)
            savedInstanceState.putInt(compKey, c.key.id)
            savedInstanceState.putDouble(usedKey, components[c.key]!!.ampsUsed)
        }

        /*
        savedInstanceState.putInt(R.id.toaster.toString(), (findViewById<View>(R.id.toaster).parent as View).id)
        savedInstanceState.putInt(R.id.kettle.toString(), (findViewById<View>(R.id.kettle).parent as View).id)
        savedInstanceState.putInt(R.id.towHeatHigh.toString(), (findViewById<View>(R.id.towHeatHigh).parent as View).id)
        savedInstanceState.putInt(R.id.towHeatLow.toString(), (findViewById<View>(R.id.towHeatLow).parent as View).id)
        savedInstanceState.putInt(R.id.vacuum.toString(), (findViewById<View>(R.id.vacuum).parent as View).id)
        savedInstanceState.putInt(R.id.workComp.toString(), (findViewById<View>(R.id.workComp).parent as View).id)
        savedInstanceState.putInt(R.id.laptop.toString(), (findViewById<View>(R.id.laptop).parent as View).id)
        savedInstanceState.putInt(R.id.compMon.toString(), (findViewById<View>(R.id.compMon).parent as View).id)
        */
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        main1Cap = savedInstanceState.getDouble("main1Cap")
        main2Cap = savedInstanceState.getDouble("main2Cap")
        main1Used = savedInstanceState.getDouble("main1Used")
        main2Used = savedInstanceState.getDouble("main2Used")
        lastCustomMBR = savedInstanceState.getDouble("lastCustomMBR")
        lastCustomRefer = savedInstanceState.getDouble("lastCustomRefer")
        lastCustomGFI = savedInstanceState.getDouble("lastCustomGFI")
        lastCustomMicro = savedInstanceState.getDouble("lastCustomMicro")
        lastCustomWaterHeater = savedInstanceState.getDouble("lastCustomWaterHeater")
        lastCustomAC = savedInstanceState.getDouble("lastCustomAC")
        lastCustomConverter = savedInstanceState.getDouble("lastCustomConverter")

        //Restore the states of the checkboxes
        findViewById<CheckBox>(R.id.powerCheck).isChecked = savedInstanceState.getBoolean(R.id.powerCheck.toString())
        findViewById<CheckBox>(R.id.chargerCheck).isChecked = savedInstanceState.getBoolean(R.id.chargerCheck.toString())
        findViewById<CheckBox>(R.id.ACCheck).isChecked = savedInstanceState.getBoolean(R.id.ACCheck.toString())
        findViewById<CheckBox>(R.id.converterCheck).isChecked = savedInstanceState.getBoolean(R.id.converterCheck.toString())
        findViewById<CheckBox>(R.id.waterHeatCheck).isChecked = savedInstanceState.getBoolean(R.id.waterHeatCheck.toString())
        findViewById<CheckBox>(R.id.microCheck).isChecked = savedInstanceState.getBoolean(R.id.microCheck.toString())
        findViewById<CheckBox>(R.id.referTVCheck).isChecked = savedInstanceState.getBoolean(R.id.referTVCheck.toString())
        findViewById<CheckBox>(R.id.referFrigeCheck).isChecked = savedInstanceState.getBoolean(R.id.referFrigeCheck.toString())


        //Restore the locations of the dragables
        var i = 0
        while(i < savedInstanceState.getInt("compSize")){
            var parKey = "compP$i"
            var compKey = "comp$i"
            var usedKey = "compUsed$i"
            i++

            var parent = findViewById<FlexboxLayout>(savedInstanceState.getInt(parKey))
            var component = findViewById<TextView>(savedInstanceState.getInt(compKey))
            var used = savedInstanceState.getDouble(usedKey)
            var source = findViewById<FlexboxLayout>(R.id.source)

            components[component] = ElectronicComponent(used)
            source.removeView(component)
            parent.addView(component)

        }
        if((findViewById<TextView>(R.id.towHeatLow).parent as View).id != R.id.source){
            findViewById<TextView>(R.id.towHeatHigh).visibility = View.GONE
        }
        else if((findViewById<TextView>(R.id.towHeatHigh).parent as View).id != R.id.source){
            findViewById<TextView>(R.id.towHeatLow).visibility = View.GONE
        }

        val vorHFan = findViewById<TextView>(R.id.vornadoHeatFan)
        val vorHLow = findViewById<TextView>(R.id.vornadoHeatLow)
        val vorHHigh = findViewById<TextView>(R.id.vornadoHeatHigh)

        if((vorHFan.parent as View).id != R.id.source){
            vorHLow.visibility = View.GONE
            vorHHigh.visibility = View.GONE
        }
        else if((vorHLow.parent as View).id != R.id.source){
            vorHFan.visibility = View.GONE
            vorHHigh.visibility = View.GONE
        }
        else if((vorHHigh.parent as View).id != R.id.source){
            vorHLow.visibility = View.GONE
            vorHFan.visibility = View.GONE
        }

        val vorFanLow = findViewById<TextView>(R.id.vornadoFanLow)
        val vorFanMed = findViewById<TextView>(R.id.vornadoFanMed)
        val vorFanHigh = findViewById<TextView>(R.id.vornadoFanHigh)

        if((vorFanLow.parent as View).id != R.id.source){
            vorFanMed.visibility = View.GONE
            vorFanHigh.visibility = View.GONE
        }
        else if((vorFanMed.parent as View).id != R.id.source){
            vorFanLow.visibility = View.GONE
            vorFanHigh.visibility = View.GONE
        }
        if((vorFanHigh.parent as View).id != R.id.source){
            vorFanLow.visibility = View.GONE
            vorFanMed.visibility = View.GONE
        }
        /*
        val toasterP = findViewById<FlexboxLayout>(savedInstanceState.getInt(R.id.toaster.toString()))
        val kettleP = findViewById<FlexboxLayout>(savedInstanceState.getInt(R.id.kettle.toString()))
        val towHeatHighP = findViewById<FlexboxLayout>(savedInstanceState.getInt(R.id.towHeatHigh.toString()))
        val towHeatLowP = findViewById<FlexboxLayout>(savedInstanceState.getInt(R.id.towHeatLow.toString()))
        val vacuumP = findViewById<FlexboxLayout>(savedInstanceState.getInt(R.id.vacuum.toString()))
        val workCompP = findViewById<FlexboxLayout>(savedInstanceState.getInt(R.id.workComp.toString()))
        val laptopP = findViewById<FlexboxLayout>(savedInstanceState.getInt(R.id.laptop.toString()))
        val compMonP = findViewById<FlexboxLayout>(savedInstanceState.getInt(R.id.compMon.toString()))

        val toaster = findViewById<TextView>(R.id.toaster)
        val kettle = findViewById<TextView>(R.id.kettle)
        val towHeatHigh = findViewById<TextView>(R.id.towHeatHigh)
        val towHeatLow = findViewById<TextView>(R.id.towHeatLow)
        val vacuum = findViewById<TextView>(R.id.vacuum)
        val workComp = findViewById<TextView>(R.id.workComp)
        val laptop = findViewById<TextView>(R.id.laptop)
        val compMon = findViewById<TextView>(R.id.compMon)

        val source = findViewById<FlexboxLayout>(R.id.source)

        source.removeView(toaster)
        toasterP.addView(toaster)
        source.removeView(kettle)
        kettleP.addView(kettle)
        source.removeView(towHeatHigh)
        towHeatHighP.addView(towHeatHigh)
        source.removeView(towHeatLow)
        towHeatLowP.addView(towHeatLow)
        source.removeView(vacuum)
        vacuumP.addView(vacuum)
        source.removeView(workComp)
        workCompP.addView(workComp)
        source.removeView(laptop)
        laptopP.addView(laptop)
        source.removeView(compMon)
        compMonP.addView(compMon)

         */



        var j = 0
        for(i in circuits){
            var myKey = "kID$j"
            var ampUNum = "aUNum$j"
            var ampCNum = "aCNum$j"
            var ampUINum = "aUINum$j"
            j++

            var theKey = findViewById<FlexboxLayout>(savedInstanceState.getInt(myKey))
            circuits[theKey] = AmpUsage(savedInstanceState.getDouble(ampCNum), savedInstanceState.getDouble(ampUNum), findViewById(savedInstanceState.getInt(ampUINum)))


        }

        //Restore the state of the custom inputs
        findViewById<EditText>(R.id.MBRCustomInputField).setText(savedInstanceState.getString(R.id.MBRCustomInputField.toString()))
        findViewById<EditText>(R.id.referCustomInputField).setText(savedInstanceState.getString(R.id.referCustomInputField.toString()))
        findViewById<EditText>(R.id.GFICustomInputField).setText(savedInstanceState.getString(R.id.GFICustomInputField.toString()))
        findViewById<EditText>(R.id.microCustomInputField).setText(savedInstanceState.getString(R.id.microCustomInputField.toString()))
        findViewById<EditText>(R.id.waterHeaterCustomInputField).setText(savedInstanceState.getString(R.id.waterHeaterCustomInputField.toString()))
        findViewById<EditText>(R.id.ACCustomInputField).setText(savedInstanceState.getString(R.id.ACCustomInputField.toString()))
        findViewById<EditText>(R.id.converterCustomInputField).setText(savedInstanceState.getString(R.id.converterCustomInputField.toString()))
    }


    val dragListener = View.OnDragListener{ view, event ->
        when(event.action){
            DragEvent.ACTION_DRAG_STARTED -> {
                (event.localState as TextView).visibility = View.INVISIBLE
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {

                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val item = event.clipData.getItemAt(0)
                val dragData = item.text

                view.invalidate()

                val v = event.localState as TextView
                val owner = v.parent as FlexboxLayout

                var movedItem: TextView

                val destination = view as FlexboxLayout

                if (destination.id != owner.id) {
                    if ((destination.parent as View).id == R.id.mainC1 &&
                            (owner.parent as View).id == R.id.mainC2) {
                        val amps = components[v]!!.ampsUsed
                        main1Used += amps
                        main2Used -= amps
                    } else if ((destination.parent as View).id == R.id.mainC2 &&
                            (owner.parent as View).id == R.id.mainC1) {
                        val amps = components[v]!!.ampsUsed
                        main2Used += amps
                        main1Used -= amps
                    } else if (owner.id == R.id.source) {
                        if ((destination.parent as View).id == R.id.mainC1) {
                            val amps = components[v]!!.ampsUsed
                            main1Used += amps
                        } else if ((destination.parent as View).id == R.id.mainC2) {
                            val amps = components[v]!!.ampsUsed
                            main2Used += amps
                        }
                    } else if ((owner.parent as View).id == R.id.mainC1 && destination.id == R.id.source) {
                        val amps = components[v]!!.ampsUsed
                        main1Used -= amps
                    } else if ((owner.parent as View).id == R.id.mainC2 && destination.id == R.id.source) {
                        val amps = components[v]!!.ampsUsed
                        main2Used -= amps
                    }

                    if (main1Used < main1Cap) {
                        findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                                main1Cap.toString()
                        findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#000000"))
                    } else {
                        findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                                main1Cap.toString() + " OVERLOAD!"

                        blink(findViewById<TextView>(R.id.mainC1Cap))
                        findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#FF0000"))
                    }

                    if (main2Used < main2Cap) {
                        findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                                main2Cap.toString()
                        findViewById<TextView>(R.id.mainC2Cap).setTextColor(Color.parseColor("#000000"))
                    } else {
                        findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                                main2Cap.toString() + " OVERLOAD!"

                        blink(findViewById<TextView>(R.id.mainC2Cap))
                        findViewById<TextView>(R.id.mainC2Cap).setTextColor(Color.parseColor("#FF0000"))
                    }
                }
                if (circuits.containsKey(destination)) {
                    val amps = components[v]!!.ampsUsed
                    val ampCap = circuits[destination]!!.ampCapacity
                    circuits[destination]!!.ampsUsed += amps
                    val ampUsed = circuits[destination]!!.ampsUsed

                    if (ampUsed <= ampCap) {
                        circuits[destination]!!.userInterface.setTextColor(Color.parseColor("#000000"))
                        circuits[destination]!!.userInterface.text = "Amps vs Rated Amps: " + round(ampUsed) + "/" + ampCap.toString()
                    } else {
                        circuits[destination]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))
                        circuits[destination]!!.userInterface.text = "Amps vs Rated Amps: " + round(ampUsed) + "/" + ampCap.toString() + " OVERLOAD!"
                        blink(destination)
                    }
                }
                if (circuits.containsKey(owner)) {
                    val amps = components[v]!!.ampsUsed
                    val ampCap = circuits[owner]!!.ampCapacity
                    circuits[owner]!!.ampsUsed -= amps
                    val ampUsed = circuits[owner]!!.ampsUsed

                    if (ampUsed <= ampCap) {
                        circuits[owner]!!.userInterface.setTextColor(Color.parseColor("#000000"))
                        circuits[owner]!!.userInterface.text = "Amps vs Rated Amps: " + round(ampUsed) + "/" + ampCap.toString()
                    } else {
                        circuits[owner]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))
                        circuits[owner]!!.userInterface.text = "Amps vs Rated Amps: " + round(ampUsed) + "/" + ampCap.toString() + " OVERLOAD!"
                        blink(owner)
                    }
                }

                if (owner.id == R.id.source && destination.id != owner.id) {
                    if (genericComponents.contains(v)) {
                        val newText = TextView(this)

                        newText.height = v.height
                        newText.width = v.width
                        newText.textSize = 10f
                        newText.gravity = Gravity.CENTER
                        newText.background = v.background
                        newText.text = v.text.toString()
                        newText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        newText.setTextColor(v.currentTextColor)
                        newText.layoutParams = v.layoutParams

                        destination.addView(newText)
                        dupedComponents.add(numDupedItems, newText)
                        newText.setOnLongClickListener {
                            val clipText = "This is our ClipData text"
                            val item = ClipData.Item(clipText)
                            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            val data = ClipData(clipText, mimeTypes, item)

                            val dragShadowBuilder = View.DragShadowBuilder(it)
                            it.startDragAndDrop(data, dragShadowBuilder, it, 0)

                            //it.visibility = View.INVISIBLE
                            true
                        }
                        val amps = components[v]!!.ampsUsed
                        components[newText] = ElectronicComponent(amps)
                        numDupedItems++
                    } else {

                        owner.removeView(v)
                        destination.addView(v)
                        if (v.id == R.id.towHeatLow) {
                            findViewById<TextView>(R.id.towHeatHigh).visibility = View.GONE
                        }
                        else if (v.id == R.id.towHeatHigh) {
                            val other = findViewById<TextView>(R.id.towHeatLow)
                            other.visibility = View.GONE
                        }
                        else if(v.id == R.id.vornadoHeatFan){
                            findViewById<TextView>(R.id.vornadoHeatLow).visibility = View.GONE
                            findViewById<TextView>(R.id.vornadoHeatHigh).visibility = View.GONE
                        }
                        else if(v.id == R.id.vornadoHeatLow){
                            findViewById<TextView>(R.id.vornadoHeatFan).visibility = View.GONE
                            findViewById<TextView>(R.id.vornadoHeatHigh).visibility = View.GONE
                        }
                        else if(v.id == R.id.vornadoHeatHigh){
                            findViewById<TextView>(R.id.vornadoHeatLow).visibility = View.GONE
                            findViewById<TextView>(R.id.vornadoHeatFan).visibility = View.GONE
                        }
                        else if(v.id == R.id.vornadoFanLow){
                            findViewById<TextView>(R.id.vornadoFanMed).visibility = View.GONE
                            findViewById<TextView>(R.id.vornadoFanHigh).visibility = View.GONE
                        }
                        else if(v.id == R.id.vornadoFanMed){
                            findViewById<TextView>(R.id.vornadoFanLow).visibility = View.GONE
                            findViewById<TextView>(R.id.vornadoFanHigh).visibility = View.GONE
                        }
                        else if(v.id == R.id.vornadoFanHigh){
                            findViewById<TextView>(R.id.vornadoFanMed).visibility = View.GONE
                            findViewById<TextView>(R.id.vornadoFanLow).visibility = View.GONE
                        }
                    }
                } else {
                    owner.removeView(v)
                    destination.addView(v)

                }

                if (destination.id == R.id.source && owner.id != destination.id
                        && dupedComponents.contains(v)) {
                    dupedComponents.remove(v)
                    numDupedItems--
                    v.visibility = View.GONE
                    components.remove(v)
                    destination.removeView(v)

                }
                v.visibility = View.VISIBLE

                if (destination.id == R.id.source && owner.id != destination.id) {
                    if (v.id == R.id.towHeatLow) {
                        findViewById<TextView>(R.id.towHeatHigh).visibility = View.VISIBLE
                    }
                    else if (v.id == R.id.towHeatHigh) {
                        findViewById<TextView>(R.id.towHeatLow).visibility = View.VISIBLE
                    }
                    else if(v.id == R.id.vornadoHeatFan){
                        findViewById<TextView>(R.id.vornadoHeatLow).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.vornadoHeatHigh).visibility = View.VISIBLE
                    }
                    else if(v.id == R.id.vornadoHeatLow){
                        findViewById<TextView>(R.id.vornadoHeatFan).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.vornadoHeatHigh).visibility = View.VISIBLE
                    }
                    else if(v.id == R.id.vornadoHeatHigh){
                        findViewById<TextView>(R.id.vornadoHeatLow).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.vornadoHeatFan).visibility = View.VISIBLE
                    }
                    else if(v.id == R.id.vornadoFanLow){
                        findViewById<TextView>(R.id.vornadoFanMed).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.vornadoFanHigh).visibility = View.VISIBLE
                    }
                    else if(v.id == R.id.vornadoFanMed){
                        findViewById<TextView>(R.id.vornadoFanLow).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.vornadoFanHigh).visibility = View.VISIBLE
                    }
                    else if(v.id == R.id.vornadoFanHigh){
                        findViewById<TextView>(R.id.vornadoFanMed).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.vornadoFanLow).visibility = View.VISIBLE
                    }
                }





                true

            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
            else->false
        }

    }
    val sourceScrollListener = View.OnDragListener{ view, event ->
        when(event.action){
            DragEvent.ACTION_DRAG_STARTED -> {
                (event.localState as TextView).visibility = View.INVISIBLE
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {

                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val item = event.clipData.getItemAt(0)
                val dragData = item.text

                view.invalidate()

                val v = event.localState as TextView
                val owner = v.parent as FlexboxLayout

                var movedItem: TextView

                val destination = findViewById(R.id.source) as FlexboxLayout

                if (destination.id != owner.id) {
                    if ((destination.parent as View).id == R.id.mainC1 &&
                            (owner.parent as View).id == R.id.mainC2) {
                        val amps = components[v]!!.ampsUsed
                        main1Used += amps
                        main2Used -= amps
                    } else if ((destination.parent as View).id == R.id.mainC2 &&
                            (owner.parent as View).id == R.id.mainC1) {
                        val amps = components[v]!!.ampsUsed
                        main2Used += amps
                        main1Used -= amps
                    } else if (owner.id == R.id.source) {
                        if ((destination.parent as View).id == R.id.mainC1) {
                            val amps = components[v]!!.ampsUsed
                            main1Used += amps
                        } else if ((destination.parent as View).id == R.id.mainC2) {
                            val amps = components[v]!!.ampsUsed
                            main2Used += amps
                        }
                    } else if ((owner.parent as View).id == R.id.mainC1 && destination.id == R.id.source) {
                        val amps = components[v]!!.ampsUsed
                        main1Used -= amps
                    } else if ((owner.parent as View).id == R.id.mainC2 && destination.id == R.id.source) {
                        val amps = components[v]!!.ampsUsed
                        main2Used -= amps
                    }

                    if (main1Used < main1Cap) {
                        findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                                main1Cap.toString()
                        findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#000000"))
                    } else {
                        findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                                main1Cap.toString() + " OVERLOAD!"

                        blink(findViewById<TextView>(R.id.mainC1Cap))
                        findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#FF0000"))
                    }

                    if (main2Used < main2Cap) {
                        findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                                main2Cap.toString()
                        findViewById<TextView>(R.id.mainC2Cap).setTextColor(Color.parseColor("#000000"))
                    } else {
                        findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                                main2Cap.toString() + " OVERLOAD!"

                        blink(findViewById<TextView>(R.id.mainC2Cap))
                        findViewById<TextView>(R.id.mainC2Cap).setTextColor(Color.parseColor("#FF0000"))
                    }
                }
                if (circuits.containsKey(destination)) {
                    val amps = components[v]!!.ampsUsed
                    val ampCap = circuits[destination]!!.ampCapacity
                    circuits[destination]!!.ampsUsed += amps
                    val ampUsed = circuits[destination]!!.ampsUsed

                    if (ampUsed <= ampCap) {
                        circuits[destination]!!.userInterface.setTextColor(Color.parseColor("#000000"))
                        circuits[destination]!!.userInterface.text = "Amps vs Rated Amps: " + round(ampUsed) + "/" + ampCap.toString()
                    } else {
                        circuits[destination]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))
                        circuits[destination]!!.userInterface.text = "Amps vs Rated Amps: " + round(ampUsed) + "/" + ampCap.toString() + " OVERLOAD!"
                        blink(destination)
                    }
                }
                if (circuits.containsKey(owner)) {
                    val amps = components[v]!!.ampsUsed
                    val ampCap = circuits[owner]!!.ampCapacity
                    circuits[owner]!!.ampsUsed -= amps
                    val ampUsed = circuits[owner]!!.ampsUsed

                    if (ampUsed <= ampCap) {
                        circuits[owner]!!.userInterface.setTextColor(Color.parseColor("#000000"))
                        circuits[owner]!!.userInterface.text = "Amps vs Rated Amps: " + round(ampUsed) + "/" + ampCap.toString()
                    } else {
                        circuits[owner]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))
                        circuits[owner]!!.userInterface.text = "Amps vs Rated Amps: " + round(ampUsed) + "/" + ampCap.toString() + " OVERLOAD!"
                        blink(owner)
                    }
                }

                if (owner.id == R.id.source && destination.id != owner.id) {
                    if (genericComponents.contains(v)) {
                        val newText = TextView(this)

                        newText.height = v.height
                        newText.width = v.width
                        newText.textSize = 10f
                        newText.gravity = Gravity.CENTER
                        newText.background = v.background
                        newText.text = v.text.toString()
                        newText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        newText.setTextColor(v.currentTextColor)
                        newText.layoutParams = v.layoutParams

                        destination.addView(newText)
                        dupedComponents.add(numDupedItems, newText)
                        newText.setOnLongClickListener {
                            val clipText = "This is our ClipData text"
                            val item = ClipData.Item(clipText)
                            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            val data = ClipData(clipText, mimeTypes, item)

                            val dragShadowBuilder = View.DragShadowBuilder(it)
                            it.startDragAndDrop(data, dragShadowBuilder, it, 0)

                            //it.visibility = View.INVISIBLE
                            true
                        }
                        val amps = components[v]!!.ampsUsed
                        components[newText] = ElectronicComponent(amps)
                        numDupedItems++
                    } else {

                        owner.removeView(v)
                        destination.addView(v)
                        if (v.id == R.id.towHeatLow) {
                            findViewById<TextView>(R.id.towHeatHigh).visibility = View.GONE
                        } else if (v.id == R.id.towHeatHigh) {
                            val other = findViewById<TextView>(R.id.towHeatLow)
                            other.visibility = View.GONE
                        }

                        if (v.id == R.id.towHeatLow) {
                            findViewById<TextView>(R.id.towHeatHigh).visibility = View.VISIBLE
                        } else if (v.id == R.id.towHeatHigh) {
                            findViewById<TextView>(R.id.towHeatLow).visibility = View.VISIBLE
                        }
                        else if(v.id == R.id.vornadoHeatFan){
                            findViewById<TextView>(R.id.vornadoHeatLow).visibility = View.GONE
                            findViewById<TextView>(R.id.vornadoHeatHigh).visibility = View.GONE
                        }
                        else if(v.id == R.id.vornadoHeatLow){
                            findViewById<TextView>(R.id.vornadoHeatFan).visibility = View.GONE
                            findViewById<TextView>(R.id.vornadoHeatHigh).visibility = View.GONE
                        }
                        else if(v.id == R.id.vornadoHeatHigh){
                            findViewById<TextView>(R.id.vornadoHeatLow).visibility = View.GONE
                            findViewById<TextView>(R.id.vornadoHeatFan).visibility = View.GONE
                        }
                        else if(v.id == R.id.vornadoFanLow){
                            findViewById<TextView>(R.id.vornadoFanMed).visibility = View.GONE
                            findViewById<TextView>(R.id.vornadoFanHigh).visibility = View.GONE
                        }
                        else if(v.id == R.id.vornadoFanMed){
                            findViewById<TextView>(R.id.vornadoFanLow).visibility = View.GONE
                            findViewById<TextView>(R.id.vornadoFanHigh).visibility = View.GONE
                        }
                        else if(v.id == R.id.vornadoFanHigh){
                            findViewById<TextView>(R.id.vornadoFanMed).visibility = View.GONE
                            findViewById<TextView>(R.id.vornadoFanLow).visibility = View.GONE
                        }
                    }
                } else {
                    owner.removeView(v)
                    destination.addView(v)

                }

                if (destination.id == R.id.source && owner.id != destination.id
                        && dupedComponents.contains(v)) {
                    dupedComponents.remove(v)
                    numDupedItems--
                    v.visibility = View.GONE
                    components.remove(v)
                    destination.removeView(v)

                }
                v.visibility = View.VISIBLE

                if (destination.id == R.id.source && owner.id != destination.id) {
                    if (v.id == R.id.towHeatLow) {
                        findViewById<TextView>(R.id.towHeatHigh).visibility = View.VISIBLE
                    }
                    else if (v.id == R.id.towHeatHigh) {
                        findViewById<TextView>(R.id.towHeatLow).visibility = View.VISIBLE
                    }
                    else if(v.id == R.id.vornadoHeatFan){
                        findViewById<TextView>(R.id.vornadoHeatLow).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.vornadoHeatHigh).visibility = View.VISIBLE
                    }
                    else if(v.id == R.id.vornadoHeatLow){
                        findViewById<TextView>(R.id.vornadoHeatFan).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.vornadoHeatHigh).visibility = View.VISIBLE
                    }
                    else if(v.id == R.id.vornadoHeatHigh){
                        findViewById<TextView>(R.id.vornadoHeatLow).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.vornadoHeatFan).visibility = View.VISIBLE
                    }
                    else if(v.id == R.id.vornadoFanLow){
                        findViewById<TextView>(R.id.vornadoFanMed).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.vornadoFanHigh).visibility = View.VISIBLE
                    }
                    else if(v.id == R.id.vornadoFanMed){
                        findViewById<TextView>(R.id.vornadoFanLow).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.vornadoFanHigh).visibility = View.VISIBLE
                    }
                    else if(v.id == R.id.vornadoFanHigh){
                        findViewById<TextView>(R.id.vornadoFanMed).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.vornadoFanLow).visibility = View.VISIBLE
                    }
                }





                true

            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
            else->false
        }

    }
    val inertDragListener = View.OnDragListener{ view, event ->
        when(event.action){
            DragEvent.ACTION_DRAG_STARTED -> {
                (event.localState as TextView).visibility = View.INVISIBLE
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {

                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val item = event.clipData.getItemAt(0)
                val dragData = item.text

                view.invalidate()

                val v = event.localState as TextView
                val owner = v.parent as FlexboxLayout

                val destination = view as View
                v.visibility = View.VISIBLE


                true

            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
            else->false
        }

    }
    fun powerSupplyClicked(v: View?) {
        if((v as CheckBox).isChecked){
            main1Cap = 50.0
            main2Cap = 50.0
        }
        else{
            main1Cap = 30.0
            main2Cap = 30.0
        }
        if(main1Used <= main1Cap){
            findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#000000"))
            findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " +
                    round(main1Used) + "/" + main1Cap.toString()
        }
        else{
            findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#FF0000"))
            findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " +
                    round(main1Used) + "/" + main1Cap.toString() + " OVERLOAD!"
            blink(findViewById<TextView>(R.id.mainC1Cap))
        }

        if(main2Used <= main2Cap){
            findViewById<TextView>(R.id.mainC2Cap).setTextColor(Color.parseColor("#000000"))
            findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " +
                    round(main2Used) + "/" + main2Cap.toString()
        }
        else{
            findViewById<TextView>(R.id.mainC2Cap).setTextColor(Color.parseColor("#FF0000"))
            findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " +
                    round(main2Used) + "/" + main2Cap.toString() + " OVERLOAD!"
            blink(findViewById<TextView>(R.id.mainC2Cap))
        }

    }
    fun chargerClicked(v: View?) {
        var mbr: FlexboxLayout = findViewById(R.id.main1MBR)
        if((v as CheckBox).isChecked){
            main1Used += 6
            circuits[mbr]!!.ampsUsed += 6
        }
        else{
            main1Used -= 6
            circuits[mbr]!!.ampsUsed -= 6
        }

        if (circuits[mbr]!!.ampsUsed <= circuits[mbr]!!.ampCapacity) {
            circuits[mbr]!!.userInterface.text = "Amps vs Rated Amps: " +
                    round(circuits[mbr]!!.ampsUsed) + "/" +
                    circuits[mbr]!!.ampCapacity.toString()
            circuits[mbr]!!.userInterface.setTextColor(Color.parseColor("#000000"))

        } else {
            circuits[mbr]!!.userInterface.text = "Amps vs Rated Amps: " +
                    round(circuits[mbr]!!.ampsUsed) + "/" +
                    circuits[mbr]!!.ampCapacity.toString() + " OVERLOAD!"

            blink(mbr)
            circuits[mbr]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))


        }

        if (main1Used < main1Cap){
            findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                    main1Cap.toString()
            findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#000000"))
        }
        else{
            findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                    main1Cap.toString() + " OVERLOAD!"

            findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#FF0000"))
            blink(findViewById<TextView>(R.id.mainC1Cap))
        }


    }

    fun referTVClicked(v: View?) {
        var refer: FlexboxLayout = findViewById(R.id.main1Refer)
        if((v as CheckBox).isChecked){
            main1Used += 0.4
            circuits[refer]!!.ampsUsed += 0.4
        }
        else{
            main1Used -= 0.4
            circuits[refer]!!.ampsUsed -= 0.4
        }

        if (circuits[refer]!!.ampsUsed <= circuits[refer]!!.ampCapacity) {
            circuits[refer]!!.userInterface.text = "Amps vs Rated Amps: " +
                    round(circuits[refer]!!.ampsUsed) + "/" +
                    circuits[refer]!!.ampCapacity.toString()
            circuits[refer]!!.userInterface.setTextColor(Color.parseColor("#000000"))

        } else {
            circuits[refer]!!.userInterface.text = "Amps vs Rated Amps: " +
                    circuits[refer]!!.ampsUsed.toString() + "/" +
                    circuits[refer]!!.ampCapacity.toString() + " OVERLOAD!"

            blink(refer)
            circuits[refer]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))


        }

        if (main1Used < main1Cap){
            findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                    main1Cap.toString()
            findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#000000"))
        }
        else{
            findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                    main1Cap.toString() + " OVERLOAD!"

            findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#FF0000"))
            blink(findViewById<TextView>(R.id.mainC1Cap))
        }


    }
    fun converterClicked(v: View?) {
        var converter: FlexboxLayout = findViewById(R.id.main2Converter)
        if((v as CheckBox).isChecked){
            main2Used += 9
            circuits[converter]!!.ampsUsed += 9
        }
        else{
            main2Used -= 9
            circuits[converter]!!.ampsUsed -= 9
        }

        if (circuits[converter]!!.ampsUsed <= circuits[converter]!!.ampCapacity) {
            circuits[converter]!!.userInterface.text = "Amps vs Rated Amps: " +
                    round(circuits[converter]!!.ampsUsed) + "/" +
                    circuits[converter]!!.ampCapacity.toString()
            circuits[converter]!!.userInterface.setTextColor(Color.parseColor("#000000"))

        }
        else {
            circuits[converter]!!.userInterface.text = "Amps vs Rated Amps: " +
                    round(circuits[converter]!!.ampsUsed) + "/" +
                    circuits[converter]!!.ampCapacity.toString() + " OVERLOAD!"

            circuits[converter]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))
            blink(converter)


        }

        if (main2Used < main2Cap){
            findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                    main2Cap.toString()
            findViewById<TextView>(R.id.mainC2Cap).setTextColor(Color.parseColor("#000000"))
        }
        else{
            findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                    main2Cap.toString() + " OVERLOAD!"

            findViewById<TextView>(R.id.mainC2Cap).setTextColor(Color.parseColor("#FF0000"))
            blink(findViewById<TextView>(R.id.mainC2Cap))
        }

    }
    fun airConditionerClicked(v: View?) {
        var airCon: FlexboxLayout = findViewById(R.id.main2AC)
        var amount = 13.0
        if((v as CheckBox).isChecked){
            main2Used += amount
            circuits[airCon]!!.ampsUsed += amount
        }
        else{
            main2Used -= amount
            circuits[airCon]!!.ampsUsed -= amount
        }

        if (circuits[airCon]!!.ampsUsed <= circuits[airCon]!!.ampCapacity) {
            circuits[airCon]!!.userInterface.text = "Amps vs Rated Amps: " +
                    round(circuits[airCon]!!.ampsUsed) + "/" +
                    circuits[airCon]!!.ampCapacity.toString()
            circuits[airCon]!!.userInterface.setTextColor(Color.parseColor("#000000"))

        }
        else {
            circuits[airCon]!!.userInterface.text = "Amps vs Rated Amps: " +
                    round(circuits[airCon]!!.ampsUsed) + "/" +
                    circuits[airCon]!!.ampCapacity.toString() + " OVERLOAD!"

            circuits[airCon]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))
            blink(airCon)


        }

        if (main2Used < main2Cap){
            findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " + main2Used.toString() + "/" +
                    main2Cap.toString()
            findViewById<TextView>(R.id.mainC2Cap).setTextColor(Color.parseColor("#000000"))
        }
        else{
            findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " + main2Used.toString() + "/" +
                    main2Cap.toString() + " OVERLOAD!"

            findViewById<TextView>(R.id.mainC2Cap).setTextColor(Color.parseColor("#FF0000"))
            blink(findViewById<TextView>(R.id.mainC2Cap))
        }
    }
    fun microClicked(v: View?) {
        var micro: FlexboxLayout = findViewById(R.id.main1Micro)
        var amount = 15.0
        if((v as CheckBox).isChecked){
            main1Used += amount
            circuits[micro]!!.ampsUsed += amount
        }
        else{
            main1Used -= amount
            circuits[micro]!!.ampsUsed -= amount
        }

        if (circuits[micro]!!.ampsUsed <= circuits[micro]!!.ampCapacity) {
            circuits[micro]!!.userInterface.text = "Amps vs Rated Amps: " +
                    round(circuits[micro]!!.ampsUsed) + "/" +
                    circuits[micro]!!.ampCapacity.toString()
            circuits[micro]!!.userInterface.setTextColor(Color.parseColor("#000000"))

        } else {
            circuits[micro]!!.userInterface.text = "Amps vs Rated Amps: " +
                    round(circuits[micro]!!.ampsUsed) + "/" +
                    circuits[micro]!!.ampCapacity.toString() + " OVERLOAD!"

            circuits[micro]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))
            blink(micro)


        }

        if (main1Used < main1Cap){
            findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                    main1Cap.toString()
            findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#000000"))
        }
        else{
            findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                    main1Cap.toString() + " OVERLOAD!"

            findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#FF0000"))
            blink(findViewById<TextView>(R.id.mainC1Cap))
        }

        findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " +
                round(main1Used) + "/" + main1Cap.toString()

        findViewById<TextView>(R.id.microUsage).text = "Amps vs Rated Amps: " +
                round(circuits[micro]!!.ampsUsed) + "/" +
                circuits[micro]!!.ampCapacity.toString()


    }
    fun waterHeatClicked(v: View?) {
        var water: FlexboxLayout = findViewById(R.id.main2WaterHeater)
        var amount = 11.0
        if((v as CheckBox).isChecked){
            main2Used += amount
            circuits[water]!!.ampsUsed += amount
        }
        else{
            main2Used -= amount
            circuits[water]!!.ampsUsed -= amount
        }

        if (circuits[water]!!.ampsUsed <= circuits[water]!!.ampCapacity) {
            circuits[water]!!.userInterface.text = "Amps vs Rated Amps: " +
                    round(circuits[water]!!.ampsUsed) + "/" +
                    circuits[water]!!.ampCapacity.toString()
            circuits[water]!!.userInterface.setTextColor(Color.parseColor("#000000"))

        }
        else {
            circuits[water]!!.userInterface.text = "Amps vs Rated Amps: " +
                    round(circuits[water]!!.ampsUsed) + "/" +
                    circuits[water]!!.ampCapacity.toString() + " OVERLOAD!"

            circuits[water]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))
            blink(water)


        }

        if (main2Used < main2Cap){
            findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                    main2Cap.toString()
            findViewById<TextView>(R.id.mainC2Cap).setTextColor(Color.parseColor("#000000"))
        }
        else{
            findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " + round(main2Used) + "/" +
                    main2Cap.toString() + " OVERLOAD!"

            findViewById<TextView>(R.id.mainC2Cap).setTextColor(Color.parseColor("#FF0000"))
            blink(findViewById<TextView>(R.id.mainC2Cap))
        }


    }
    fun referFrigeClicked(v: View?) {
        var refer: FlexboxLayout = findViewById(R.id.main1Refer)
        if ((v as CheckBox).isChecked) {
            main1Used += 2
            circuits[refer]!!.ampsUsed += 2
        } else {
            main1Used -= 2
            circuits[refer]!!.ampsUsed -= 2
        }

        if (circuits[refer]!!.ampsUsed <= circuits[refer]!!.ampCapacity) {
            circuits[refer]!!.userInterface.text = "Amps vs Rated Amps: " +
                    round(circuits[refer]!!.ampsUsed) + "/" +
                    circuits[refer]!!.ampCapacity.toString()
            circuits[refer]!!.userInterface.setTextColor(Color.parseColor("#000000"))

        } else {
            circuits[refer]!!.userInterface.text = "Amps vs Rated Amps: " +
                    round(circuits[refer]!!.ampsUsed) + "/" +
                    circuits[refer]!!.ampCapacity.toString() + " OVERLOAD!"

            circuits[refer]!!.userInterface.setTextColor(Color.parseColor("#FF0000"))
            blink(refer)


        }

        if (main1Used < main1Cap){
            findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                    round(main1Cap)
            findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#000000"))
        }
        else{
            findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " + round(main1Used) + "/" +
                    main1Cap.toString() + " OVERLOAD!"

            findViewById<TextView>(R.id.mainC1Cap).setTextColor(Color.parseColor("#FF0000"))
            blink(findViewById<TextView>(R.id.mainC1Cap))
        }
    }
    fun saveClicked(v: View?) {
        file.writeText("")

        //Save the stuff in the circuits hashmap to the bundle individually
        var j = 0

        for(i in circuits){
            j++

            file.appendText("" + i.key.id + "\n")
            file.appendText("" + circuits[i.key]!!.ampsUsed + "\n")
            file.appendText("" + circuits[i.key]!!.ampCapacity + "\n")
            file.appendText("" + circuits[i.key]!!.userInterface.id + "\n")
        }


        file.appendText("$main1Cap\n")
        file.appendText("$main2Cap\n")
        file.appendText("$main1Used\n")
        file.appendText("$main2Used\n")
        file.appendText("$lastCustomMBR\n")
        file.appendText("$lastCustomRefer\n")
        file.appendText("$lastCustomGFI\n")
        file.appendText("$lastCustomMicro\n")
        file.appendText("$lastCustomWaterHeater\n")
        file.appendText("$lastCustomAC\n")
        file.appendText("$lastCustomConverter\n")

        //Save the states of the checkboxes
        file.appendText("" + findViewById<CheckBox>(R.id.powerCheck).isChecked + "\n")
        file.appendText("" + findViewById<CheckBox>(R.id.chargerCheck).isChecked + "\n")
        file.appendText("" + findViewById<CheckBox>(R.id.ACCheck).isChecked + "\n")
        file.appendText("" + findViewById<CheckBox>(R.id.converterCheck).isChecked + "\n")
        file.appendText("" + findViewById<CheckBox>(R.id.waterHeatCheck).isChecked + "\n")
        file.appendText("" + findViewById<CheckBox>(R.id.microCheck).isChecked + "\n")
        file.appendText("" + findViewById<CheckBox>(R.id.referTVCheck).isChecked + "\n")
        file.appendText("" + findViewById<CheckBox>(R.id.referFrigeCheck).isChecked + "\n")


        file.appendText(components.size.toString() + "\n")
        var i = 0
        for(c in components){
            i++
            file.appendText((c.key.parent as View).id.toString() + "\n")
            file.appendText(c.key.id.toString() + "\n")
            file.appendText(components[c.key]!!.ampsUsed.toString() + "\n")
        }
        /*
        //Save the locations of the dragables
        file.appendText("" + (findViewById<View>(R.id.toaster).parent as View).id + "\n")
        file.appendText("" + (findViewById<View>(R.id.kettle).parent as View).id + "\n")
        file.appendText("" + (findViewById<View>(R.id.towHeatHigh).parent as View).id + "\n")
        file.appendText("" + (findViewById<View>(R.id.towHeatLow).parent as View).id + "\n")
        file.appendText("" + (findViewById<View>(R.id.vacuum).parent as View).id + "\n")
        file.appendText("" + (findViewById<View>(R.id.workComp).parent as View).id + "\n")
        file.appendText("" + (findViewById<View>(R.id.laptop).parent as View).id + "\n")
        file.appendText("" + (findViewById<View>(R.id.compMon).parent as View).id + "\n")

         */

        //Save the state of the custom inputs
        file.appendText(findViewById<EditText>(R.id.MBRCustomInputField).text.toString() + "\n")
        file.appendText(findViewById<EditText>(R.id.referCustomInputField).text.toString() + "\n")
        file.appendText(findViewById<EditText>(R.id.GFICustomInputField).text.toString() + "\n")
        file.appendText(findViewById<EditText>(R.id.microCustomInputField).text.toString() + "\n")
        file.appendText(findViewById<EditText>(R.id.waterHeaterCustomInputField).text.toString() + "\n")
        file.appendText(findViewById<EditText>(R.id.ACCustomInputField).text.toString() + "\n")
        file.appendText(findViewById<EditText>(R.id.converterCustomInputField).text.toString() + "\n")
    }
    fun loadClicked(v: View?){
        try {
            if (file.length() == 0.toLong()) {
                Toast.makeText(getApplicationContext(), "No Save State", Toast.LENGTH_SHORT).show()
                return
            }
            reset()

            val readResult = FileInputStream(file).bufferedReader().use { it.readText() }

            //Save the stuff in the circuits hashmap to the bundle individually

            val lines = readResult.split("\n").toTypedArray()


            var j = 0

            for (i in circuits) {
                var keyID = lines[j].toInt()
                j++
                var ampsU = lines[j].toDouble()
                j++
                var ampC = lines[j].toDouble()
                j++
                var iID = lines[j].toInt()
                j++

                var theKey = findViewById<FlexboxLayout>(keyID)
                circuits[theKey] = AmpUsage(ampC, ampsU, findViewById(iID))
            }


            main1Cap = lines[j].toDouble()
            j++
            main2Cap = lines[j].toDouble()
            j++
            main1Used = lines[j].toDouble()
            j++
            main2Used = lines[j].toDouble()
            j++
            lastCustomMBR = lines[j].toDouble()
            j++
            lastCustomRefer = lines[j].toDouble()
            j++
            lastCustomGFI = lines[j].toDouble()
            j++
            lastCustomMicro = lines[j].toDouble()
            j++
            lastCustomWaterHeater = lines[j].toDouble()
            j++
            lastCustomAC = lines[j].toDouble()
            j++
            lastCustomConverter = lines[j].toDouble()
            j++

            //Save the states of the checkboxes
            findViewById<CheckBox>(R.id.powerCheck).isChecked = lines[j].toBoolean()
            j++
            findViewById<CheckBox>(R.id.chargerCheck).isChecked = lines[j].toBoolean()
            j++
            findViewById<CheckBox>(R.id.ACCheck).isChecked = lines[j].toBoolean()
            j++
            findViewById<CheckBox>(R.id.converterCheck).isChecked = lines[j].toBoolean()
            j++
            findViewById<CheckBox>(R.id.waterHeatCheck).isChecked = lines[j].toBoolean()
            j++
            findViewById<CheckBox>(R.id.microCheck).isChecked = lines[j].toBoolean()
            j++
            findViewById<CheckBox>(R.id.referTVCheck).isChecked = lines[j].toBoolean()
            j++
            findViewById<CheckBox>(R.id.referFrigeCheck).isChecked = lines[j].toBoolean()
            j++



            var dupes = 0
            var parents = mutableListOf<FlexboxLayout>()
            var repeatTimes = lines[j].toInt()
            j++
            var i = 0
            while(i < repeatTimes){
                i++


                var parent = findViewById<FlexboxLayout>(lines[j].toInt())
                j++
                var component = findViewById<TextView>(lines[j].toInt())
                j++
                var used = lines[j].toDouble()
                j++

                if(component != null) {
                    components[component] = ElectronicComponent(used)

                    var curPar = (component.parent as FlexboxLayout)

                    curPar.removeView(component)
                    parent.addView(component)
                }
                else{
                    dupes++
                    parents.add(parent)
                }
            }
            var l = 0
            numDupedItems = 0
            while(l < dupes){

                var v = findViewById<TextView>(R.id.laptop)

                var newText = TextView(this)

                newText.height = v.height
                newText.width = v.width
                newText.textSize = 10f
                newText.gravity = Gravity.CENTER
                newText.background = v.background
                newText.text = v.text
                newText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                newText.setTextColor(v.currentTextColor)
                newText.layoutParams = v.layoutParams

                parents[l].addView(newText)
                dupedComponents.add(l, newText)
                components[newText] = ElectronicComponent(components[v]!!.ampsUsed)
                numDupedItems++
                newText.setOnLongClickListener {
                    val clipText = "This is our ClipData text"
                    val item = ClipData.Item(clipText)
                    val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    val data = ClipData(clipText, mimeTypes, item)

                    val dragShadowBuilder = View.DragShadowBuilder(it)
                    it.startDragAndDrop(data, dragShadowBuilder, it, 0)

                    //it.visibility = View.INVISIBLE
                    true
                }
                l++
            }
            if((findViewById<TextView>(R.id.towHeatHigh).parent as FlexboxLayout).id != R.id.source){
                findViewById<TextView>(R.id.towHeatLow).visibility = View.GONE
                findViewById<TextView>(R.id.towHeatHigh).visibility = View.VISIBLE
            }
            else if((findViewById<TextView>(R.id.towHeatLow).parent as FlexboxLayout).id != R.id.source){
                findViewById<TextView>(R.id.towHeatHigh).visibility = View.GONE
                findViewById<TextView>(R.id.towHeatLow).visibility = View.VISIBLE
            }

            val vorHFan = findViewById<TextView>(R.id.vornadoHeatFan)
            val vorHLow = findViewById<TextView>(R.id.vornadoHeatLow)
            val vorHHigh = findViewById<TextView>(R.id.vornadoHeatHigh)

            if((vorHFan.parent as View).id != R.id.source){
                vorHLow.visibility = View.GONE
                vorHHigh.visibility = View.GONE
                vorHFan.visibility = View.VISIBLE
            }
            else if((vorHLow.parent as View).id != R.id.source){
                vorHFan.visibility = View.GONE
                vorHHigh.visibility = View.GONE
                vorHLow.visibility = View.VISIBLE
            }
            if((vorHHigh.parent as View).id != R.id.source){
                vorHLow.visibility = View.GONE
                vorHFan.visibility = View.GONE
                vorHLow.visibility = View.VISIBLE
            }

            val vorFanLow = findViewById<TextView>(R.id.vornadoFanLow)
            val vorFanMed = findViewById<TextView>(R.id.vornadoFanMed)
            val vorFanHigh = findViewById<TextView>(R.id.vornadoFanHigh)

            if((vorFanLow.parent as View).id != R.id.source){
                vorFanMed.visibility = View.GONE
                vorFanHigh.visibility = View.GONE
                vorFanLow.visibility = View.VISIBLE
            }
            else if((vorFanMed.parent as View).id != R.id.source){
                vorFanLow.visibility = View.GONE
                vorFanHigh.visibility = View.GONE
                vorFanMed.visibility = View.VISIBLE
            }
            else if((vorFanHigh.parent as View).id != R.id.source){
                vorFanLow.visibility = View.GONE
                vorFanMed.visibility = View.GONE
                vorFanHigh.visibility = View.VISIBLE
            }

            /*
            //Save the locations of the dragables
            val toaster = findViewById<TextView>(R.id.toaster)
            val kettle = findViewById<TextView>(R.id.kettle)
            val towHeatHigh = findViewById<TextView>(R.id.towHeatHigh)
            val towHeatLow = findViewById<TextView>(R.id.towHeatLow)
            val vacuum = findViewById<TextView>(R.id.vacuum)
            val workComp = findViewById<TextView>(R.id.workComp)
            val laptop = findViewById<TextView>(R.id.laptop)
            val compMon = findViewById<TextView>(R.id.compMon)

            val toasterP = findViewById<FlexboxLayout>(lines[j].toInt())
            j++
            val kettleP = findViewById<FlexboxLayout>(lines[j].toInt())
            j++
            val towHeatHighP = findViewById<FlexboxLayout>(lines[j].toInt())
            j++
            val towHeatLowP = findViewById<FlexboxLayout>(lines[j].toInt())
            j++
            val vacuumP = findViewById<FlexboxLayout>(lines[j].toInt())
            j++
            val workCompP = findViewById<FlexboxLayout>(lines[j].toInt())
            j++
            val laptopP = findViewById<FlexboxLayout>(lines[j].toInt())
            j++
            val compMonP = findViewById<FlexboxLayout>(lines[j].toInt())
            j++

            if (towHeatHighP != findViewById(R.id.source)) {
                towHeatLow.visibility = View.GONE
            } else if (towHeatLowP != findViewById(R.id.source)) {
                towHeatHigh.visibility = View.GONE
            }

            val source = findViewById<FlexboxLayout>(R.id.source)

            if ((toaster.parent as FlexboxLayout) == source) {
                source.removeView(toaster)
                toasterP.addView(toaster)
            }
            if ((kettle.parent as FlexboxLayout) == source) {
                source.removeView(kettle)
                kettleP.addView(kettle)
            }
            if ((toaster.parent as FlexboxLayout) == source)
                source.removeView(toaster)
            toasterP.addView(toaster)
            if ((towHeatHigh.parent as FlexboxLayout) == source) {
                source.removeView(towHeatHigh)
                towHeatHighP.addView(towHeatHigh)
            }
            if ((towHeatLow.parent as FlexboxLayout) == source) {
                source.removeView(towHeatLow)
                towHeatLowP.addView(towHeatLow)
            }
            if ((vacuum.parent as FlexboxLayout) == source) {
                source.removeView(vacuum)
                vacuumP.addView(vacuum)
            }
            if ((workComp.parent as FlexboxLayout) == source) {
                source.removeView(workComp)
                workCompP.addView(workComp)
            }
            if ((laptop.parent as FlexboxLayout) == source) {
                source.removeView(laptop)
                laptopP.addView(laptop)
            }
            if ((compMon.parent as FlexboxLayout) == source) {
                source.removeView(compMon)
                compMonP.addView(compMon)
            }

             */

            //Save the state of the custom inputs
            findViewById<EditText>(R.id.MBRCustomInputField).setText(lines[j])
            j++
            findViewById<EditText>(R.id.referCustomInputField).setText(lines[j])
            j++
            findViewById<EditText>(R.id.GFICustomInputField).setText(lines[j])
            j++
            findViewById<EditText>(R.id.microCustomInputField).setText(lines[j])
            j++
            findViewById<EditText>(R.id.waterHeaterCustomInputField).setText(lines[j])
            j++
            findViewById<EditText>(R.id.ACCustomInputField).setText(lines[j])
            j++
            findViewById<EditText>(R.id.converterCustomInputField).setText(lines[j])
            j++
        }
        catch(e: Exception){
            //Toast.makeText(getApplicationContext(), "Problem Loading Save", Toast.LENGTH_SHORT).show()
            Toast.makeText(getApplicationContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    //External Storage
    fun extSaveClicked(v: View?) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "camperPowerSave")
        }
        startActivityForResult(intent, 1)



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == 1
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->
                // Perform operations on the document using its URI.
                try {
                    contentResolver.openFileDescriptor(uri, "w")?.use {
                        FileOutputStream(it.fileDescriptor).use {
                            //it.write(("Append Test\n").toByteArray())

                            //Save the stuff in the circuits hashmap to the bundle individually
                            var j = 0

                            for(i in circuits){
                                j++

                                it.write(("" + i.key.id + "\n").toByteArray())
                                it.write(("" + circuits[i.key]!!.ampsUsed + "\n").toByteArray())
                                it.write(("" + circuits[i.key]!!.ampCapacity + "\n").toByteArray())
                                it.write(("" + circuits[i.key]!!.userInterface.id + "\n").toByteArray())
                            }


                            it.write(("$main1Cap\n").toByteArray())
                            it.write(("$main2Cap\n").toByteArray())
                            it.write(("$main1Used\n").toByteArray())
                            it.write(("$main2Used\n").toByteArray())
                            it.write(("$lastCustomMBR\n").toByteArray())
                            it.write(("$lastCustomRefer\n").toByteArray())
                            it.write(("$lastCustomGFI\n").toByteArray())
                            it.write(("$lastCustomMicro\n").toByteArray())
                            it.write(("$lastCustomWaterHeater\n").toByteArray())
                            it.write(("$lastCustomAC\n").toByteArray())
                            it.write(("$lastCustomConverter\n").toByteArray())

                            //Save the states of the checkboxes
                            it.write(("" + findViewById<CheckBox>(R.id.powerCheck).isChecked + "\n").toByteArray())
                            it.write(("" + findViewById<CheckBox>(R.id.chargerCheck).isChecked + "\n").toByteArray())
                            it.write(("" + findViewById<CheckBox>(R.id.ACCheck).isChecked + "\n").toByteArray())
                            it.write(("" + findViewById<CheckBox>(R.id.converterCheck).isChecked + "\n").toByteArray())
                            it.write(("" + findViewById<CheckBox>(R.id.waterHeatCheck).isChecked + "\n").toByteArray())
                            it.write(("" + findViewById<CheckBox>(R.id.microCheck).isChecked + "\n").toByteArray())
                            it.write(("" + findViewById<CheckBox>(R.id.referTVCheck).isChecked + "\n").toByteArray())
                            it.write(("" + findViewById<CheckBox>(R.id.referFrigeCheck).isChecked + "\n").toByteArray())

                            it.write((components.size.toString() + "\n").toByteArray())
                            var i = 0
                            for(c in components){
                                i++
                                it.write(((c.key.parent as View).id.toString() + "\n").toByteArray())
                                it.write((c.key.id.toString() + "\n").toByteArray())
                                it.write((components[c.key]!!.ampsUsed.toString() + "\n").toByteArray())
                            }
                            /*
                            //Save the locations of the dragables
                            it.write(("" + (findViewById<View>(R.id.toaster).parent as View).id + "\n").toByteArray())
                            it.write(("" + (findViewById<View>(R.id.kettle).parent as View).id + "\n").toByteArray())
                            it.write(("" + (findViewById<View>(R.id.towHeatHigh).parent as View).id + "\n").toByteArray())
                            it.write(("" + (findViewById<View>(R.id.towHeatLow).parent as View).id + "\n").toByteArray())
                            it.write(("" + (findViewById<View>(R.id.vacuum).parent as View).id + "\n").toByteArray())
                            it.write(("" + (findViewById<View>(R.id.workComp).parent as View).id + "\n").toByteArray())
                            it.write(("" + (findViewById<View>(R.id.laptop).parent as View).id + "\n").toByteArray())
                            it.write(("" + (findViewById<View>(R.id.compMon).parent as View).id + "\n").toByteArray())

                             */

                            //Save the state of the custom inputs
                            it.write((findViewById<EditText>(R.id.MBRCustomInputField).text.toString() + "\n").toByteArray())
                            it.write((findViewById<EditText>(R.id.referCustomInputField).text.toString() + "\n").toByteArray())
                            it.write((findViewById<EditText>(R.id.GFICustomInputField).text.toString() + "\n").toByteArray())
                            it.write((findViewById<EditText>(R.id.microCustomInputField).text.toString() + "\n").toByteArray())
                            it.write((findViewById<EditText>(R.id.waterHeaterCustomInputField).text.toString() + "\n").toByteArray())
                            it.write((findViewById<EditText>(R.id.ACCustomInputField).text.toString() + "\n").toByteArray())
                            it.write((findViewById<EditText>(R.id.converterCustomInputField).text.toString() + "\n").toByteArray())
                        }
                    }
                } catch (e: FileNotFoundException) {
                    Toast.makeText(getApplicationContext(), "No Save State", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                } catch (e: IOException) {
                    Toast.makeText(getApplicationContext(), "IOException", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
        else if(requestCode == 2){
            val stringBuilder = StringBuilder()

            resultData?.data?.also { uri ->
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String? = reader.readLine()
                        while (line != null) {
                            stringBuilder.append(line + "\n")
                            line = reader.readLine()
                        }
                    }
                }
            }
            var fileInput = stringBuilder.toString()

            try {
                if (fileInput == "") {
                    Toast.makeText(getApplicationContext(), "No Save State", Toast.LENGTH_SHORT).show()
                    return
                }
                reset()

                //Save the stuff in the circuits hashmap to the bundle individually

                val lines = fileInput.split("\n").toTypedArray()


                var j = 0

                for (i in circuits) {
                    var keyID = lines[j].toInt()
                    j++
                    var ampsU = lines[j].toDouble()
                    j++
                    var ampC = lines[j].toDouble()
                    j++
                    var iID = lines[j].toInt()
                    j++

                    var theKey = findViewById<FlexboxLayout>(keyID)
                    circuits[theKey] = AmpUsage(ampC, ampsU, findViewById(iID))
                }


                main1Cap = lines[j].toDouble()
                j++
                main2Cap = lines[j].toDouble()
                j++
                main1Used = lines[j].toDouble()
                j++
                main2Used = lines[j].toDouble()
                j++
                lastCustomMBR = lines[j].toDouble()
                j++
                lastCustomRefer = lines[j].toDouble()
                j++
                lastCustomGFI = lines[j].toDouble()
                j++
                lastCustomMicro = lines[j].toDouble()
                j++
                lastCustomWaterHeater = lines[j].toDouble()
                j++
                lastCustomAC = lines[j].toDouble()
                j++
                lastCustomConverter = lines[j].toDouble()
                j++

                //Save the states of the checkboxes
                findViewById<CheckBox>(R.id.powerCheck).isChecked = lines[j].toBoolean()
                j++
                findViewById<CheckBox>(R.id.chargerCheck).isChecked = lines[j].toBoolean()
                j++
                findViewById<CheckBox>(R.id.ACCheck).isChecked = lines[j].toBoolean()
                j++
                findViewById<CheckBox>(R.id.converterCheck).isChecked = lines[j].toBoolean()
                j++
                findViewById<CheckBox>(R.id.waterHeatCheck).isChecked = lines[j].toBoolean()
                j++
                findViewById<CheckBox>(R.id.microCheck).isChecked = lines[j].toBoolean()
                j++
                findViewById<CheckBox>(R.id.referTVCheck).isChecked = lines[j].toBoolean()
                j++
                findViewById<CheckBox>(R.id.referFrigeCheck).isChecked = lines[j].toBoolean()
                j++

                var repeatTimes = lines[j].toInt()
                j++
                var i = 0
                var dupes = 0
                var parents = mutableListOf<FlexboxLayout>()
                while(i < repeatTimes){
                    i++


                    var parent = findViewById<FlexboxLayout>(lines[j].toInt())
                    j++
                    var component = findViewById<TextView>(lines[j].toInt())
                    j++
                    var used = lines[j].toDouble()
                    j++

                    if(component != null) {
                        components[component] = ElectronicComponent(used)

                        var curPar = (component.parent as FlexboxLayout)

                        curPar.removeView(component)
                        parent.addView(component)
                    }
                    else{
                        dupes++
                        parents.add(parent)
                    }
                }
                var l = 0
                numDupedItems = 0
                while(l < dupes){

                    var v = findViewById<TextView>(R.id.laptop)

                    var newText = TextView(this)

                    newText.height = v.height
                    newText.width = v.width
                    newText.textSize = 10f
                    newText.gravity = Gravity.CENTER
                    newText.background = v.background
                    newText.text = v.text
                    newText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    newText.setTextColor(v.currentTextColor)
                    newText.layoutParams = v.layoutParams

                    parents[l].addView(newText)
                    dupedComponents.add(l, newText)
                    components[newText] = ElectronicComponent(components[v]!!.ampsUsed)
                    numDupedItems++
                    newText.setOnLongClickListener {
                        val clipText = "This is our ClipData text"
                        val item = ClipData.Item(clipText)
                        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                        val data = ClipData(clipText, mimeTypes, item)

                        val dragShadowBuilder = View.DragShadowBuilder(it)
                        it.startDragAndDrop(data, dragShadowBuilder, it, 0)

                        //it.visibility = View.INVISIBLE
                        true
                    }
                    l++
                }
                if((findViewById<TextView>(R.id.towHeatHigh).parent as FlexboxLayout).id != R.id.source){
                    findViewById<TextView>(R.id.towHeatLow).visibility = View.GONE
                    findViewById<TextView>(R.id.towHeatHigh).visibility = View.VISIBLE
                }
                else if((findViewById<TextView>(R.id.towHeatLow).parent as FlexboxLayout).id != R.id.source){
                    findViewById<TextView>(R.id.towHeatHigh).visibility = View.GONE
                    findViewById<TextView>(R.id.towHeatLow).visibility = View.VISIBLE
                }

                val vorHFan = findViewById<TextView>(R.id.vornadoHeatFan)
                val vorHLow = findViewById<TextView>(R.id.vornadoHeatLow)
                val vorHHigh = findViewById<TextView>(R.id.vornadoHeatHigh)

                if((vorHFan.parent as View).id != R.id.source){
                    vorHLow.visibility = View.GONE
                    vorHHigh.visibility = View.GONE
                    vorHFan.visibility = View.VISIBLE
                }
                else if((vorHLow.parent as View).id != R.id.source){
                    vorHFan.visibility = View.GONE
                    vorHHigh.visibility = View.GONE
                    vorHLow.visibility = View.VISIBLE
                }
                if((vorHHigh.parent as View).id != R.id.source){
                    vorHLow.visibility = View.GONE
                    vorHFan.visibility = View.GONE
                    vorHLow.visibility = View.VISIBLE
                }

                val vorFanLow = findViewById<TextView>(R.id.vornadoFanLow)
                val vorFanMed = findViewById<TextView>(R.id.vornadoFanMed)
                val vorFanHigh = findViewById<TextView>(R.id.vornadoFanHigh)

                if((vorFanLow.parent as View).id != R.id.source){
                    vorFanMed.visibility = View.GONE
                    vorFanHigh.visibility = View.GONE
                    vorFanLow.visibility = View.VISIBLE
                }
                else if((vorFanMed.parent as View).id != R.id.source){
                    vorFanLow.visibility = View.GONE
                    vorFanHigh.visibility = View.GONE
                    vorFanMed.visibility = View.VISIBLE
                }
                else if((vorFanHigh.parent as View).id != R.id.source){
                    vorFanLow.visibility = View.GONE
                    vorFanMed.visibility = View.GONE
                    vorFanHigh.visibility = View.VISIBLE
                }

                /*
                //Save the locations of the dragables
                val toaster = findViewById<TextView>(R.id.toaster)
                val kettle = findViewById<TextView>(R.id.kettle)
                val towHeatHigh = findViewById<TextView>(R.id.towHeatHigh)
                val towHeatLow = findViewById<TextView>(R.id.towHeatLow)
                val vacuum = findViewById<TextView>(R.id.vacuum)
                val workComp = findViewById<TextView>(R.id.workComp)
                val laptop = findViewById<TextView>(R.id.laptop)
                val compMon = findViewById<TextView>(R.id.compMon)

                val toasterP = findViewById<FlexboxLayout>(lines[j].toInt())
                j++
                val kettleP = findViewById<FlexboxLayout>(lines[j].toInt())
                j++
                val towHeatHighP = findViewById<FlexboxLayout>(lines[j].toInt())
                j++
                val towHeatLowP = findViewById<FlexboxLayout>(lines[j].toInt())
                j++
                val vacuumP = findViewById<FlexboxLayout>(lines[j].toInt())
                j++
                val workCompP = findViewById<FlexboxLayout>(lines[j].toInt())
                j++
                val laptopP = findViewById<FlexboxLayout>(lines[j].toInt())
                j++
                val compMonP = findViewById<FlexboxLayout>(lines[j].toInt())
                j++

                if (towHeatHighP != findViewById(R.id.source)) {
                    towHeatLow.visibility = View.GONE
                } else if (towHeatLowP != findViewById(R.id.source)) {
                    towHeatHigh.visibility = View.GONE
                }

                val source = findViewById<FlexboxLayout>(R.id.source)

                if ((toaster.parent as FlexboxLayout) == source) {
                    source.removeView(toaster)
                    toasterP.addView(toaster)
                }
                if ((kettle.parent as FlexboxLayout) == source) {
                    source.removeView(kettle)
                    kettleP.addView(kettle)
                }
                if ((toaster.parent as FlexboxLayout) == source)
                    source.removeView(toaster)
                toasterP.addView(toaster)
                if ((towHeatHigh.parent as FlexboxLayout) == source) {
                    source.removeView(towHeatHigh)
                    towHeatHighP.addView(towHeatHigh)
                }
                if ((towHeatLow.parent as FlexboxLayout) == source) {
                    source.removeView(towHeatLow)
                    towHeatLowP.addView(towHeatLow)
                }
                if ((vacuum.parent as FlexboxLayout) == source) {
                    source.removeView(vacuum)
                    vacuumP.addView(vacuum)
                }
                if ((workComp.parent as FlexboxLayout) == source) {
                    source.removeView(workComp)
                    workCompP.addView(workComp)
                }
                if ((laptop.parent as FlexboxLayout) == source) {
                    source.removeView(laptop)
                    laptopP.addView(laptop)
                }
                if ((compMon.parent as FlexboxLayout) == source) {
                    source.removeView(compMon)
                    compMonP.addView(compMon)
                }

                 */

                //Save the state of the custom inputs
                findViewById<EditText>(R.id.MBRCustomInputField).setText(lines[j])
                j++
                findViewById<EditText>(R.id.referCustomInputField).setText(lines[j])
                j++
                findViewById<EditText>(R.id.GFICustomInputField).setText(lines[j])
                j++
                findViewById<EditText>(R.id.microCustomInputField).setText(lines[j])
                j++
                findViewById<EditText>(R.id.waterHeaterCustomInputField).setText(lines[j])
                j++
                findViewById<EditText>(R.id.ACCustomInputField).setText(lines[j])
                j++
                findViewById<EditText>(R.id.converterCustomInputField).setText(lines[j])
                j++

            }
            catch(e: Exception){
                Toast.makeText(getApplicationContext(), "Problem Loading Save", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun extLoadClicked(v: View?){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, 2)


    }
    fun blink(v: View?){
        var color = colors[v] as Int
        val anim = ObjectAnimator.ofInt(v, "backgroundColor", color, Color.RED, color)
        anim.duration = 250
        anim.setEvaluator(ArgbEvaluator())
        anim.repeatMode = ValueAnimator.REVERSE
        anim.repeatCount = 2
        anim.start()
    }

    fun resetClicked(v:View?){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reset Confirmation")
        builder.setMessage("Are you sure you want to reset?")
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton("Yes") { dialog, which ->
            reset()

        }

        builder.setNegativeButton("No") { dialog, which ->

        }
        builder.show()
    }
    fun reset(){
        for(i in circuits){
            circuits[i.key] = AmpUsage(circuits[i.key]!!.ampCapacity, 0.0, circuits[i.key]!!.userInterface)
        }


        main1Cap = 30.0
        main2Cap = 30.0
        main1Used = 0.0
        main2Used = 0.0
        lastCustomMBR = 0.0
        lastCustomRefer = 0.0
        lastCustomGFI = 0.0
        lastCustomMicro = 0.0
        lastCustomWaterHeater = 0.0
        lastCustomAC = 0.0
        lastCustomConverter = 0.0

        //Save the states of the checkboxes
        findViewById<CheckBox>(R.id.powerCheck).isChecked = false
        findViewById<CheckBox>(R.id.chargerCheck).isChecked = true
        chargerClicked(findViewById<CheckBox>(R.id.chargerCheck))
        findViewById<CheckBox>(R.id.ACCheck).isChecked = false
        findViewById<CheckBox>(R.id.converterCheck).isChecked = true
        converterClicked(findViewById<CheckBox>(R.id.converterCheck))
        findViewById<CheckBox>(R.id.waterHeatCheck).isChecked = false
        findViewById<CheckBox>(R.id.microCheck).isChecked = false
        findViewById<CheckBox>(R.id.referTVCheck).isChecked = false
        findViewById<CheckBox>(R.id.referFrigeCheck).isChecked = true
        referFrigeClicked(findViewById<CheckBox>(R.id.referFrigeCheck))

        var removeQueue = mutableListOf<TextView>()

        var source = findViewById<FlexboxLayout>(R.id.source)
        //var i = 0
        for(i in components){
            var component = i.key


            var curPar = (component.parent as FlexboxLayout)

            curPar.removeView(component)
            source.addView(component)

            if(i.key in dupedComponents) {
                removeQueue.add(i.key)
            }
        }
        for(i in removeQueue){
            dupedComponents.remove(i)
            numDupedItems--
            i.visibility = View.GONE
            components.remove(i)
            source.removeView(i)
        }

        findViewById<TextView>(R.id.towHeatLow).visibility = View.VISIBLE
        findViewById<TextView>(R.id.towHeatHigh).visibility = View.VISIBLE
        findViewById<TextView>(R.id.vornadoHeatHigh).visibility = View.VISIBLE
        findViewById<TextView>(R.id.vornadoHeatLow).visibility = View.VISIBLE
        findViewById<TextView>(R.id.vornadoHeatFan).visibility = View.VISIBLE
        findViewById<TextView>(R.id.vornadoFanLow).visibility = View.VISIBLE
        findViewById<TextView>(R.id.vornadoFanMed).visibility = View.VISIBLE
        findViewById<TextView>(R.id.vornadoFanHigh).visibility = View.VISIBLE

        //Save the state of the custom inputs
        findViewById<EditText>(R.id.MBRCustomInputField).setText("")
        findViewById<EditText>(R.id.referCustomInputField).setText("")
        findViewById<EditText>(R.id.GFICustomInputField).setText("")
        findViewById<EditText>(R.id.microCustomInputField).setText("")
        findViewById<EditText>(R.id.waterHeaterCustomInputField).setText("")
        findViewById<EditText>(R.id.ACCustomInputField).setText("")
        findViewById<EditText>(R.id.converterCustomInputField).setText("")
    }

    fun round(x: Double): String{
        var result = x.toString()

        var roundVal = (x * 1000) % 10
        var num = x
        if(roundVal >= 5){
            num += 0.01
        }

        if(num > 0.001){
            var splitUp = num.toString().split(".").toTypedArray()

            if(splitUp[1].length > 1) {
                result = splitUp[0] + "." + splitUp[1][0] + splitUp[1][1]
            }
            else{
                result = splitUp[0] + "." + splitUp[1][0]
            }
        }
        else{
            result = "0.0"
        }




        return result

    }
}

