package com.example.hospital_management_app.Utilities

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object PdfGenerator {

    /**
     * Captures [contentView] as a full-height bitmap, writes it to a PDF file
     * in the Downloads folder, then opens it with an external PDF viewer.
     */
    fun captureAndSave(context: Context, contentView: View, fileName: String) {
        try {
            // 1. Render the full (scrollable) view into a bitmap
            val bitmap = captureFullView(contentView)

            // 2. Build a PdfDocument with one page matching the bitmap size
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            page.canvas.drawColor(Color.WHITE)
            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDocument.finishPage(page)
            bitmap.recycle()

            // 3. Save to Downloads
            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ – use MediaStore (no permission needed)
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                val insertUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                insertUri?.let { resolver.openOutputStream(it) }?.use { out ->
                    pdfDocument.writeTo(out)
                }
                pdfDocument.close()
                insertUri
            } else {
                // Android 9 and below – write to a cache file then share via FileProvider
                val cacheFile = File(context.cacheDir, fileName)
                FileOutputStream(cacheFile).use { out -> pdfDocument.writeTo(out) }
                pdfDocument.close()
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    cacheFile
                )
            }

            // 4. Open the PDF
            if (uri != null) {
                Toast.makeText(context, "PDF saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val chooser = Intent.createChooser(intent, "Open PDF with")
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(chooser)
                }
            } else {
                Toast.makeText(context, "Failed to save PDF", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun captureFullView(view: View): Bitmap {
        // Force measure to full (unwrapped) height so ScrollView children are fully rendered
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width.takeIf { it > 0 } ?: 1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val width = view.measuredWidth.coerceAtLeast(1)
        val height = view.measuredHeight.coerceAtLeast(1)
        view.layout(0, 0, width, height)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }
}

