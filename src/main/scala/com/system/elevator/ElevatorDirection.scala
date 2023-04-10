package com.system.elevator

sealed trait ElevatorDirection

object ElevatorDirection {
  case object Up extends ElevatorDirection
  case object Down extends ElevatorDirection
}