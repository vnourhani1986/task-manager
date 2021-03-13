package com.task.manager

import com.task.manager.model.core
import com.task.manager.model.core.Process
import com.task.manager.model.generic.{High, Low, Medium, OrderTypes}
import com.task.manager.repository.FIFOBaseRepository
import com.task.manager.repository.RepositoryFactory.RepositoryTypes
import org.scalatest.{FlatSpec, Matchers}
import org.mockito.Mockito._

class TaskManagerSpec extends FlatSpec with Matchers {

  behavior of "unit test for TaskManager with FIFOBaseRepository of a bounded queue with size 2"

  "add method for process1 and repository with state ()" should "insert it to repository and then kill nothing" in {
    val fifoBaseRepository: FIFOBaseRepository =
      mock(classOf[FIFOBaseRepository])
    val taskManager: TaskManager = new TaskManager(fifoBaseRepository)
    val process1: Process = mock(classOf[Process])
    when(fifoBaseRepository.insert(process1)).thenReturn(Nil)

    taskManager.add(process1)
    verify(fifoBaseRepository).insert(process1)
  }

  "add method for process2 and repository with state (process1)" should "insert it to repository and then kill the process1" in {
    val fifoBaseRepository: FIFOBaseRepository =
      mock(classOf[FIFOBaseRepository])
    val taskManager: TaskManager = new TaskManager(fifoBaseRepository)
    val process1: Process = mock(classOf[Process])
    val process2: Process = mock(classOf[Process])
    when(fifoBaseRepository.insert(process2)).thenReturn(List(process1))
    when(process1.kill()).thenCallRealMethod()

    taskManager.add(process2)
    verify(fifoBaseRepository).insert(process2)
    verify(process1).kill()
  }

  "kill method for process with PID = 1 and repository with state (process1)" should "remove it from repository and then kill the process1" in {
    val fifoBaseRepository: FIFOBaseRepository =
      mock(classOf[FIFOBaseRepository])
    val taskManager: TaskManager = new TaskManager(fifoBaseRepository)
    val process1: Process = mock(classOf[Process])
    when(fifoBaseRepository.remove("1")).thenReturn(Some(process1))
    when(process1.kill()).thenCallRealMethod()

    taskManager.kill("1")
    verify(fifoBaseRepository).remove("1")
    verify(process1).kill()
  }

  "killGroup method for process 1, 2 and same priority Low and repository with state (process1, process2)" should "remove them it from repository and then kill the process 1, 2" in {
    val fifoBaseRepository: FIFOBaseRepository =
      mock(classOf[FIFOBaseRepository])
    val taskManager: TaskManager = new TaskManager(fifoBaseRepository)
    val process1: Process = mock(classOf[Process])
    val process2: Process = mock(classOf[Process])
    when(fifoBaseRepository.remove(Low)).thenReturn(List(process1, process2))
    when(process1.kill()).thenCallRealMethod()

    taskManager.killGroup(Low)
    verify(fifoBaseRepository).remove(Low)
    verify(process1).kill()
    verify(process2).kill()
  }

  "killAll method with repository with state (process1, process2)" should "remove them it from repository and then kill the process 1, 2" in {
    val fifoBaseRepository: FIFOBaseRepository =
      mock(classOf[FIFOBaseRepository])
    val taskManager: TaskManager = new TaskManager(fifoBaseRepository)
    val process1: Process = mock(classOf[Process])
    val process2: Process = mock(classOf[Process])
    when(fifoBaseRepository.removeAll()).thenReturn(List(process1, process2))
    when(process1.kill()).thenCallRealMethod()

    taskManager.killAll()
    verify(fifoBaseRepository).removeAll()
    verify(process1).kill()
    verify(process2).kill()
  }

  "list method with PID priority with repository with state (process1, process2)" should "return process1, 2 in order" in {
    val fifoBaseRepository: FIFOBaseRepository =
      mock(classOf[FIFOBaseRepository])
    val taskManager: TaskManager = new TaskManager(fifoBaseRepository)
    taskManager.list(OrderTypes.OrderByPID)
    verify(fifoBaseRepository).get(OrderTypes.OrderByPID)
  }

}

class FIFOTaskManagerSpec extends FlatSpec with Matchers {

  behavior of "Integration test for TaskManager with FIFOBaseRepository of a bounded queue with size 2"

  val fifoBaseRepository: FIFOBaseRepository = FIFOBaseRepository(2)
  val taskManager: TaskManager = new TaskManager(fifoBaseRepository)
  val process1: core.Process = Process("1", High)
  val process2: Process = core.Process("2", Low)
  val process3: Process = core.Process("3", Medium)
  val process4: Process = core.Process("4", Low)

  "add method with process PID = 1, Priority = High and state ()" should "add process to the list and nothing removed" in {
    taskManager.add(process1)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1)
  }

  "add method with process PID = 1, Priority = High and state (Process(1, High))" should "never add anything" in {
    taskManager.add(process1)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1)
  }

  "add method with process PID = 2, Priority = Low and state (Process(1, High))" should "add process to the list and nothing removed" in {
    taskManager.add(process2)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1, process2)
  }

  "list method with ordering with PID and state (Process(1, High), Process(2, Low))" should "return List(process(1), process(2) )" in {
    taskManager
      .list(OrderTypes.OrderByPID) shouldBe List(process1, process2)
  }

  "list method with ordering with Priority and state (Process(1, High), Process(2, Low))" should "return List(process(2), process(1) )" in {
    taskManager
      .list(OrderTypes.OrderByPriority) shouldBe List(process2, process1)
  }

  "list method with ordering with creation time and state (Process(1, High), Process(2, Low))" should "return List(process(1), process(2) )" in {
    taskManager
      .list(OrderTypes.OrderByTime) shouldBe List(process1, process2)
  }

  "add method with process PID = 3, Priority = Medium and state (Process(1, High), Process(2, Low))" should "add process to the list and will remove process with PID 1" in {
    taskManager.add(process3)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process2, process3)
  }

  "kill method with process PID = 3, Priority = Medium and state (Process(2, Low), Process(3, Medium))" should "remove process with PID 3" in {
    taskManager.kill("3")
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process2)
  }

  "add method with process PID = 4, Priority = Low and state (Process(2, Low))" should "add process to the list and nothing removed" in {
    taskManager.add(process4)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process2, process4)
  }

  "kill group method for processes with priority Low and state (Process(2, Low), Process(4, Low))" should "remove process all low priority processes" in {
    taskManager.killGroup(Low)
    taskManager.list(OrderTypes.OrderByPID) shouldBe Nil
  }

  "kill all method for state (Process(1, High), Process(4, Low))" should "remove all processes" in {
    // preparation
    taskManager.add(process1)
    taskManager.add(process4)
    // call method
    taskManager.killAll()
    taskManager.list(OrderTypes.OrderByPID) shouldBe Nil
  }

}

class DefaultTaskManagerSpec extends FlatSpec with Matchers {

  behavior of "Integration test for TaskManager with DefaultRepository of a bounded queue with size 2"

  val taskManager: TaskManager = TaskManager(2, RepositoryTypes.Default)
  val process1: Process = core.Process("1", High)
  val process2: Process = core.Process("2", Low)
  val process3: Process = core.Process("3", Medium)
  val process4: Process = core.Process("4", Low)

  "add method with process PID = 1, Priority = High and state ()" should "add process to the list and nothing removed" in {
    taskManager.add(process1)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1)
  }

  "add method with process PID = 1, Priority = High and state (Process(1, High))" should "never add anything" in {
    taskManager.add(process1)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1)
  }

  "add method with process PID = 2, Priority = Low and state (Process(1, High))" should "add process to the list and nothing removed" in {
    taskManager.add(process2)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1, process2)
  }

  "list method with ordering with PID and state (Process(1, High), Process(2, Low))" should "return List(process(1), process(2) )" in {
    taskManager
      .list(OrderTypes.OrderByPID) shouldBe List(process1, process2)
  }

  "list method with ordering with Priority and state (Process(1, High), Process(2, Low))" should "return List(process(2), process(1) )" in {
    taskManager
      .list(OrderTypes.OrderByPriority) shouldBe List(process2, process1)
  }

  "list method with ordering with creation time and state (Process(1, High), Process(2, Low))" should "return List(process(1), process(2) )" in {
    taskManager
      .list(OrderTypes.OrderByTime) shouldBe List(process1, process2)
  }

  "add method with process PID = 3, Priority = Medium and state (Process(1, High), Process(2, Low))" should "not add process to the list" in {
    taskManager.add(process3)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1, process2)
  }

  "kill method with process PID = 2, Priority = Medium and state (Process(1, High), Process(2, Low))" should "remove process with PID 2" in {
    taskManager.kill("2")
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1)
  }

  "add method with process PID = 4, Priority = Low and state (Process(1, High))" should "add process to the list and nothing removed" in {
    taskManager.add(process4)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1, process4)
  }

  "kill group method for processes with priority Low and state (Process(1, High), Process(4, Low))" should "remove process all low priority processes" in {
    taskManager.killGroup(Low)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1)
  }

  "kill all method for state (Process(1, High), Process(4, Low))" should "remove all processes" in {
    // preparation
    taskManager.add(process4)
    // call method
    taskManager.killAll()
    taskManager.list(OrderTypes.OrderByPID) shouldBe Nil
  }

}

class PriorityTaskManagerSpec extends FlatSpec with Matchers {

  behavior of "Integration test for TaskManager with PriorityBaseRepository of a bounded queue with size 2"

  val taskManager: TaskManager = TaskManager(2, RepositoryTypes.PriorityBase)
  val process1: Process = core.Process("1", High)
  val process2: Process = core.Process("2", Low)
  val process3: Process = core.Process("3", Medium)
  val process4: Process = core.Process("4", Low)

  "add method with process PID = 1, Priority = High and state ()" should "add process to the list and nothing removed" in {
    taskManager.add(process1)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1)
  }

  "add method with process PID = 1, Priority = High and state (Process(1, High))" should "never add anything" in {
    taskManager.add(process1)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1)
  }

  "add method with process PID = 2, Priority = Low and state (Process(1, High))" should "add process to the list and nothing removed" in {
    taskManager.add(process2)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1, process2)
  }

  "list method with ordering with PID and state (Process(1, High), Process(2, Low))" should "return List(process(1), process(2) )" in {
    taskManager
      .list(OrderTypes.OrderByPID) shouldBe List(process1, process2)
  }

  "list method with ordering with Priority and state (Process(1, High), Process(2, Low))" should "return List(process(2), process(1) )" in {
    taskManager
      .list(OrderTypes.OrderByPriority) shouldBe List(process2, process1)
  }

  "list method with ordering with creation time and state (Process(1, High), Process(2, Low))" should "return List(process(1), process(2) )" in {
    taskManager
      .list(OrderTypes.OrderByTime) shouldBe List(process1, process2)
  }

  "add method with process PID = 3, Priority = Medium and state (Process(1, High), Process(2, Low))" should "remove task with priority low (PID = 2) and add new process to list" in {
    taskManager.add(process3)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process1, process3)
  }

  "kill method with process PID = 1, Priority = Medium and state (Process(1, High), Process(3, Medium))" should "remove process with PID 1" in {
    taskManager.kill("1")
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process3)
  }

  "add method with process PID = 4, Priority = Low and state (Process(3, Medium))" should "add process to the list and nothing removed" in {
    taskManager.add(process4)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process3, process4)
  }

  "kill group method for processes with priority Low and state (Process(3, Medium), Process(4, Low))" should "remove process all low priority processes" in {
    taskManager.killGroup(Low)
    taskManager.list(OrderTypes.OrderByPID) shouldBe List(process3)
  }

  "kill all method for state (Process(3, Medium))" should "remove all processes" in {
    // preparation
    taskManager.add(process4)
    // call method
    taskManager.killAll()
    taskManager.list(OrderTypes.OrderByPID) shouldBe Nil
  }

}
