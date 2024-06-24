package com.example.learning

import android.annotation.SuppressLint
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiModelActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "manu.tflite"

    private lateinit var resultText: TextView
    private lateinit var ProductionVolume: EditText
    private lateinit var ProductionCost: EditText
    private lateinit var SupplierQuality: EditText
    private lateinit var DeliveryDelay: EditText
    private lateinit var DefectRate: EditText
    private lateinit var QualityScore: EditText
    private lateinit var MaintenanceHours: EditText
    private lateinit var DowntimePercentage: EditText
    private lateinit var InventoryTurnover: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi_model)

        resultText = findViewById(R.id.txtResult)
        ProductionVolume = findViewById(R.id.ProductionVolume)
        ProductionCost = findViewById(R.id.ProductionCost)
        SupplierQuality = findViewById(R.id.SupplierQuality)
        DeliveryDelay = findViewById(R.id.DeliveryDelay)
        DefectRate = findViewById(R.id.DefectRate)
        QualityScore = findViewById(R.id.QualityScore)
        MaintenanceHours = findViewById(R.id.MaintenanceHours)
        DowntimePercentage = findViewById(R.id.DowntimePercentage)
        InventoryTurnover = findViewById(R.id.InventoryTurnover)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                ProductionVolume.text.toString(),
                ProductionCost.text.toString(),
                SupplierQuality.text.toString(),
                DeliveryDelay.text.toString(),
                DefectRate.text.toString(),
                QualityScore.text.toString(),
                MaintenanceHours.text.toString(),
                DowntimePercentage.text.toString(),
                InventoryTurnover.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Low Defects"
                }else if (result == 1){
                    resultText.text = "High Defects"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(10)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String, input8: String, input9: String): Int{
        val inputVal = FloatArray(9)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        inputVal[7] = input8.toFloat()
        inputVal[8] = input9.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}