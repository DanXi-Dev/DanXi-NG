package com.fduhole.danxinative.ui.fdu

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fduhole.danxinative.databinding.FragmentQrCodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume


class QRCodeFragment : Fragment() {

    companion object {
        const val ARG_QR_CODE = "qr_code"
    }

    private var qrCode: String? = null
    private lateinit var binding: FragmentQrCodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            qrCode = it.getString(ARG_QR_CODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentQrCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            (activity as AppCompatActivity).supportActionBar?.title = "复旦生活码"

            try {
                val bitmap = withContext(Dispatchers.IO) {
                    suspendCancellableCoroutine { it.resume(generateQRCode(requireNotNull(qrCode) { "QRCode's data is null!" })) }
                }
                binding.fragQrCodeQrCode.setImageBitmap(bitmap)
            } catch (e: Throwable) {
                // TODO: dealing with error when generating bitmap for qr code.
            }
        }
    }

    private fun generateQRCode(data: String): Bitmap {
        val matrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 200, 200, mutableMapOf(
            EncodeHintType.CHARACTER_SET to "utf-8",
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L,
            EncodeHintType.MARGIN to 2
        ))
        return toBitmap(matrix)
    }

    private fun toBitmap(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y * width + x] = if (bitMatrix[x, y]) -0x1000000 else -0x1
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }
}