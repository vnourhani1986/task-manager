package com.task.manager

import com.task.manager.model.core
import com.task.manager.model.generic.{OrderTypes, Priority}
import com.task.manager.repository.{ProcessRepository, RepositoryFactory}
import com.task.manager.repository.RepositoryFactory.RepositoryTypes.RepositoryType

class TaskManager(
    processRepository: ProcessRepository
) {

  def add(process: core.Process): Unit =
    processRepository
      .insert(process)
      .foreach(_.kill())

  def list(orderBy: OrderTypes.OrderType): List[core.Process] =
    processRepository.get(orderBy)

  def kill(PID: String): Unit =
    processRepository
      .remove(PID)
      .foreach(_.kill())

  def killGroup(priority: Priority): Unit =
    processRepository
      .remove(priority)
      .foreach(_.kill())

  def killAll(): Unit =
    processRepository
      .removeAll()
      .foreach(_.kill())

}

object TaskManager {
  def apply(
      capacity: Int,
      repositoryType: RepositoryType
  ): TaskManager =
    new TaskManager(RepositoryFactory(capacity, repositoryType))
}
