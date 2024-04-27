package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hintText: String = "",
    onTextChanged: (String) -> Unit = {},
    onCreatePlaylist: () -> Unit = {},
    value: String = ""
) {
    OutlinedTextField(
        shape = RoundedCornerShape(100.dp),
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        value = value,
        onValueChange = onTextChanged,
        leadingIcon = { Icon(Icons.Rounded.Search, null) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                Row {
                    IconButton(onClick = onCreatePlaylist) {
                        Icon(Icons.AutoMirrored.Rounded.PlaylistAdd, null)
                    }
                    IconButton(onClick = { onTextChanged("") }) {
                        Icon(Icons.Rounded.Clear, null)
                    }
                }
            }
        },
        maxLines = 1, placeholder = { Text(hintText, maxLines = 1, modifier = Modifier.basicMarquee()) },
    )
}