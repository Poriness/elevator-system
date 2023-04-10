package com.system.elevator

sealed trait MovementStatus {
  def isRunning: Boolean
}

object MovementStatus {
  case object Up extends MovementStatus {
    override def isRunning: Boolean = true
  }

  case object Down extends MovementStatus {
    override def isRunning: Boolean = true
  }

  case object Stopped extends MovementStatus {
    override def isRunning: Boolean = false
  }
}
