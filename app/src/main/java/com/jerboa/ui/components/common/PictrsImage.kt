package com.jerboa.ui.components.common

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.jerboa.R
import com.jerboa.datatypes.sampleCommunitySafe
import com.jerboa.pictrsImageThumbnail
import com.jerboa.ui.theme.*

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CircularIcon(
    icon: String,
    size: Dp = ICON_SIZE,
    thumbnailSize: Int = ICON_THUMBNAIL_SIZE,
) {
    Image(
        painter = rememberImagePainter(
            data = pictrsImageThumbnail(icon, thumbnailSize),
            builder = {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                transformations(CircleCropTransformation())
            }
        ),
        contentDescription = null,
        modifier = Modifier.size(size)
    )
}

@Composable
fun LargerCircularIcon(icon: String) {
    CircularIcon(
        icon = icon,
        size = LARGER_ICON_SIZE,
        thumbnailSize = LARGER_ICON_THUMBNAIL_SIZE,
    )
}

@Preview
@Composable
fun CircularIconPreview() {
    CircularIcon(icon = sampleCommunitySafe.icon!!)
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PictrsThumbnailImage(
    thumbnail: String,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberImagePainter(
            data = pictrsImageThumbnail(thumbnail, THUMBNAIL_SIZE),
            builder = {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                transformations(RoundedCornersTransformation(12f))
            },
        ),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = modifier,
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PictrsUrlImage(
    url: String,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberImagePainter(
            data = pictrsImageThumbnail(url, MAX_IMAGE_SIZE),
            builder = {
                size(OriginalSize)
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                transformations(RoundedCornersTransformation(12f))
            },
        ),
        contentScale = ContentScale.FillWidth,
        contentDescription = null,
        modifier = modifier,
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PictrsBannerImage(
    url: String,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberImagePainter(
            data = pictrsImageThumbnail(url, MAX_IMAGE_SIZE),
            builder = {
                size(OriginalSize)
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
            },
        ),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = modifier,
    )
}

@Composable
fun PickImage(
    modifier: Modifier = Modifier,
    onPickedImage: (image: Uri) -> Unit = {},
    showImage: Boolean = true,
) {
    val ctx = LocalContext.current
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        Log.d("jerboa", imageUri.toString())
        onPickedImage(uri!!)
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        OutlinedButton(onClick = {
            launcher.launch("image/*")
        }) {
            Text(
                text = "Upload Image",
                color = Muted,
            )
        }

        if (showImage) {

            Spacer(modifier = Modifier.height(SMALL_PADDING))

            imageUri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap.value = MediaStore.Images
                        .Media.getBitmap(ctx.contentResolver, it)
                } else {
                    val source = ImageDecoder
                        .createSource(ctx.contentResolver, it)
                    bitmap.value = ImageDecoder.decodeBitmap(source)
                }

                bitmap.value?.let { btm ->
                    Image(
                        bitmap = btm.asImageBitmap(),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}