package com.udacity


sealed class ButtonState {
    object Clicked : ButtonState()
    object Completed : ButtonState()
}