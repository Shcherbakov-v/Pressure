package com.mydoctor.pressure.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mydoctor.pressure.R
import com.mydoctor.pressure.ui.theme.PressureTheme

@Composable
fun Header() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(
                top = 6.dp,
                bottom = 16.dp,
            )
            .padding(
                top = 44.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.icon_logo),
            null
        )
        Text(
            text = stringResource(R.string.my_doctor),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(
                    start = 13.dp,
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    PressureTheme {
        Header()
    }
}