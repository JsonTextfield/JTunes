package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hintText: String = "",
    onTextChanged: (String) -> Unit = {},
    value: String = ""
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(fraction = 0.9f),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        value = value,
        onValueChange = onTextChanged,
        leadingIcon = { Icon(Icons.Rounded.Search, null) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onTextChanged("") }) {
                    Icon(Icons.Rounded.Clear, null)
                }
            }
        },
        maxLines = 1, placeholder = { Text(hintText) },
    )
}