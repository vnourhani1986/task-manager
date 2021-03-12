package com.task.manager.repository.storage

import scala.collection.mutable

class BoundedQueue[A](size: Int) extends mutable.Queue[A] {

  def enqueueAll(elems: A*): List[A] = {
    this ++= elems
    dequeueAll(super.size - size)
  }

  def dequeueAll(len: Int): List[A] =
    if (len > 0 && this.nonEmpty) this.dequeue() :: dequeueAll(len - 1) else Nil

}

object BoundedQueue {
  def apply[A](size: Int): BoundedQueue[A] = new BoundedQueue(size)
}
