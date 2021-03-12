package com.task.manager.repository

import java.time.Instant

import com.task.manager.model.core
import com.task.manager.model.core.Process
import com.task.manager.repository.ProcessRepository.ProcessDAO
import com.task.manager.repository.storage.{BoundedBuffer, BoundedQueue}
import com.task.manager.model.generic.{Low, Medium, OrderTypes, Priority}

trait ProcessRepository {
  def insert(process: Process): List[Process]
  def remove(PID: String): Option[Process]
  def remove(priority: Priority): List[Process]
  def removeAll(): List[Process]
  def get(orderBy: OrderTypes.OrderType): List[Process]
}

object ProcessRepository {
  case class ProcessDAO(
      PID: String,
      priority: Priority,
      createdAt: Instant
  )

  object ProcessDAO {
    implicit def PIDOrdering: Ordering[ProcessDAO] =
      Ordering.by(_.PID)
    implicit def priorityOrdering: Ordering[ProcessDAO] =
      Ordering.by(_.priority)
    implicit def timeOrdering: Ordering[ProcessDAO] =
      Ordering.by(_.createdAt)
  }

  val processDAOToProcess: ProcessDAO => Process =
    processDAO =>
      core.Process(
        PID = processDAO.PID,
        priority = processDAO.priority
      )

  val processToProcessDAO: Process => ProcessDAO =
    process =>
      ProcessDAO(
        PID = process.PID,
        priority = process.priority,
        createdAt = Instant.now()
      )
}

class FIFOBaseRepository(
    queue: BoundedQueue[ProcessDAO]
) extends ProcessRepository {

  import ProcessRepository._

  override def insert(process: Process): List[Process] = {
    queue.find(_.PID == process.PID) match {
      case Some(_) => Nil
      case None =>
        queue
          .enqueueAll(processToProcessDAO(process))
          .map(processDAOToProcess)
    }

  }

  override def remove(PID: String): Option[Process] =
    queue
      .dequeueFirst(_.PID == PID)
      .map(processDAOToProcess)

  override def remove(priority: Priority): List[Process] =
    queue
      .dequeueAll(_.priority == priority)
      .map(processDAOToProcess)
      .toList

  override def removeAll(): List[Process] =
    queue
      .dequeueAll(queue.size)
      .map(processDAOToProcess)

  override def get(orderBy: OrderTypes.OrderType): List[Process] =
    orderBy match {
      case OrderTypes.OrderByPID =>
        queue.toList
          .sortBy(_.PID)
          .map(processDAOToProcess)
      case OrderTypes.OrderByPriority =>
        queue.toList
          .sortBy(_.priority)
          .map(processDAOToProcess)
      case OrderTypes.OrderByTime =>
        queue.toList
          .sortBy(_.createdAt)
          .map(processDAOToProcess)
    }

}

object FIFOBaseRepository {
  def apply(
      capacity: Int
  ): FIFOBaseRepository =
    new FIFOBaseRepository(BoundedQueue[ProcessDAO](capacity))
}

class DefaultRepository(buffer: BoundedBuffer[ProcessDAO])
    extends ProcessRepository {

  import ProcessRepository._

  override def insert(process: Process): List[Process] = {
    if (!buffer.isFull && buffer.find(_.PID == process.PID).isEmpty)
      buffer.add(processToProcessDAO(process))
    Nil
  }

  override def remove(PID: String): Option[Process] =
    buffer.find(_.PID == PID) match {
      case Some(value) =>
        buffer.remove(value)
        Some(processDAOToProcess(value))
      case None => None
    }

  override def remove(priority: Priority): List[Process] =
    buffer.filter(_.priority == priority) match {
      case list =>
        buffer.remove(list: _*)
        list.map(processDAOToProcess)
    }

  override def removeAll(): List[Process] =
    buffer.removeAll().map(processDAOToProcess)

  override def get(orderBy: OrderTypes.OrderType): List[Process] =
    orderBy match {
      case OrderTypes.OrderByPID =>
        buffer.toList
          .sortBy(_.PID)
          .map(processDAOToProcess)
      case OrderTypes.OrderByPriority =>
        buffer.toList
          .sortBy(_.priority)
          .map(processDAOToProcess)
      case OrderTypes.OrderByTime =>
        buffer.toList
          .sortBy(_.createdAt)
          .map(processDAOToProcess)
    }
}

object DefaultRepository {
  def apply(capacity: Int): DefaultRepository =
    new DefaultRepository(BoundedBuffer[ProcessDAO](capacity))
}

class PriorityRepository(buffer: BoundedBuffer[ProcessDAO])
    extends ProcessRepository {
  import ProcessRepository._

  override def insert(process: Process): List[Process] =
    if (!buffer.isFull && buffer.find(_.PID == process.PID).isEmpty) {
      buffer.add(processToProcessDAO(process))
      Nil
    } else {
      find(process.priority)
        .map { processDAO =>
          buffer.remove(processDAO)
          buffer.add(processToProcessDAO(process))
          processDAO
        }
        .map(processDAOToProcess)
        .toList
    }

  private def find(priority: Priority): Option[ProcessDAO] =
    if (priority > Medium) {
      buffer.find(_.priority == Low) match {
        case None        => buffer.find(_.priority == Medium)
        case Some(value) => Some(value)
      }
    } else if (priority > Low) {
      buffer.find(_.priority == Low)
    } else None

  override def remove(PID: String): Option[Process] =
    buffer.find(_.PID == PID) match {
      case Some(value) =>
        buffer.remove(value)
        Some(value).map(processDAOToProcess)
      case None => None
    }

  override def remove(priority: Priority): List[Process] =
    buffer.filter(_.priority == priority) match {
      case list =>
        buffer.remove(list: _*)
        list.map(processDAOToProcess)
    }

  override def removeAll(): List[Process] =
    buffer.removeAll().map(processDAOToProcess)

  override def get(orderBy: OrderTypes.OrderType): List[Process] =
    orderBy match {
      case OrderTypes.OrderByPID =>
        buffer.toList
          .sortBy(_.PID)
          .map(processDAOToProcess)
      case OrderTypes.OrderByPriority =>
        buffer.toList
          .sortBy(_.priority)
          .map(processDAOToProcess)
      case OrderTypes.OrderByTime =>
        buffer.toList
          .sortBy(_.createdAt)
          .map(processDAOToProcess)
    }
}

object PriorityRepository {
  def apply(capacity: Int): PriorityRepository =
    new PriorityRepository(BoundedBuffer[ProcessDAO](capacity))
}
