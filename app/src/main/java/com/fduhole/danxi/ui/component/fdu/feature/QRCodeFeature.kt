package com.fduhole.danxi.ui.component.fdu.feature

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.fduhole.danxi.R
import com.fduhole.danxi.repository.fdu.ECardRepository
import com.fduhole.danxi.ui.component.fdu.Feature
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@ViewModelScoped
class QRCodeFeature @Inject constructor(
    val repository: ECardRepository
) : Feature<Nothing>(
    icon = Icons.Filled.QrCode,
    title = R.string.fudan_qr_code,
    hasMoreContent = true,
) {
    override val mUIState = MutableStateFlow(
        State<Nothing>(clickable = true)
    )

    override fun onClick(navController: NavController) {
        when (mUIState.value.state) {
            Status.Idle -> {
                mUIState.update { it.copy(showMoreContent = true, clickable = false) }
            }

            is Status.Success, is Status.Error -> {
                mUIState.update { it.copy(state = Status.Idle, showMoreContent = false, clickable = true) }
            }

            else -> {}
        }
    }

    override val moreContent: @Composable (navController: NavController) -> Unit = {
        var qrCodeBitMap by remember { mutableStateOf<Bitmap?>(null) }
        var error by remember { mutableStateOf<Throwable?>(null) }
        val scope = rememberCoroutineScope()
        val task: suspend CoroutineScope.() -> Unit = {
            try {
                qrCodeBitMap = generateQRCode(repository.getQRCode())
            } catch (e: Throwable) {
                error = e
            }
        }
        var job: Job? = null
        LaunchedEffect(Unit) {
            job = scope.launch(context = Dispatchers.IO, block = task)
        }

        val title = stringResource(title)
        AlertDialog(
            onDismissRequest = { mUIState.update { it.copy(showMoreContent = false, clickable = true) } },
            confirmButton = {
                TextButton(
                    onClick = {
                        mUIState.update { it.copy(showMoreContent = false, clickable = true) }
                    },
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                if (error != null) {
                    TextButton(onClick = {
                        job?.cancel()
                    }) {
                        Text(stringResource(R.string.retry))
                    }
                }
            },
            title = { Text(title) },
            text = {
                if (qrCodeBitMap != null) {
                    Image(
                        qrCodeBitMap!!.asImageBitmap(),
                        contentDescription = title,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    if (error != null) {
                        Text(stringResource(R.string.failed_to_load))
                    } else {
                        Text(stringResource(R.string.loading_qr_code))
                    }
                }
            },
        )
    }

    private fun generateQRCode(data: String): Bitmap {
        val matrix = MultiFormatWriter().encode(
            data, BarcodeFormat.QR_CODE, 200, 200, mutableMapOf(
                EncodeHintType.CHARACTER_SET to "utf-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L,
                EncodeHintType.MARGIN to 2
            )
        )
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