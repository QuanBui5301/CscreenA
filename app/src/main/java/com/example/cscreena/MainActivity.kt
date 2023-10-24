package com.example.cscreena

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.InputStream

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var dialog: AlertDialog
    private lateinit var btnStart: ImageButton
    private lateinit var btnRecognize: Button
    private lateinit var btnTranslate: Button
    private lateinit var imageTran: ImageView
    private lateinit var translateText: EditText
    private lateinit var translated: EditText
    private lateinit var textRegconizer: TextRecognizer
    private lateinit var recognizercn: TextRecognizer
    private lateinit var recognizerjp: TextRecognizer
    private lateinit var recognizerkr: TextRecognizer
    private lateinit var progressDialog: ProgressDialog
    lateinit var recognizetxt:String
    private val mGalleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val inputStream : InputStream = getContentResolver().openInputStream(result.data!!.data!!)!!
                val imageBitmap : Bitmap = BitmapFactory.decodeStream(inputStream)
                mProfileBitmap = imageBitmap
                val inputImage = InputImage.fromBitmap(mProfileBitmap!!, 0)
                imageTran.setImageBitmap(mProfileBitmap)
                if (recognizetxt == "ENGLISH") {
                    val textTaskResult = textRegconizer.process(inputImage)
                        .addOnSuccessListener { text ->
                            val recognizedText = text.text
                            translateText.setText(recognizedText)
                        }
                        .addOnFailureListener { e->
                            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show()
                        }
                }
                if (recognizetxt == "CHINESE") {
                    val textTaskResult = recognizercn.process(inputImage)
                        .addOnSuccessListener { text ->
                            val recognizedText = text.text
                            translateText.setText(recognizedText)
                        }
                        .addOnFailureListener { e->
                            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show()
                        }
                }
                if (recognizetxt == "JAPANESE") {
                    val textTaskResult = recognizerjp.process(inputImage)
                        .addOnSuccessListener { text ->
                            val recognizedText = text.text
                            translateText.setText(recognizedText)
                        }
                        .addOnFailureListener { e->
                            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show()
                        }
                }
                if (recognizetxt == "KOREAN") {
                    val textTaskResult = recognizerkr.process(inputImage)
                        .addOnSuccessListener { text ->
                            val recognizedText = text.text
                            translateText.setText(recognizedText)
                        }
                        .addOnFailureListener { e->
                            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    private val mCameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val imageBitmap: Bitmap = intent?.extras?.get("data") as Bitmap
                mProfileBitmap = imageBitmap
                val inputImage = InputImage.fromBitmap(mProfileBitmap!!, 0)
                imageTran.setImageBitmap(imageBitmap)
                if (recognizetxt == "ENGLISH") {
                    val textTaskResult = textRegconizer.process(inputImage)
                        .addOnSuccessListener { text ->
                            val recognizedText = text.text
                            translateText.setText(recognizedText)
                        }
                        .addOnFailureListener { e->
                            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show()
                        }
                }
                if (recognizetxt == "CHINESE") {
                    val textTaskResult = recognizercn.process(inputImage)
                        .addOnSuccessListener { text ->
                            val recognizedText = text.text
                            translateText.setText(recognizedText)
                        }
                        .addOnFailureListener { e->
                            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show()
                        }
                }
                if (recognizetxt == "JAPANESE") {
                    val textTaskResult = recognizerjp.process(inputImage)
                        .addOnSuccessListener { text ->
                            val recognizedText = text.text
                            translateText.setText(recognizedText)
                        }
                        .addOnFailureListener { e->
                            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show()
                        }
                }
                if (recognizetxt == "KOREAN") {
                    val textTaskResult = recognizerkr.process(inputImage)
                        .addOnSuccessListener { text ->
                            val recognizedText = text.text
                            translateText.setText(recognizedText)
                        }
                        .addOnFailureListener { e->
                            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    var mProfileBitmap : Bitmap? = null
    private val mConverterImg : BitmapConverter = BitmapConverter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnStart = findViewById(R.id.btnStart)
        btnRecognize = findViewById(R.id.translate)
        btnTranslate = findViewById(R.id.translate1)
        imageTran = findViewById(R.id.image)
        translateText = findViewById(R.id.text_recognize)
        translated = findViewById(R.id.text_translate)
        val recognize: Spinner = findViewById(R.id.spinner1)
        val adapterlan:ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this,
            R.array.language, android.R.layout.simple_spinner_item)
        adapterlan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        recognize.setAdapter(adapterlan)
        recognize.setOnItemSelectedListener(this)
        progressDialog = ProgressDialog(this)
        if (isServiceRunning()) {
            stopService(Intent(this@MainActivity, FloatingIcon::class.java))
        }
        btnStart.setOnClickListener() {
            if(checkOverlayPermission()) {
                startService(Intent(this@MainActivity, FloatingIcon::class.java))
                finish()
            }else {
                requestFloatingIcon()
            }
        }

        textRegconizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizercn = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
        recognizerjp = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
        recognizerkr = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

        btnRecognize.setOnClickListener() {
            translated.setText("")
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Select Image")
            builder.setMessage("Choose your option?")
            builder.setPositiveButton("Gallery") {dialog : DialogInterface, which : Int ->
                galleryAvatar()
                dialog.dismiss()
            }
            builder.setNegativeButton("Camera") {dialog : DialogInterface, which : Int ->
                pickAvatar()
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()
        }
        val optionsen = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.VIETNAMESE)
            .build()
        val optionscn = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.CHINESE)
            .setTargetLanguage(TranslateLanguage.VIETNAMESE)
            .build()
        val optionskr = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.KOREAN)
            .setTargetLanguage(TranslateLanguage.VIETNAMESE)
            .build()
        val optionsjp = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.JAPANESE)
            .setTargetLanguage(TranslateLanguage.VIETNAMESE)
            .build()
        val englishVietTranslator = Translation.getClient(optionsen)
        val chineseVietTranslator = Translation.getClient(optionscn)
        val koreanVietTranslator = Translation.getClient(optionskr)
        val japaneseVietTranslator = Translation.getClient(optionsjp)

        btnTranslate.setOnClickListener() {
            progressDialog.setMessage("Translating text...")
            if (recognizetxt == "ENGLISH") {
                var conditionsen = DownloadConditions.Builder()
                    .requireWifi()
                    .build()
                englishVietTranslator.downloadModelIfNeeded(conditionsen)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
                englishVietTranslator.translate(translateText.text.toString())
                    .addOnSuccessListener { translatedText ->
                        progressDialog.dismiss()
                        translated.setText(translatedText.toString())
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
            }
            if (recognizetxt == "CHINESE") {
                var conditionscn = DownloadConditions.Builder()
                    .requireWifi()
                    .build()
                chineseVietTranslator.downloadModelIfNeeded(conditionscn)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
                chineseVietTranslator.translate(translateText.text.toString())
                    .addOnSuccessListener { translatedText ->
                        progressDialog.dismiss()
                        translated.setText(translatedText.toString())
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
            }
            if (recognizetxt == "KOREAN") {
                var conditionskr = DownloadConditions.Builder()
                    .requireWifi()
                    .build()
                koreanVietTranslator.downloadModelIfNeeded(conditionskr)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
                koreanVietTranslator.translate(translateText.text.toString())
                    .addOnSuccessListener { translatedText ->
                        progressDialog.dismiss()
                        translated.setText(translatedText.toString())
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
            }
            if (recognizetxt == "JAPANESE") {
                var conditionsjp = DownloadConditions.Builder()
                    .requireWifi()
                    .build()
                japaneseVietTranslator.downloadModelIfNeeded(conditionsjp)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
                japaneseVietTranslator.translate(translateText.text.toString())
                    .addOnSuccessListener { translatedText ->
                        progressDialog.dismiss()
                        translated.setText(translatedText.toString())
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    private fun isServiceRunning(): Boolean{
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for(service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (FloatingIcon::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }
    private fun requestFloatingIcon(){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle("Screen Overlay Permission Needed")
        builder.setMessage("Enable 'Display over the App' from settings")
        builder.setPositiveButton("Open Settings", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, RESULT_OK)
        })
        dialog = builder.create()
        dialog.show()
    }
    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        }
        else return true
    }
    private fun galleryAvatar() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        mGalleryResultLauncher.launch(galleryIntent)
    }
    private fun pickAvatar() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        mCameraResultLauncher.launch(cameraIntent)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val txt:String = parent?.getItemAtPosition(position).toString()
        recognizetxt = txt
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}
