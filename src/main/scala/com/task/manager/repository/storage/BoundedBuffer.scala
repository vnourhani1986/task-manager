package com.task.manager.repository.storage

import scala.collection.mutable.ListBuffer

class BoundedBuffer[A] private (capacity: Int, listBuffer: ListBuffer[A]) {

  def isFull: Boolean = listBuffer.length >= capacity

  def isEmpty: Boolean = listBuffer.isEmpty

  def add(a: A): Unit =
    if (listBuffer.length < capacity) listBuffer += a

  def find(f: A => Boolean): Option[A] =
    listBuffer.find(f)

  def filter(f: A => Boolean): List[A] =
    listBuffer.filter(f).toList

  def remove(a: A): List[A] =
    listBuffer.find(_ == a) match {
      case Some(value) =>
        listBuffer -= value
        List(value)
      case None =>
        Nil
    }

  def remove(elems: A*): List[A] = {
    listBuffer --= elems
    elems.toList
  }

  def removeAll(): List[A] = {
    val result = listBuffer.toList
    listBuffer --= listBuffer
    result
  }

  def toList: List[A] = listBuffer.toList

}

object BoundedBuffer {
  def apply[A](capacity: Int): BoundedBuffer[A] =
    new BoundedBuffer(capacity, new ListBuffer[A])
}
