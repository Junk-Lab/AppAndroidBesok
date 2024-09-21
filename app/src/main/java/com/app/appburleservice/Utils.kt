package com.app.appburleservice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppFooter(modifier: Modifier = Modifier) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BackgroundColorGlobal)
    ) {
        Text(
            text = "Copyright © 2024 Vallis. All Rights Reserved | Versão Dev 1.0.1",
            color = Color.White,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 10.sp
        )
    }
}
