package com.example.launguagetranslatorapp

import android.app.Activity
import android.content.Intent
import android.icu.lang.UCharacter.LineBreak.JV
import android.os.Build.VERSION_CODES.S
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import org.jetbrains.annotations.Nullable
import java.lang.Exception
import java.net.Authenticator
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fromLanguage = arrayOf<String>(
                "From", "English", "Hindi", "Spanish", "Arabic", "Portuguese", "Bengali", "Russian", "Japanese",
                "German", "Telugu", "Korean", "French", "Marathi", "Tamil", "Urdu", "Turkish", "Italian", "Thai",
                "Gujarati", "Persian", "Kannada", "Ukrainian", "Romanian", "Dutch", "Greek")

        val toLanguage = arrayOf<String>(
                "To", "Hindi", "English", "Spanish", "Arabic", "Portuguese", "Bengali", "Russian", "Japanese",
                "German", "Telugu", "Korean", "French", "Marathi", "Tamil", "Urdu", "Turkish", "Italian", "Thai",
                "Gujarati", "Persian", "Kannada", "Ukrainian", "Romanian", "Dutch", "Greek")

        val requestPermissionCode: Int = 1
        val languageCode: Int = 0
        var fromLanguageCode: Int = 0
        var toLanguageCode: Int = 0

        val fromSpinner = findViewById<Spinner>(R.id.idFromSpinner)
        val toSpinner = findViewById<Spinner>(R.id.idToSpinner)
        val sourceEdt = findViewById<TextInputEditText>(R.id.idEdtSource)

        val translateBtn = findViewById<MaterialButton>(R.id.idBtnTranslate)
        val translatedTV = findViewById<TextView>(R.id.idTVTranslatedTV)

        //Array Adapter for fromSpinner
        fromSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                fromLanguageCode = getLanguageCode(fromLanguage[position]);
            }


            override fun onNothingSelected(parent: AdapterView<*>) {
                fromLanguageCode = getLanguageCode(fromLanguage[1]);

            }
        }

        val fromAdapter:ArrayAdapter<*>
        fromAdapter = ArrayAdapter(this , R.layout.spinner_item,fromLanguage)
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromSpinner.adapter = fromAdapter

        //Array Adapter for toSpinner
        toSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                toLanguageCode = getLanguageCode(toLanguage[position]);
            }


            override fun onNothingSelected(parent: AdapterView<*>) {
                /*Do something if nothing selected*/
                toLanguageCode = getLanguageCode(toLanguage[1]);
            }
        }

        val toAdapter:ArrayAdapter<*>
        toAdapter = ArrayAdapter(this , R.layout.spinner_item,toLanguage)
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        toSpinner.adapter = toAdapter

        //working on translate Button
        translateBtn.setOnClickListener {
            translatedTV.setText("")
            if(sourceEdt.getText().toString().isEmpty()) {
                Toast.makeText(this@MainActivity, "Please enter your text to Translate", Toast.LENGTH_SHORT).show()
            }
            else if (fromLanguageCode==0){
                Toast.makeText(this@MainActivity, "Please select source Language", Toast.LENGTH_SHORT).show()
            }
            else if (toLanguageCode==0) {
                Toast.makeText(this@MainActivity, "Please select the Language to Translate", Toast.LENGTH_SHORT).show()
            }
            else {
                translateText(fromLanguageCode, toLanguageCode, sourceEdt.getText().toString())
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var sourceEdt = findViewById<TextInputEditText>(R.id.idEdtSource)
        super.onActivityResult(requestCode, resultCode, data)
        val requestPermissionCode: Int = 1
        if(requestCode == requestPermissionCode){
            if(resultCode == RESULT_OK && data != null){
                var result = ArrayList<String>()
                result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
                sourceEdt.setText(result.get(0))
            }
        }
    }

    private fun translateText(fromLanguageCode: Int, toLanguageCode:Int, Source:String) {
        var translatedTV = findViewById<TextView>(R.id.idTVTranslatedTV)
        translatedTV.setText("Downloading Model...")
        val options = FirebaseTranslatorOptions. Builder ()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build()
        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
        val conditions = FirebaseModelDownloadConditions. Builder ().build()
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    // Model downloaded successfully. Okay to start translating.
                    // (Set a flag, unhide the translation UI, etc.)
                    translatedTV.setText("Translating...")
                    translator.translate(Source).addOnSuccessListener{ translatedString ->
                        translatedTV.setText(translatedString)
                    }


                }
                .addOnFailureListener { exception ->
                    // Model couldn’t be downloaded or other internal error.
                    // ...
                    Toast.makeText(this@MainActivity, "Fail to Translate: " + exception.message, Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this@MainActivity, "Fail to Download: "+exception.message, Toast.LENGTH_SHORT).show()
                }

    }

    public fun getLanguageCode(language: String): Int {
        var languageCode = 0
        when(language) {
            "English" -> languageCode = FirebaseTranslateLanguage.EN;
            "Hindi" -> languageCode = FirebaseTranslateLanguage.HI;
            "Spanish" -> languageCode = FirebaseTranslateLanguage.ES;
            "Arabic" -> languageCode = FirebaseTranslateLanguage.AR;
            "Portuguese" -> languageCode = FirebaseTranslateLanguage.PT;
            "Bengali" -> languageCode = FirebaseTranslateLanguage.BN;
            "Russian" -> languageCode = FirebaseTranslateLanguage.RU;
            "Japanese" -> languageCode = FirebaseTranslateLanguage.JA;
            "German" -> languageCode = FirebaseTranslateLanguage.DE;
            "Telugu" -> languageCode = FirebaseTranslateLanguage.TE;
            "Korean" -> languageCode = FirebaseTranslateLanguage.KO;
            "French" -> languageCode = FirebaseTranslateLanguage.FR;
            "Marathi" -> languageCode = FirebaseTranslateLanguage.MR;
            "Tamil" -> languageCode = FirebaseTranslateLanguage.TA;
            "Urdu" -> languageCode = FirebaseTranslateLanguage.UR;
            "Turkish" -> languageCode = FirebaseTranslateLanguage.TR;
            "Italian" -> languageCode = FirebaseTranslateLanguage.IT;
            "Thai" -> languageCode = FirebaseTranslateLanguage.TH;
            "Gujarati" -> languageCode = FirebaseTranslateLanguage.GU;
            "Persian" -> languageCode = FirebaseTranslateLanguage.FA;
            "Kannada" -> languageCode = FirebaseTranslateLanguage.KN;
            "Ukrainian" -> languageCode = FirebaseTranslateLanguage.UK;
            "Romanian" -> languageCode = FirebaseTranslateLanguage.RO;
            "Dutch" -> languageCode = FirebaseTranslateLanguage.NL;
            "Greek" -> languageCode = FirebaseTranslateLanguage.EL;

        }
        return languageCode;
    }

}