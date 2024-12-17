package com.mydoctor.pressure.ui.targets

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mydoctor.pressure.R
import com.mydoctor.pressure.ui.navigation.NavigationDestination
import com.mydoctor.pressure.ui.targets.TargetDetails.Companion.EMPTY_INDEX_TARGET_ID
import com.mydoctor.pressure.ui.theme.PressureTheme
import com.mydoctor.pressure.ui.utilities.AdvancedDatePickerDialog
import com.mydoctor.pressure.ui.utilities.AdvancedTimePickerDialog
import com.mydoctor.pressure.utilities.TAG
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Destination for Target screen
 */
object TargetDestination : NavigationDestination {
    override val route = "target"
    const val targetIdArg = "targetId"
    val routeWithArgs = "$route/{$targetIdArg}"
}

/**
 * Entry route for Target screen
 *
 * @param navigateBack - the function that will be used to navigate to the previous screen
 * @param viewModel - ViewModel [TargetViewModel] for this screen
 */
@Composable
fun TargetScreen(
    navigateBack: () -> Unit,
    viewModel: TargetViewModel = hiltViewModel()
) {
    Log.d(TAG, "TargetScreen: targetUiState:${viewModel.targetUiState}")
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val targetDetails = viewModel.targetUiState.targetDetails
    TargetScreenUI(
        navigateBack = navigateBack,
        snackbarHostState = snackbarHostState,
        targetUiState = viewModel.targetUiState,
        onValueChange = viewModel::updateUiState,
        onSaveClick = {
            // Note: If the user rotates the screen very fast, the operation may get cancelled
            // and the pressure may not be saved in the Database. This is because when config
            // change occurs, the Activity will be recreated and the rememberCoroutineScope will
            // be cancelled - since the scope is bound to composition.

            val selectedDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(targetDetails.date),
                ZoneId.systemDefault()
            )
            val currentDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(viewModel.targetUiState.currentTime),
                ZoneId.systemDefault()
            )
            coroutineScope.launch {
                when {
                    selectedDateTime.toLocalDate() < currentDateTime.toLocalDate() ->
                        snackbarHostState.showSnackbar(context.getString(R.string.date_incorrect))

                    selectedDateTime <= currentDateTime ->
                        snackbarHostState.showSnackbar(context.getString(R.string.time_incorrect))

                    else -> {
                        viewModel.saveTarget()
                    }
                }
            }
        },
        onEditClick = viewModel::editTarget,
        onDeleteClick = {
            coroutineScope.launch {
                viewModel.deleteTarget()
                navigateBack()
            }
        },
    )
}

@Composable
fun TargetScreenUI(
    navigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    targetUiState: TargetUiState,
    onValueChange: (TargetDetails) -> Unit,
    onSaveClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val targetDetails = targetUiState.targetDetails
    Scaffold(
        modifier = Modifier.imePadding(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TargetHeader(
                targetDetails = targetDetails,
                navigateBack = navigateBack,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
            )
        },
        bottomBar = {
            Button(
                onClick = onSaveClick,
                enabled = targetUiState.isEntryValid && targetUiState.allowEditing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 24.dp,
                    ),
            ) {
                Text(stringResource(R.string.save))
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding),
        ) {
            item {
                Column(
                    modifier = Modifier.padding(
                        top = 24.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.target_description),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        color = Color(0x801C1C24),
                    )
                    Text(
                        modifier = Modifier.padding(top = 24.dp),
                        text = stringResource(R.string.target),
                        fontSize = 16.sp,
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 16.dp,
                            ),
                        enabled = targetUiState.allowEditing,
                        placeholder = {
                            Text(
                                if (targetDetails.description == "")
                                    stringResource(R.string.target_description_default)
                                else ""
                            )
                        },
                        value = targetDetails.description,
                        onValueChange = {
                            onValueChange(targetDetails.copy(description = it))
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedPlaceholderColor = Color(0xFF83A0B9),
                            focusedPlaceholderColor = Color(0xFF83A0B9),
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                    )
                }
            }
            item {
                DataAndTimeTarget(
                    targetUiState = targetUiState,
                    onValueChange = onValueChange,
                )
            }
        }
    }
}

@Composable
fun TargetHeader(
    targetDetails: TargetDetails,
    navigateBack: () -> Unit,
    //onExpandedMenu: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(
                top = 22.dp,
            )
            .padding(
                top = 44.dp,
                bottom = 16.dp,
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
            text = stringResource(R.string.target),
            modifier = Modifier
                .align(alignment = Alignment.Center),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )

        if (targetDetails.isCreated) {
            Box(
                modifier = Modifier
                    .align(alignment = TopEnd)
                    .padding(end = 16.dp),
            ) {
                var expandedMenu by remember { mutableStateOf(false) }
                SmallFloatingActionButton(
                    onClick = { expandedMenu = true },
                    modifier = Modifier
                        .align(alignment = CenterEnd),
                    shape = RoundedCornerShape(10.dp),
                    containerColor = Color.White,
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_more),
                        null,
                    )
                }
                DropdownMenu(
                    offset = DpOffset(0.dp, 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .align(alignment = TopEnd)
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                        ),
                    containerColor = Color.White,
                    expanded = expandedMenu,
                    onDismissRequest = { expandedMenu = false },
                ) {
                    DropdownMenuItem(
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            start = 64.dp,
                            end = 16.dp
                        ),
                        onClick = {
                            onEditClick()
                            expandedMenu = false
                        },
                        text = {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.edit),
                                textAlign = TextAlign.End,
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                            )
                        }
                    )
                    DropdownMenuItem(
                        contentPadding = PaddingValues(
                            start = 64.dp,
                            end = 16.dp,
                            bottom = 8.dp,
                        ),
                        onClick = {
                            onDeleteClick()
                            expandedMenu = false
                        },
                        text = {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.delete),
                                textAlign = TextAlign.End,
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAndTimeTarget(
    targetUiState: TargetUiState,
    onValueChange: (TargetDetails) -> Unit,
) {
    val targetDetails: TargetDetails = targetUiState.targetDetails
    val formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val formatterTime = DateTimeFormatter.ofPattern("HH:mm")

    val date = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(targetDetails.date),
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
                text = stringResource(R.string.date),
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
                enabled = targetUiState.allowEditing,
                placeholder = { Text(if (!targetDetails.dateSelected) "до $textDate" else "") },
                value = if (targetDetails.dateSelected) "до $textDate" else "",
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
                    initialSelectedDateMillis = targetDetails.date,
                    onDismiss = { showDatePicker = false },
                    onDateSelected = { selectedDateMillis ->
                        onValueChange(
                            targetDetails.copy(
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
                text = stringResource(R.string.time),
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
                enabled = targetUiState.allowEditing,
                placeholder = { Text(if (!targetDetails.timeSelected) "до $textTime" else "") },
                value = if (targetDetails.timeSelected) "до $textTime" else "",
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

                        onValueChange(targetDetails.copy(date = millis, timeSelected = true))
                    },
                    currentTime = date
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TargetScreenUIPreview() {
    PressureTheme {
        TargetScreenUI(
            navigateBack = {},
            onValueChange = {},
            snackbarHostState = SnackbarHostState(),
            onSaveClick = {},
            onEditClick = {},
            onDeleteClick = {},
            targetUiState = TargetUiState(
                targetDetails = TargetDetails(
                    id = EMPTY_INDEX_TARGET_ID,
                    description = "Lose 2 kg",
                    date = 1731100000000,
                    status = TargetDetails.Status.SET,
                    dateSelected = true,
                    timeSelected = true
                )
            )
        )
    }
}