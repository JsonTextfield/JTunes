package com.jsontextfield.jtunes

import androidx.lifecycle.ViewModel

class MusicViewModel : ViewModel() {

}

enum class UIStates { LOADING, LOADED, ERROR, INITIAL, }
enum class MusicStates { STOPPED, PAUSED, PLAYING, }
enum class LoopModes { OFF, LOOP_ALL, LOOP_ONE, }