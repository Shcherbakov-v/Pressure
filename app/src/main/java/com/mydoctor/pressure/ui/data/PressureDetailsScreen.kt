package com.mydoctor.pressure.ui.data

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.mydoctor.pressure.R
import com.mydoctor.pressure.data.Pressure
import com.mydoctor.pressure.ui.data.PressureDetails.Companion.EMPTY_INDEX_PRESSURE_ID
import com.mydoctor.pressure.ui.navigation.NavigationDestination
import com.mydoctor.pressure.ui.theme.PressureTheme
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Destination for Pressure Details screen
 */
object PressureDetailsDestination : NavigationDestination {
    override val route = "pressure_details"
    const val pressureIdArg = "pressureId"
    val routeWithArgs = "$route/{$pressureIdArg}"
}

/**
 * Entry route for Pressure Details Screen
 *
 * @param navigateBack - the function that will be used to navigate to the previous screen
 * @param viewModel - ViewModel [PressureDetailsViewModel] for this screen
 */
@Composable
fun PressureDetailsScreen(
    navigateBack: () -> Unit,
    viewModel: PressureDetailsViewModel = hiltViewModel()
) {
    val pressureUiState = viewModel.pressureUiState
    val coroutineScope = rememberCoroutineScope()
    PressureDetailsScreenUI(
        navigateBack = navigateBack,
        onDelete = {
            // Note: If the user rotates the screen very fast, the operation may get cancelled
            // and the item may not be deleted from the Database. This is because when config
            // change occurs, the Activity will be recreated and the rememberCoroutineScope will
            // be cancelled - since the scope is bound to composition.
            coroutineScope.launch {
                viewModel.deleteItem()
                navigateBack()
            }
        },
        isDeleteDialog = pressureUiState.isDeleteDialog,
        pressure = pressureUiState.pressureDetails,
        onValueChange = viewModel::updateUiState,
    )
}

@Composable
fun PressureDetailsScreenUI(
    navigateBack: () -> Unit,
    onDelete: () -> Unit,
    onValueChange: (Boolean) -> Unit,
    isDeleteDialog: Boolean,
    pressure: PressureDetails,
) {
    LazyColumn(
        modifier = Modifier
            .padding(bottom = 16.dp)
    ) {
        item {
            PressureDetailsHeader(
                navigateBack = navigateBack,
                onDelete = { onValueChange(true) },
            )
        }
        if (pressure.id > EMPTY_INDEX_PRESSURE_ID) {
            item {
                PressureInPressureDetails(
                    pressure = pressure
                )
            }
            if (pressure.note.isNotEmpty()) {
                item {
                    NotesInPressureDetails(
                        pressure.note
                    )
                }
            }
            val isHighPressure =
                pressure.systolic.toInt() > 150 || pressure.diastolic.toInt() > 150
            if (isHighPressure) {
                item {
                    PressureHigh()
                }
            }
        }
    }
    if (isDeleteDialog) {
        AlertDialogDelete(
            onDismissRequest = { onValueChange(false) },
            onConfirmation = onDelete,
        )
    }
}

@Composable
fun PressureDetailsHeader(
    navigateBack: () -> Unit,
    onDelete: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(
                top = 22.dp,
            )
            .padding(
                top = 44.dp,
            ),
    ) {
        SmallFloatingActionButton(
            onClick = navigateBack,
            modifier = Modifier
                .align(alignment = CenterStart)
                .padding(start = 16.dp),
            shape = RoundedCornerShape(10.dp),
            containerColor = Color.White,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.button_back),
                null,
            )
        }
        Text(
            text = stringResource(R.string.measurement_log),
            modifier = Modifier
                .align(alignment = Alignment.Center),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        SmallFloatingActionButton(
            onClick = onDelete,
            modifier = Modifier
                .align(alignment = CenterEnd)
                .padding(end = 16.dp),
            shape = RoundedCornerShape(10.dp),
            containerColor = Color.White,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.icon_bin),
                null,
            )
        }
    }
}

@Composable
fun PressureInPressureDetails(
    pressure: PressureDetails,
) {
    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            //.animateContentSize()
            //.animateContentSize { initialValue, targetValue ->  }
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        val formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val date = formatterDate.format(
            Instant.ofEpochMilli(pressure.date).atZone(ZoneId.systemDefault())
        )
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.name_pressure),
                    fontSize = 12.sp,
                    color = Color(0x801C1C24),
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = pressure.systolic,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(R.string.separator),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = pressure.diastolic,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(R.string.unit_of_pressure_measurement),
                        fontSize = 12.sp,
                        color = Color(0x801C1C24),
                    )
                }
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "$date г.",
                    fontSize = 12.sp,
                    color = Color(0x4D1C1C24),
                )
            }
            Column(
                modifier = Modifier.padding(start = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.pulse),
                    fontSize = 12.sp,
                    color = Color(0x801C1C24),
                )
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = pressure.pulse,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(R.string.unit_of_measurement_of_pulse),
                        fontSize = 12.sp,
                        color = Color(0x801C1C24),
                    )
                }
            }
        }
    }
}

@Composable
fun NotesInPressureDetails(
    descriptionNote: String,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp,
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0x261C1C24)
            )
            Text(
                text = descriptionNote,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(end = 10.dp),
            )
        }
    }
}

@Composable
fun PressureHigh() {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp,
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.icon_attention),
                null
            )
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(R.string.pressure_is_higher_than_normal_long),
                fontSize = 14.sp,
                color = Color(0xB2FF66A6)
            )
        }
    }
}

@Composable
fun AlertDialogDelete(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 8.dp
                        ),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.delete_entry),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 16.dp
                        ),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.confirmation_of_deletion),
                    fontSize = 14.sp,
                    color = Color(0x4D1C1C24)
                )
                Row(
                    modifier = Modifier
                        .padding(
                            top = 16.dp
                        ),
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .weight(1f),
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                        )
                    }
                    Button(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .weight(1f),
                        onClick = onConfirmation,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0088FF)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PressureDetailsScreenPreview() {
    PressureTheme {
        PressureDetailsScreenUI(
            navigateBack = {},
            onDelete = {},
            isDeleteDialog = false,
            onValueChange = {},
            pressure = Pressure(
                id = 0,
                systolic = 154,
                diastolic = 94,
                pulse = 71,
                date = 1729900800000,
                note = "My head is spinning",
            ).toPressureDetails(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AlertDialogDeletePreview() {
    PressureTheme {
        AlertDialogDelete(
            onDismissRequest = {},
            onConfirmation = {},
        )
    }
}