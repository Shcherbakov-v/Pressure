package com.mydoctor.pressure.ui.pressure

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mydoctor.pressure.R
import com.mydoctor.pressure.ui.Header
import com.mydoctor.pressure.ui.utilities.SegmentedControl
import com.mydoctor.pressure.ui.navigation.NavigationDestination
import com.mydoctor.pressure.ui.theme.PressureTheme

object PressureDestination : NavigationDestination {
    override val route = "home"
}

/**
 * Entry route for Pressure screen
 */
@Composable
fun PressureScreen(
    //modifier: Modifier = Modifier,
    navigateToAddPressure: () -> Unit,
    pressureViewModel: PressureViewModel = hiltViewModel()
) {
    pressureViewModel.pressureUiState

    val pressureUiState by pressureViewModel.pressureUiState.collectAsState()
    Log.d(stringResource(R.string.pressure_tag), pressureUiState.pressureList.toString())
    PressureScreenUI(navigateToAddPressure)
}

@Composable
fun PressureScreenUI(
    navigateToAddPressure: () -> Unit,
) {
    Column {
        Header()
        PressureBlock(
            //TODO Change date along with adding schedule
            date = "Июня 2024 г.",
            navigateToAddPressure = navigateToAddPressure
        )
        SegmentedControl(
            /* TODO How do indents affect?
            modifier = Modifier
                            .padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp,
                            ),*/
            items = listOf(
                stringResource(R.string.day),
                stringResource(R.string.week),
                stringResource(R.string.month)
            ),
            onItemSelection = { selectedItemIndex ->

            }
        )
        Notes(
            {},
            stringResource(R.string.description_note_text)
        )
        Targets(
            {},
            stringResource(R.string.description_target_text)
        )
        MeasurementLog {}
    }
}

@Composable
fun PressureBlock(
    date: String,
    navigateToAddPressure: () -> Unit,
) {
    Box(
        Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.align(alignment = Center)
        ) {
            Text(
                text = stringResource(R.string.name_pressure),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
            Text(
                text = date,
                fontSize = 14.sp,
            )
        }
        SmallFloatingActionButton(
            onClick = navigateToAddPressure,
            modifier = Modifier
                .align(alignment = CenterEnd)
                .padding(end = 16.dp),
            shape = RoundedCornerShape(10.dp),
            containerColor = Color.White,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.button_add_black),
                null,
            )
        }
    }
}

@Composable
fun Notes(
    onClick: () -> Unit,
    descriptionNote: String,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Column(
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 6.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_notes),
                        null
                    )
                    Text(
                        text = stringResource(R.string.notes),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                IconButton(onClick) {
                    Icon(
                        painterResource(R.drawable.button_add), null,
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier
                    .padding(end = 10.dp),
            )
            Text(
                text = descriptionNote,
                fontSize = 14.sp,
                color = Color(0xFF83A0B9),
                modifier = Modifier
                    .padding(end = 10.dp),
            )
        }
    }
}

@Composable
fun Targets(
    onClick: () -> Unit,
    descriptionTarget: String,
) {
    Card(
        Modifier
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
    ) {
        Column(
            Modifier.padding(
                top = 8.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 6.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_targets),
                        null
                    )
                    Text(
                        text = stringResource(R.string.targets),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                IconButton(onClick) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.button_add),
                        null
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier
                    .padding(end = 10.dp),
            )
            Text(
                text = descriptionTarget,
                fontSize = 14.sp,
                color = Color(0xFF83A0B9),
                modifier = Modifier
                    .padding(end = 10.dp),
            )
        }
    }
}

@Composable
fun MeasurementLog(onClick: () -> Unit) {
    Card(
        Modifier
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 6.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_measurement_log),
                    null
                )
                Text(
                    text = stringResource(R.string.measurement_log),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            IconButton(onClick) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_next),
                    null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PressureScreenPreview() {
    PressureTheme {
        PressureScreenUI {}
    }
}