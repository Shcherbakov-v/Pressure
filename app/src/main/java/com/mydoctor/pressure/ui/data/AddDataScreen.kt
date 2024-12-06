package com.mydoctor.pressure.ui.data

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mydoctor.pressure.R
import com.mydoctor.pressure.ui.navigation.NavigationDestination
import com.mydoctor.pressure.ui.theme.PressureTheme
import com.mydoctor.pressure.ui.utilities.AdvancedDatePickerDialog
import com.mydoctor.pressure.ui.utilities.AdvancedTimePickerDialog
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

/**
 * Destination for Add Data screen
 */
object AddDataDestination : NavigationDestination {
    override val route = "add_data"
}

/**
 * Entry route for Add Data screen
 *
 * @param navigateBack - the function that will be used to navigate to the previous screen
 * @param viewModel - ViewModel [AddDataViewModel] for this screen
 */
@Composable
fun AddDataScreen(
    navigateBack: () -> Unit,
    //modifier: Modifier = Modifier,
    viewModel: AddDataViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val currentTime = Calendar.getInstance()
    val pressureDetails = viewModel.pressureUiState.pressureDetails
    if (!pressureDetails.dateCreated) {
        viewModel.updateUiState(
            pressureDetails.copy(
                date = currentTime.timeInMillis,
                dateCreated = true
            )
        )
    }
    AddPressureScreenUI(
        pressureUiState = viewModel.pressureUiState,
        onPressureValueChange = viewModel::updateUiState,
        onSaveClick = {
            // Note: If the user rotates the screen very fast, the operation may get cancelled
            // and the pressure may not be saved in the Database. This is because when config
            // change occurs, the Activity will be recreated and the rememberCoroutineScope will
            // be cancelled - since the scope is bound to composition.
            coroutineScope.launch {
                viewModel.savePressure()
                navigateBack()
            }
        },
        navigateBack = navigateBack,
    )
}

@Composable
fun AddPressureScreenUI(
    //modifier: Modifier = Modifier,
    pressureUiState: PressureUiState,
    onPressureValueChange: (PressureDetails) -> Unit,
    onSaveClick: () -> Unit,
    navigateBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            AddDataHeader(
                navigateBack = navigateBack,
            )
        },
        bottomBar = {
            Button(
                onClick = onSaveClick,
                enabled = pressureUiState.isEntryValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 24.dp,
                    ),
                colors = ButtonDefaults.buttonColors(Color(0xFF0088FF))
            ) {
                Text(stringResource(R.string.save))
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding),
        ) {
            LazyColumn {
                item {
                    PressureEndPulse(
                        pressureDetails = pressureUiState.pressureDetails,
                        onValueChange = onPressureValueChange
                    )
                }
                item {
                    DataAndTime(
                        pressureDetails = pressureUiState.pressureDetails,
                        onValueChange = onPressureValueChange
                    )
                }
                item {
                    Note(
                        pressureDetails = pressureUiState.pressureDetails,
                        onValueChange = onPressureValueChange
                    )
                }
            }

        }
    }
}

@Composable
fun AddDataHeader(
    navigateBack: () -> Unit,
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
            text = stringResource(R.string.add_data),
            modifier = Modifier
                .align(alignment = Alignment.Center),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
    }
}

@Composable
fun PressureEndPulse(
    pressureDetails: PressureDetails,
    onValueChange: (PressureDetails) -> Unit,
) {
    val pattern = remember { Regex("^\\d+\$") }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .weight(2f)
                .padding(end = 4.dp)
                .wrapContentWidth(Alignment.Start),
        ) {
            Text(
                text = stringResource(R.string.blood_pressure),
                fontSize = 16.sp,
            )
            Row {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                        .wrapContentWidth(Alignment.Start),
                ) {
                    Text(
                        text = stringResource(R.string.systolic),
                        fontSize = 11.sp,
//                        color = Color(0x801C1C24),
                    )
                    OutlinedTextField(
                        placeholder = { Text(stringResource(R.string.default_systolic_pressure)) },
                        value = pressureDetails.systolic,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(pattern)) {
                                onValueChange(pressureDetails.copy(systolic = it))
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedPlaceholderColor = Color(0xFF83A0B9),
                            focusedPlaceholderColor = Color(0xFF83A0B9),
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                        .wrapContentWidth(Alignment.End),
                ) {
                    Text(
                        text = stringResource(R.string.diastolic),
                        fontSize = 11.sp,
                        color = Color(0x801C1C24),
                        maxLines = 1,
                    )
                    OutlinedTextField(
                        placeholder = { Text(stringResource(R.string.default_diastolic_pressure)) },
                        value = pressureDetails.diastolic,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(pattern)) {
                                onValueChange(pressureDetails.copy(diastolic = it))
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedPlaceholderColor = Color(0xFF83A0B9),
                            focusedPlaceholderColor = Color(0xFF83A0B9),
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .wrapContentWidth(Alignment.End)
        ) {
            Text(
                text = stringResource(R.string.pulse),
                fontSize = 16.sp,
            )
            Text(
                text = "",
                fontSize = 11.sp,
                color = Color(0x801C1C24),
            )
            OutlinedTextField(
                placeholder = { Text(stringResource(R.string.default_pulse)) },
                value = pressureDetails.pulse,
                onValueChange = {
                    if (it.isEmpty() || it.matches(pattern)) {
                        onValueChange(pressureDetails.copy(pulse = it))
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedPlaceholderColor = Color(0xFF83A0B9),
                    focusedPlaceholderColor = Color(0xFF83A0B9),
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAndTime(
    pressureDetails: PressureDetails,
    onValueChange: (PressureDetails) -> Unit,
) {
    val formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val formatterTime = DateTimeFormatter.ofPattern("HH:mm")

    val date = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(pressureDetails.date),
        ZoneId.systemDefault()
    )

    val textDate = formatterDate.format(date)
    val textTime = formatterTime.format(date)

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Row(
        Modifier
            .fillMaxWidth()
            .padding(
                top = 24.dp,
                start = 16.dp,
                end = 16.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .padding(end = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.date_of_measurements),
                fontSize = 16.sp,
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            showDatePicker = true
                            focusManager.clearFocus(true)
                        }
                    },
                placeholder = { Text(if (!pressureDetails.dateSelected) textDate else "") },
                value = if (pressureDetails.dateSelected) textDate else "",
                onValueChange = { },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedPlaceholderColor = Color(0xFF83A0B9),
                    focusedPlaceholderColor = Color(0xFF83A0B9),
                ),
            )
            fun dateMillisPlusTime(selectedDateMillis: Long): Long {
                val selectedDate =
                    Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneId.systemDefault())
                        .toLocalDate()
                val time = date.toLocalTime()
                val localDateTime = LocalDateTime.of(selectedDate, time)
                val millis =
                    ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).toInstant()
                        .toEpochMilli()
                return millis
            }
            if (showDatePicker) {
                AdvancedDatePickerDialog(
                    initialSelectedDateMillis = pressureDetails.date,
                    onDismiss = { showDatePicker = false },
                    onDateSelected = { selectedDateMillis ->
                        onValueChange(
                            pressureDetails.copy(
                                date = dateMillisPlusTime(selectedDateMillis),
                                dateSelected = true
                            )
                        )
                    },
                )
            }

        }
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.measurement_time),
                fontSize = 16.sp,
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            showTimePicker = true
                            focusManager.clearFocus(true)
                        }
                    },
                placeholder = { Text(if (!pressureDetails.timeSelected) textTime else "") },
                value = if (pressureDetails.timeSelected) textTime else "",
                onValueChange = { },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedPlaceholderColor = Color(0xFF83A0B9),
                    focusedPlaceholderColor = Color(0xFF83A0B9),
                ),
            )
            if (showTimePicker) {
                AdvancedTimePickerDialog(
                    onDismiss = { showTimePicker = false },
                    onConfirm = { timePickerState ->
                        showTimePicker = false

                        val time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        val localDateTime = LocalDateTime.of(date.toLocalDate(), time)
                        val millis =
                            ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).toInstant()
                                .toEpochMilli()

                        onValueChange(pressureDetails.copy(date = millis, timeSelected = true))
                    },
                    currentTime = date
                )
            }
        }
    }
}

@Composable
fun Note(
    pressureDetails: PressureDetails,
    onValueChange: (PressureDetails) -> Unit,
) {
    //var textNote by remember { mutableStateOf("") }
    Text(
        modifier = Modifier.padding(
            top = 24.dp,
            start = 16.dp,
        ),
        text = stringResource(R.string.note),
        fontSize = 16.sp,
    )
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            ),
        placeholder = { Text(if (pressureDetails.note == "") stringResource(R.string.describe_how_you_feel) else "") },
        value = pressureDetails.note,
        onValueChange = {
            onValueChange(pressureDetails.copy(note = it))
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults
            .colors(
                unfocusedPlaceholderColor = Color(0xFF83A0B9),
                focusedPlaceholderColor = Color(0xFF83A0B9),
            ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun AddPressureScreenPreview() {
    PressureTheme {
        AddPressureScreenUI(
            pressureUiState = PressureUiState(
                PressureDetails(
                    id = 0,
                    systolic = "",
                    diastolic = "",
                    pulse = "",
                    date = Calendar.getInstance().timeInMillis,
                    note = "",
                )
            ),
            onPressureValueChange = {},
            onSaveClick = {},
            navigateBack = {},
        )
    }
}