package com.jsontextfield.jtunes

import androidx.lifecycle.ViewModel

class MusicViewModel : ViewModel() {

}

enum class UIStates { LOADING, LOADED, ERROR, INITIAL, }
enum class MusicStates { STOPPED, PAUSED, PLAYING, }