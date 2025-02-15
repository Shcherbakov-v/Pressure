package com.mydoctor.pressure.ui.utilities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mydoctor.pressure.R
import com.mydoctor.pressure.ui.theme.PressureTheme
import com.mydoctor.pressure.ui.theme.SelectButton

/**
 * SegmentedControl allows you to switch between elements from a set
 *
 * @param modifier
 * @param items - list of elements
 * @param selectedIndex - selected item
 * @param cornerRadius - radius, applied for all four corners.
 * @param onItemSelection - a function that is triggered when an element is clicked
 */
@Composable
fun SegmentedControl(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedIndex: Int,
    cornerRadius: Dp = 24.dp,
    onItemSelection: (selectedItemIndex: Int) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            )
            .fillMaxWidth()
            .height(42.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White),
            horizontalArrangement = Arrangement.Center
        ) {
            items.forEachIndexed { index, item ->
                Card(
                    modifier = modifier
                        .weight(1f)
                        .padding(
                            top = 8.dp,
                            bottom = 8.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                    shape = RoundedCornerShape(cornerRadius),
                    onClick = {
                        onItemSelection(index)
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedIndex == index) {
                            SelectButton
                        } else {
                            Color.White
                        }
                    ),
                ) {
                    Box(
                        modifier = modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item,
                            style = LocalTextStyle.current.copy(
                                fontSize = 14.sp,
                                fontWeight = if (selectedIndex == index)
                                    FontWeight.Bold
                                else
                                    FontWeight.Normal,
                            ),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SegmentedControlPreview() {
    PressureTheme {
        SegmentedControl(
            selectedIndex = 0,
            items = listOf(
                stringResource(R.string.day),
                stringResource(R.string.week),
                stringResource(R.string.month)
            ),
            onItemSelection = { }
        )
    }
}