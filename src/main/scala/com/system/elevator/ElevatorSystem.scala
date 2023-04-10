package com.system.elevator

import scala.collection.mutable

trait ElevatorSystem {
  def numOfFloors: Int
  def numOfElevator: Int

  def pickup(button: PickupOrder)
  def update(elevatorId: ElevatorId, orders: List[PickupOrder])
  def step()
  def status(): mutable.Map[ElevatorId, ElevatorStatus]
}
