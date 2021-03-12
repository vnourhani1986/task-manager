package com.task.manager.model.generic

object OrderTypes extends Enumeration {
  type OrderType = Value
  val OrderByPID: OrderType = Value("order_by_pid")
  val OrderByPriority: OrderType = Value("order_by_priority")
  val OrderByTime: OrderType = Value("order_by_time")
}
