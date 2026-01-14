package com.anastasiaiva.pelmenisegodnya.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anastasiaiva.pelmenisegodnya.R

@Composable
fun WelcomeScreen(onReadyClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.footer_icon),
            contentDescription = "Приветственная картинка",
            modifier = Modifier
                .height(280.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            text = "Ну что, дорогие мои пельмешки, \nвы готовы поиграть?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onReadyClick) {
            Text("Готовы!")
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(viewModel: KerilViewModel, onUpdateClick: () -> Unit) {
    // подписываемся на состояние из ViewModel
    val uiState = viewModel.uiState.collectAsState().value
    val isUpdateAvailable = viewModel.isUpdateAvailable.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val currentImageRes = when (uiState) {
            is KerilUiState.Loading -> uiState.imageResId
            is KerilUiState.Result -> uiState.imageResId
            is KerilUiState.AlreadyUsedToday -> uiState.imageResId
            else -> R.drawable.slot_main
        }

        currentImageRes?.let { res ->
            Image(
                painter = painterResource(id = res),
                contentDescription = "Рандомный мем",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .padding(bottom = 24.dp),
                contentScale = ContentScale.Fit
            )
        }
        when (uiState) {
            is KerilUiState.Result -> {
                Text(
                    text = uiState.phrase,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = uiState.endingPhrase,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
            is KerilUiState.AlreadyUsedToday -> Text(
                text = uiState.message,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            else -> {}
        }
        Button (onClick = { viewModel.onButtonClicked() },
            modifier = Modifier.clip(shape= RoundedCornerShape(30.dp))) {
            Text("Какой ты сегодня Керил?")
        }

        if (isUpdateAvailable) {
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = onUpdateClick,
                modifier = Modifier.clip(RoundedCornerShape(30.dp))
            ) {
                Text("Обновить приложение")
            }
        }
    }
}
