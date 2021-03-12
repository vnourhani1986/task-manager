package com.task.manager.repository

import com.task.manager.repository.RepositoryFactory.RepositoryTypes.{
  Default,
  FIFOBase,
  PriorityBase,
  RepositoryType
}

object RepositoryFactory {

  def apply(capacity: Int, repositoryType: RepositoryType): ProcessRepository =
    repositoryType match {
      case Default      => DefaultRepository(capacity)
      case FIFOBase     => FIFOBaseRepository(capacity)
      case PriorityBase => PriorityRepository(capacity)
    }

  object RepositoryTypes extends Enumeration {
    type RepositoryType = Value
    val Default: RepositoryType = Value("default")
    val FIFOBase: RepositoryType = Value("fifo_base")
    val PriorityBase: RepositoryType = Value("priority_base")
  }

}
