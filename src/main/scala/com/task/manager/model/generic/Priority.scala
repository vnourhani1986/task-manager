package com.task.manager.model.generic

sealed trait Priority {
  def >(priority: Priority): Boolean
}
object Priority {
  implicit def ordering: Ordering[Priority] = {
    Ordering.by {
      case Low    => 0
      case Medium => 1
      case High   => 2
    }
  }
}
case object Low extends Priority {
  override def >(priority: Priority): Boolean = false
}
case object Medium extends Priority {
  override def >(priority: Priority): Boolean =
    if (priority == Low) true else false
}
case object High extends Priority {
  override def >(priority: Priority): Boolean =
    if (priority == Low || priority == Medium) true else false
}
