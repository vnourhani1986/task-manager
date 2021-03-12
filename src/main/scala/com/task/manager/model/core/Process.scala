package com.task.manager.model.core

import com.task.manager.model.generic.Priority

case class Process(
    PID: String,
    priority: Priority
) {
  def kill(): Unit =
    println(s"process with PID: $PID and priority: $priority is killed")
}
