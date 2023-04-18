package edu.singaporetech.csc2007team06.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.singaporetech.csc2007team06.R
import edu.singaporetech.csc2007team06.adapters.ViewHistoryAdaptor
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.viewmodels.EndoscopeViewModel
import edu.singaporetech.csc2007team06.viewmodels.WasherViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ViewHistoryActivity : MainBaseActivity() {
    private val adapter = ViewHistoryAdaptor(arrayListOf())

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewhistory)

        // Underline textViewPastHistory
        val textViewPastHistory = findViewById<TextView>(R.id.textViewPastHistory)
        textViewPastHistory.paintFlags = textViewPastHistory.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Adapter for the list of past history
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewHistory)

        // Get the washer and endoscope object from the previous activity
        val washerViewModel =
            ViewModelProvider(this)[WasherViewModel::class.java] // WasherViewModel
        val endoscopeViewModel =
            ViewModelProvider(this)[EndoscopeViewModel::class.java] // EndoscopeViewModel


        val textViewTitle = findViewById<TextView>(R.id.textViewTitle)
        val imageViewBack = findViewById<android.widget.ImageView>(R.id.imageViewBack)
        // set up back button
        imageViewBack.setOnClickListener {
            finish()
        }

        // Get the washerId and endoscopeId from the previous activity
        val washerId = intent.getStringExtra("washerId")
        val endescopeId = intent.getStringExtra("endoscopeId")


        // Set the adapter to the histories recyclerView
        recyclerView.adapter = adapter // Set the adapter to the recyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(this@ViewHistoryActivity) // Set the layout manager to the recyclerView

        if (washerId != null) {
            washerViewModel.washer(washerId)
            washerViewModel.washerStatus.observe(this) {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val washer = it.data
                        if (washer != null && washer.history != null) {
                            // Pass the washer to the adapter
                            adapter.items = washer.history!!
                            adapter.notifyDataSetChanged()

                            // Set the text of the textViewTitle
                            textViewTitle.text = washer.label
                        }
                    }
                    is Resource.Error -> {}
                }
            }
        }

        if (endescopeId != null) {
            endoscopeViewModel.endoscope(endescopeId)
            endoscopeViewModel.endoscopeStatus.observe(this) {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val endoscope = it.data
                        if (endoscope != null && endoscope.history != null) {
                            // Pass the endoscope to the adapter
                            adapter.items = endoscope.history!!
                            adapter.notifyDataSetChanged()

                            // Set the text of the textViewTitle
                            textViewTitle.text = endoscope.label
                        }
                    }
                    is Resource.Error -> {}
                }
            }
        }

        // Email Button to send the history of the washer/endoscope
        val buttonEmail = findViewById<Button>(R.id.buttonSendEmail)
        buttonEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "text/plain"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "History of Washer/Endoscope")
            var history = ""
            for (i in adapter.items) {
                history += i.description + "\n"
            }
            emailIntent.putExtra(Intent.EXTRA_TEXT, history)
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }

        // PDF Button to create a PDF file of the history of the washer/endoscope and save it to the device
        val buttonPDF = findViewById<Button>(R.id.buttonDownloadReport)
        buttonPDF.setOnClickListener {
            // User permission to write to external storage
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            } else {
                // Create PDF file
                val pdfDocument = PdfDocument()
                // Full page size to capture the whole screen
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                val paint = Paint()
                paint.textSize = 20f
                canvas.drawText("History of Washer/Endoscope", 10f, 50f, paint)
                var y = 80f
                for (i in adapter.items) { // Loop through the history and add it to the PDF file
                    if (i.description!!.length > 100) {
                        y += 40f

                        // format the date to DD/MM/YYYY
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val dateStr = dateFormat.format(i.createdAt!!)

                        paint.textSize = 15f
                        canvas.drawText(dateStr, 10f, y, paint)

                        paint.textSize = 10f
                        y += 15f

                        // if description is more than 100 characters, split it into 2 lines
                        if (i.description!!.length > 100) {
                            canvas.drawText(
                                i.description!!.substring(0, 100),
                                10f,
                                y,
                                paint
                            )
                            y += 10f
                            canvas.drawText(
                                i.description!!.substring(100, i.description!!.length),
                                10f,
                                y,
                                paint
                            )
                        } else {
                            canvas.drawText(
                                i.description!!,
                                10f,
                                y,
                                paint
                            )
                        }
                    } else {
                        canvas.drawText(i.description!!, 10f, y, paint)
                    }
                }
                pdfDocument.finishPage(page)
                try {
                    // Set file name to History_YYYYMMDD_HHMMSS.pdf
                    val file = File(
                        Environment.getExternalStorageDirectory(),
                        "/Download/History_" + SimpleDateFormat("yyyyMMdd_HHmmss")
                            .format(Date()) + ".pdf"
                    )
                    pdfDocument.writeTo(FileOutputStream(file))
                    showToast("PDF file saved to Download folder")
                } catch (e: IOException) {
                    e.printStackTrace()
                    showToast("Something wrong: $e")
                } finally {
                    pdfDocument.close()
                }
            }
        }

    }


}

