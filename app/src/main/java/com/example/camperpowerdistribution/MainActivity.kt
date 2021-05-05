package com.example.camperpowerdistribution

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.DragEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.FlexboxLayout
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    var genericComponents = mutableListOf<TextView>()
    var components = HashMap<TextView, ElectronicComponent>(30)
    var circuits = HashMap<FlexboxLayout, AmpUsage>(8)
    var dupedComponents = mutableListOf<TextView>()
    var numDupedItems = 0
    var main1Cap = 30.0
    var main2Cap = 30.0
    var main1Used = 0.0
    var main2Used = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var lastCustomMBR = 0.0
        var lastCustomRefer = 0.0
        var lastCustomGFI = 0.0
        var lastCustomMicro = 0.0
        var lastCustomWaterHeater = 0.0
        var lastCustomAC = 0.0
        var lastCustomConverter = 0.0

        //Non-Droppable Views
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
        val main1Micro: FlexboxLayout = findViewById(R.id.main1Micro)
        val main2WaterHeater: FlexboxLayout = findViewById(R.id.main2WaterHeater)
        val main2AC: FlexboxLayout = findViewById(R.id.main2AC)
        val main2Converter: FlexboxLayout = findViewById(R.id.main2Converter)



        circuits[main1MBR] = AmpUsage(15.0, 0.0, circuitMBRCap)
        circuits[main1Refer] = AmpUsage(15.0, 0.0, circuitReferCap)
        circuits[main1GFI] = AmpUsage(15.0, 0.0, circuitGFICap)
        circuits[main1Micro] = AmpUsage(15.0, 0.0, circuitMicroCap)
        circuits[main2WaterHeater] = AmpUsage(15.0, 0.0, circuitWaterHeaterCap)
        circuits[main2AC] = AmpUsage(20.0, 0.0, circuitACCap)
        circuits[main2Converter] = AmpUsage(15.0, 0.0, circuitConverterCap)


        components[findViewById(R.id.toaster)] = ElectronicComponent(12.5)
        components[findViewById(R.id.kettle)] = ElectronicComponent(11.0)
        components[findViewById(R.id.towHeatHigh)] = ElectronicComponent(11.0)
        components[findViewById(R.id.towHeatLow)] = ElectronicComponent(4.0)
        components[findViewById(R.id.batChar)] = ElectronicComponent(6.0)
        components[findViewById(R.id.vacuum)] = ElectronicComponent(4.0)
        components[findViewById(R.id.tankHeat)] = ElectronicComponent(2.0)
        components[findViewById(R.id.refrigerator)] = ElectronicComponent(2.0)
        components[findViewById(R.id.workComp)] = ElectronicComponent(2.5)
        components[findViewById(R.id.laptop)] = ElectronicComponent(1.5)
        components[findViewById(R.id.compMon)] = ElectronicComponent(1.5)
        components[findViewById(R.id.tv)] = ElectronicComponent(1.5)

        //Generic power consumers
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

        this.findViewById<View>(android.R.id.content).rootView.setOnDragListener(inertDragListener)

        //Droppable
        source.setOnDragListener(dragListener)
        main1MBR.setOnDragListener(dragListener)
        main1Refer.setOnDragListener(dragListener)
        main1GFI.setOnDragListener(dragListener)
        main1Micro.setOnDragListener(dragListener)
        main2WaterHeater.setOnDragListener(dragListener)
        main2AC.setOnDragListener(dragListener)
        main2Converter.setOnDragListener(dragListener)

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

                if(customInputMBR.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputMBR.text.toString().toDouble()
                }
                circuits[main1MBR]!!.ampsUsed -= (lastCustomMBR - customInput)
                main1Used -= (lastCustomMBR - customInput)
                lastCustomMBR = customInput
                circuits[main1MBR]!!.userInterface.text = "Amps vs Rated Amps: " +
                        circuits[main1MBR]!!.ampsUsed.toString() + "/" +
                        circuits[main1MBR]!!.ampCapacity.toString()

                main1AmpUsageM.text = "Amps vs Rated Amps: " + main1Used.toString() + "/" +
                        main1Cap.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        customInputRefer.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var customInput: Double = 0.0

                if(customInputRefer.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputRefer.text.toString().toDouble()
                }
                circuits[main1Refer]!!.ampsUsed -= (lastCustomRefer - customInput)
                main1Used -= (lastCustomRefer - customInput)
                lastCustomRefer = customInput
                circuits[main1Refer]!!.userInterface.text = "Amps vs Rated Amps: " +
                        circuits[main1Refer]!!.ampsUsed.toString() + "/" +
                        circuits[main1Refer]!!.ampCapacity.toString()

                main1AmpUsageM.text = "Amps vs Rated Amps: " + main1Used.toString() + "/" +
                        main1Cap.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        customInputGFI.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var customInput: Double = 0.0

                if(customInputGFI.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputGFI.text.toString().toDouble()
                }
                circuits[main1GFI]!!.ampsUsed -= (lastCustomGFI - customInput)
                main1Used -= (lastCustomGFI - customInput)
                lastCustomGFI = customInput
                circuits[main1GFI]!!.userInterface.text = "Amps vs Rated Amps: " +
                        circuits[main1GFI]!!.ampsUsed.toString() + "/" +
                        circuits[main1GFI]!!.ampCapacity.toString()

                main1AmpUsageM.text = "Amps vs Rated Amps: " + main1Used.toString() + "/" +
                        main1Cap.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        customInputMicro.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var customInput: Double = 0.0

                if(customInputMicro.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputMicro.text.toString().toDouble()
                }
                circuits[main1Micro]!!.ampsUsed -= (lastCustomMicro - customInput)
                main1Used -= (lastCustomMicro - customInput)
                lastCustomMicro = customInput
                circuits[main1Micro]!!.userInterface.text = "Amps vs Rated Amps: " +
                        circuits[main1Micro]!!.ampsUsed.toString() + "/" +
                        circuits[main1Micro]!!.ampCapacity.toString()

                main1AmpUsageM.text = "Amps vs Rated Amps: " + main1Used.toString() + "/" +
                        main1Cap.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        customInputWaterHeater.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var customInput: Double = 0.0

                if(customInputWaterHeater.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputWaterHeater.text.toString().toDouble()
                }
                circuits[main2WaterHeater]!!.ampsUsed -= (lastCustomWaterHeater - customInput)
                main2Used -= (lastCustomWaterHeater - customInput)
                lastCustomWaterHeater = customInput
                circuits[main2WaterHeater]!!.userInterface.text = "Amps vs Rated Amps: " +
                        circuits[main2WaterHeater]!!.ampsUsed.toString() + "/" +
                        circuits[main2WaterHeater]!!.ampCapacity.toString()

                main2AmpUsageM.text = "Amps vs Rated Amps: " + main2Used.toString() + "/" +
                        main2Cap.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        customInputAC.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var customInput: Double = 0.0

                if(customInputAC.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputAC.text.toString().toDouble()
                }
                circuits[main2AC]!!.ampsUsed -= (lastCustomAC - customInput)
                main2Used -= (lastCustomAC - customInput)
                lastCustomAC = customInput
                circuits[main2AC]!!.userInterface.text = "Amps vs Rated Amps: " +
                        circuits[main2AC]!!.ampsUsed.toString() + "/" +
                        circuits[main2AC]!!.ampCapacity.toString()

                main2AmpUsageM.text = "Amps vs Rated Amps: " + main2Used.toString() + "/" +
                        main2Cap.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        customInputConverter.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var customInput: Double = 0.0

                if(customInputConverter.text.toString().toDoubleOrNull() != null) {
                    customInput = customInputConverter.text.toString().toDouble()
                }
                circuits[main2Converter]!!.ampsUsed -= (lastCustomConverter - customInput)
                main2Used -= (lastCustomConverter - customInput)
                lastCustomConverter = customInput
                circuits[main2Converter]!!.userInterface.text = "Amps vs Rated Amps: " +
                        circuits[main2Converter]!!.ampsUsed.toString() + "/" +
                        circuits[main2Converter]!!.ampCapacity.toString()

                main2AmpUsageM.text = "Amps vs Rated Amps: " + main2Used.toString() + "/" +
                        main2Cap.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        //This is a test



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

                if(destination.id != owner.id){
                    if((destination.parent as View).id == R.id.mainC1 &&
                            (owner.parent as View).id == R.id.mainC2){
                        val amps = components[v]!!.ampsUsed
                        main1Used += amps
                        main2Used -= amps
                    }
                    else if((destination.parent as View).id == R.id.mainC2 &&
                            (owner.parent as View).id == R.id.mainC1){
                        val amps = components[v]!!.ampsUsed
                        main2Used += amps
                        main1Used -= amps
                    }
                    else if(owner.id == R.id.source &&
                            (destination.parent as View).id == R.id.mainC1){
                        val amps = components[v]!!.ampsUsed
                        main1Used += amps
                    }
                    else if(owner.id == R.id.source &&
                            (destination.parent as View).id == R.id.mainC2){
                        val amps = components[v]!!.ampsUsed
                        main2Used += amps
                    }
                    else if((owner.parent as View).id == R.id.mainC1 && destination.id == R.id.source){
                        val amps = components[v]!!.ampsUsed
                        main1Used -= amps
                    }
                    else if((owner.parent as View).id == R.id.mainC2 && destination.id == R.id.source){
                        val amps = components[v]!!.ampsUsed
                        main2Used -= amps
                    }



                    findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " +
                            main1Used.toString() + "/" + main1Cap.toString()
                    findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " +
                            main2Used.toString() + "/" + main2Cap.toString()
                }
                if(circuits.containsKey(destination)){
                    val amps = components[v]!!.ampsUsed
                    val ampCap = circuits[destination]!!.ampCapacity
                    circuits[destination]!!.ampsUsed += amps
                    val ampUsed = circuits[destination]!!.ampsUsed
                    circuits[destination]!!.userInterface.text = "Amps vs Rated Amps: " + ampUsed.toString() + "/" + ampCap.toString()
                }
                if(circuits.containsKey(owner)){
                    val amps = components[v]!!.ampsUsed
                    val ampCap = circuits[owner]!!.ampCapacity
                    circuits[owner]!!.ampsUsed -= amps
                    val ampUsed = circuits[owner]!!.ampsUsed
                    circuits[owner]!!.userInterface.text = "Amps vs Rated Amps: " + ampUsed.toString() + "/" + ampCap.toString()
                }

                if (owner.id == R.id.source && destination.id != owner.id) {
                    if (genericComponents.contains(v)) {
                        val newText = TextView(this)

                        newText.height = v.height
                        newText.width = v.width
                        newText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                        newText.background = v.background
                        newText.text = v.text.toString()
                        newText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        newText.setTextColor(v.currentTextColor)

                        destination.addView(newText)
                        dupedComponents.add(numDupedItems, newText)
                        newText.setOnLongClickListener {
                            val clipText = "This is our ClipData text"
                            val item = ClipData.Item(clipText)
                            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            val data = ClipData(clipText, mimeTypes, item)

                            val dragShadowBuilder = View.DragShadowBuilder(it)
                            it.startDragAndDrop(data, dragShadowBuilder, it, 0)

                            it.visibility = View.INVISIBLE
                            true
                        }
                        val amps = components[v]!!.ampsUsed
                        components[newText] = ElectronicComponent(amps)
                        movedItem = newText
                        numDupedItems++
                    } else {
                        owner.removeView(v)
                        destination.addView(v)
                        movedItem = v
                    }
                } else {
                    owner.removeView(v)
                    destination.addView(v)
                    movedItem = v

                }

                if (destination.id == R.id.source && owner.id != destination.id
                    && dupedComponents.contains(v)
                ) {
                    dupedComponents.remove(v)
                    numDupedItems--
                    v.visibility = View.GONE
                    components.remove(v)
                    destination.removeView(v)
                }
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
        findViewById<TextView>(R.id.mainC1Cap).text = "Amps vs Rated Amps: " +
                main1Used.toString() + "/" + main1Cap.toString()
        findViewById<TextView>(R.id.mainC2Cap).text = "Amps vs Rated Amps: " +
                main2Used.toString() + "/" + main2Cap.toString()
    }
}