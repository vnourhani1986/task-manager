package com.task.manager.repository

import com.task.manager.model.core
import com.task.manager.model.core.Process
import com.task.manager.model.generic.{High, Low, Medium, OrderTypes}
import com.task.manager.repository.ProcessRepository.ProcessDAO
import com.task.manager.repository.storage.{BoundedBuffer, BoundedQueue}
import org.scalatest.{FlatSpec, Matchers}

class FIFOBaseRepositorySpec extends FlatSpec with Matchers {

  behavior of "FIFOBaseRepository with bounded queue with size 2"
  val fifoBaseRepository = new FIFOBaseRepository(
    new BoundedQueue[ProcessDAO](2)
  )
  "insert new process with PID = 1, Priority = High" should "accept processes and do not dequeue any process" in {
    fifoBaseRepository.insert(Process("1", High)) shouldBe List.empty[Process]
  }
  "insert new process with PID = 2, Priority = Low" should "accept processes and do not dequeue any process" in {
    fifoBaseRepository.insert(core.Process("2", Low)) shouldBe List
      .empty[Process]
  }
  "insert new process with PID = 3, Priority = Medium" should "dequeue first one (PID = 1, Priority = High)" in {
    fifoBaseRepository.insert(core.Process("3", Medium)) shouldBe List(
      core.Process("1", High)
    )
  }
  "get list of processes ordered by PID" should "be processes by PID = 2, PID = 3" in {
    fifoBaseRepository.get(OrderTypes.OrderByPID) shouldBe List(
      core.Process("2", Low),
      core.Process("3", Medium)
    )
  }
  "get list of processes ordered by Priority" should "be processes by PID = 2, PID = 3" in {
    fifoBaseRepository.get(OrderTypes.OrderByPriority) shouldBe List(
      core.Process("2", Low),
      core.Process("3", Medium)
    )
  }
  "get list of processes ordered by created time" should "be processes by PID = 2, PID = 3" in {
    fifoBaseRepository.get(OrderTypes.OrderByTime) shouldBe List(
      core.Process("2", Low),
      core.Process("3", Medium)
    )
  }
  "remove by PID = 3" should "return processes by PID = 3 and repository have PID = 2 only" in {
    fifoBaseRepository.remove("3") shouldBe Some(
      core.Process("3", Medium)
    )
    fifoBaseRepository.get(OrderTypes.OrderByPID) shouldBe List(
      core.Process("2", Low)
    )
  }
  "remove by priority Medium" should "return nothing and repository have PID = 2 only" in {
    fifoBaseRepository.remove(Medium) shouldBe List.empty[Process]
    fifoBaseRepository.get(OrderTypes.OrderByPID) shouldBe List(
      core.Process("2", Low)
    )
  }
  "remove all" should "return PID = 2 and queue is empty" in {
    fifoBaseRepository.removeAll shouldBe List(
      core.Process("2", Low)
    )
    fifoBaseRepository.get(OrderTypes.OrderByPID) shouldBe List.empty[Process]
  }

}

class DefaultRepositorySpec extends FlatSpec with Matchers {

  behavior of "DefaultRepository with bounded buffer with size 2"
  val defaultRepository = new DefaultRepository(BoundedBuffer(2))

  "insert new process with PID = 1, Priority = High" should "accept processes and do not dequeue any process" in {
    defaultRepository.insert(core.Process("1", High)) shouldBe List
      .empty[Process]
  }
  "insert new process with PID = 2, Priority = Low" should "accept processes and do not dequeue any process" in {
    defaultRepository.insert(core.Process("2", Low)) shouldBe List
      .empty[Process]
  }
  "insert new process with PID = 3, Priority = Medium" should "return no process" in {
    defaultRepository.insert(core.Process("3", Medium)) shouldBe List
      .empty[Process]
  }
  "get list of processes ordered by PID" should "be processes by PID = 1, PID = 2" in {
    defaultRepository.get(OrderTypes.OrderByPID) shouldBe List(
      core.Process("1", High),
      core.Process("2", Low)
    )
  }
  "get list of processes ordered by Priority" should "be processes by PID = 2, PID = 1" in {
    defaultRepository.get(OrderTypes.OrderByPriority) shouldBe List(
      core.Process("2", Low),
      core.Process("1", High)
    )
  }
  "get list of processes ordered by created time" should "be processes by PID = 1, PID = 2" in {
    defaultRepository.get(OrderTypes.OrderByTime) shouldBe List(
      core.Process("1", High),
      core.Process("2", Low)
    )
  }
  "remove by PID = 2" should "return processes by PID = 2 and repository have PID = 1 only" in {
    defaultRepository.remove("2") shouldBe Some(
      core.Process("2", Low)
    )
    defaultRepository.get(OrderTypes.OrderByPID) shouldBe List(
      core.Process("1", High)
    )
  }
  "remove by priority Medium" should "return nothing and repository have PID = 1 only" in {
    defaultRepository.remove(Medium) shouldBe List.empty[Process]
    defaultRepository.get(OrderTypes.OrderByPID) shouldBe List(
      core.Process("1", High)
    )
  }
  "remove all" should "return PID = 1 and queue is empty" in {
    defaultRepository.removeAll shouldBe List(
      core.Process("1", High)
    )
    defaultRepository.get(OrderTypes.OrderByPID) shouldBe List.empty[Process]
  }
}

class PriorityRepositorySpec extends FlatSpec with Matchers {

  behavior of "PriorityRepository with bounded buffer with size 2"
  val priorityRepository = new PriorityRepository(BoundedBuffer(2))

  "insert new process with PID = 1, Priority = High" should "accept processes and do not dequeue any process" in {
    priorityRepository.insert(core.Process("1", High)) shouldBe List
      .empty[Process]
  }
  "insert new process with PID = 2, Priority = Low" should "accept processes and do not dequeue any process" in {
    priorityRepository.insert(core.Process("2", Low)) shouldBe List
      .empty[Process]
  }
  "insert new process with PID = 3, Priority = Medium" should "dequeue first one (PID = 1, Priority = High)" in {
    priorityRepository.insert(core.Process("3", Medium)) shouldBe List(
      core.Process("2", Low)
    )
  }
  "get list of processes ordered by PID" should "be processes by PID = 1, PID = 3" in {
    priorityRepository.get(OrderTypes.OrderByPID) shouldBe List(
      core.Process("1", High),
      core.Process("3", Medium)
    )
  }
  "get list of processes ordered by Priority" should "be processes by PID = 3, PID = 1" in {
    priorityRepository.get(OrderTypes.OrderByPriority) shouldBe List(
      core.Process("3", Medium),
      core.Process("1", High)
    )
  }
  "get list of processes ordered by created time" should "be processes by PID = 1, PID = 3" in {
    priorityRepository.get(OrderTypes.OrderByTime) shouldBe List(
      core.Process("1", High),
      core.Process("3", Medium)
    )
  }
  "remove by PID = 3" should "return processes by PID = 3 and repository have PID = 1 only" in {
    priorityRepository.remove("3") shouldBe Some(
      core.Process("3", Medium)
    )
    priorityRepository.get(OrderTypes.OrderByPID) shouldBe List(
      core.Process("1", High)
    )
  }
  "remove by priority Medium" should "return nothing and repository have PID = 1 only" in {
    priorityRepository.remove(Medium) shouldBe List.empty[Process]
    priorityRepository.get(OrderTypes.OrderByPID) shouldBe List(
      core.Process("1", High)
    )
  }
  "remove all" should "return PID = 1 and queue is empty" in {
    priorityRepository.removeAll shouldBe List(
      core.Process("1", High)
    )
    priorityRepository.get(OrderTypes.OrderByPID) shouldBe List.empty[Process]
  }
}
