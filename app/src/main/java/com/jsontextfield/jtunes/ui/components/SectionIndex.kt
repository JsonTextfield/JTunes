package com.jsontextfield.jtunes.ui.components

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsontextfield.jtunes.entities.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private fun getIndexData(songs: List<Song>): ArrayList<Pair<String, Int>> {
    val dataString = songs.sortedBy {
        it.title.trim().toUpperCase(Locale.current)
    }.map {
        it.title.trim().toUpperCase(Locale.current).first()
    }.joinToString("")

    val letters = Regex("[A-ZÀ-Ö]")
    val numbers = Regex("[0-9]")
    val special = Regex("[^0-9A-ZÀ-Ö]")

    val result = LinkedHashSet<Pair<String, Int>>()

    if (special.containsMatchIn(dataString)) {
        result.add(Pair("*", special.find(dataString)?.range?.first!!))
    }
    if (numbers.containsMatchIn(dataString)) {
        result.add(Pair("#", numbers.find(dataString)?.range?.first!!))
    }
    for (character in dataString.split("")) {
        if (letters.matches(character)) {
            result.add(Pair(character, dataString.indexOf(character)))
        }
    }
    Log.d("SectionIndex", dataString)
    Log.d("SectionIndex", result.toString())
    return ArrayList<Pair<String, Int>>(result)
}

private fun getSelectedIndex(
    yPosition: Float,
    sectionIndexHeight: Float,
    positions: ArrayList<Pair<String, Int>>,
): Int {
    val result = ((yPosition / sectionIndexHeight) * positions.size)
        .toInt()
        .coerceIn(0, positions.size - 1)
    val tag = "SectionIndex"
    Log.e(tag, result.toString())
    return result
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SectionIndex(cameras: List<Song>, listState: LazyListState, selectedColor: Color = Color.Cyan) {
    val result = getIndexData(cameras)
    var selectedKey by remember { mutableStateOf("") }
    var offsetY by remember { mutableStateOf(0f) }
    var columnHeightPx by remember { mutableStateOf(0f) }

    val selectIndex = {
        val listIndex = getSelectedIndex(offsetY, columnHeightPx, result)
        if (selectedKey != result[listIndex].first) {
            selectedKey = result[listIndex].first
            val index = result[listIndex].second
            CoroutineScope(Dispatchers.Main).launch {
                listState.scrollToItem(index)
            }
        }
    }

    val draggableState = remember {
        DraggableState { delta ->
            offsetY += delta
            selectIndex()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(IntrinsicSize.Max)
            .padding(vertical = 10.dp)
            .onGloballyPositioned { coordinates ->
                columnHeightPx = coordinates.size.height.toFloat()
            }
            .draggable(
                orientation = Orientation.Vertical,
                state = draggableState,
                onDragStopped = {
                    selectedKey = ""
                    offsetY = it
                }
            )
            .pointerInteropFilter { motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        offsetY = motionEvent.y
                        selectIndex()
                    }

                    MotionEvent.ACTION_UP -> {
                        offsetY = motionEvent.y
                        selectIndex()
                        selectedKey = ""
                    }
                }
                true
            }
    ) {
        result.map {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = it.first,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    color = if (selectedKey == it.first) {
                        selectedColor
                    } else if (isSystemInDarkTheme()) {
                        Color.White
                    } else {
                        Color.Black
                    }
                )
            }
        }
    }
}