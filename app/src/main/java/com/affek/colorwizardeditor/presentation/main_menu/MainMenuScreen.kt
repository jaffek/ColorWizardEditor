package com.affek.colorwizardeditor.presentation.main_menu

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.affek.colorwizardeditor.R
import com.affek.colorwizardeditor.navigation.ColorTransferScreenNavArgs
import com.affek.colorwizardeditor.presentation.destinations.ColorTransferScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun MainMenuScreen(
    sourceImageUri: String,
    navigator: DestinationsNavigator,
) {

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val mimeTypeFilter = arrayOf("image/jpeg", "image/png", "image/jpg")

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            selectedImageUri = uri
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        AsyncImage(
            model = Uri.parse(sourceImageUri),
            contentDescription = "Chosen image",
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.4f),
        )

         Spacer(modifier = Modifier
             .fillMaxHeight(0.1f))

        Text(
            text = stringResource(id = R.string.select_copy_color),
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier
            .fillMaxHeight(0.02f))

        Button(
            onClick = {
                singlePhotoPickerLauncher.launch(
                    mimeTypeFilter
                )
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_folder_open_24),
                contentDescription = "Choose image",
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(id = R.string.open_image),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier
            .fillMaxHeight(0.02f))

        Text(
            text = stringResource(id = R.string.or),
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier
            .fillMaxHeight(0.02f))

        Button(
            onClick = {
                // TODO
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_tune_24),
                contentDescription = "Choose image",
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(id = R.string.edit_image),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
    selectedImageUri?.let {
        navigator.navigate(ColorTransferScreenDestination(ColorTransferScreenNavArgs(sourceImageUri = sourceImageUri, colorImageUri = it.toString())))
    }
}

