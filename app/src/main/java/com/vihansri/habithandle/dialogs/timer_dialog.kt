package com.vihansri.habithandle.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.filter


@Composable
fun DurationPickerDialog(
    initialSeconds: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var h by remember { mutableIntStateOf((initialSeconds / 3600).toInt()) }
    var m by remember { mutableIntStateOf(((initialSeconds % 3600) / 60).toInt()) }
    var sec by remember { mutableIntStateOf((initialSeconds % 60).toInt()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select duration") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WheelPicker(0..23, h, "Hours", { h = it }, Modifier.weight(1f))
                WheelPicker(0..59, m, "Minutes", { m = it }, Modifier.weight(1f))
                WheelPicker(0..59, sec, "Seconds", { sec = it }, Modifier.weight(1f))
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(h * 3600L + m * 60L + sec) }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    range: IntRange,
    initial: Int,
    label: String,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeight = 48.dp
    val values = remember(range) { range.toList() }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (initial - range.first).coerceIn(values.indices)
    )
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val halfItemPx = with(LocalDensity.current) { itemHeight.toPx() / 2 }

    // When scrolling stops, report the value sitting in the middle row
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { !it }
            .collect {
                var index = listState.firstVisibleItemIndex
                if (listState.firstVisibleItemScrollOffset > halfItemPx) index++
                onSelected(values[index.coerceIn(values.indices)])
            }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier.height(itemHeight * 3),
            contentAlignment = Alignment.Center
        ) {
            // highlight band for the selected row
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            ) {}

            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                contentPadding = PaddingValues(vertical = itemHeight)
            ) {
                items(values) { value ->
                    Box(
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("%02d".format(value), fontSize = 28.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}