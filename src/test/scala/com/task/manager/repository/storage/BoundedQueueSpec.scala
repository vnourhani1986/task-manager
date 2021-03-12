package com.task.manager.repository.storage

import org.scalatest.{FlatSpec, Matchers}

class BoundedQueueSpec extends FlatSpec with Matchers {

  "enqueueAll method with params 2, 5 for BoundedQueue with size 2 and state ()" should "change state to (2, 5)" in {
    val queue = BoundedQueue[Int](2)
    queue.enqueueAll(2, 5)
    queue.toList shouldBe List(2, 5)
  }

  "enqueueAll method with params 6 for BoundedQueue with size 2 and state (2, 5)" should "change state to (5, 6)" in {
    val queue = BoundedQueue[Int](2)
    // preparation
    queue.enqueueAll(2, 5)
    // call method
    queue.enqueueAll(6)
    queue.toList shouldBe List(5, 6)
  }

  "dequeueAll method with params 1 for BoundedQueue with size 2 and state (2, 5)" should "change state to (5)" in {
    val queue = BoundedQueue[Int](2)
    // preparation
    queue.enqueueAll(2, 5)
    // call method
    queue.dequeueAll(1)
    queue.toList shouldBe List(5)
  }

  "dequeueAll method with params 10 for BoundedQueue with size 2 and state (2, 5)" should "change state to ()" in {
    val queue = BoundedQueue[Int](2)
    // preparation
    queue.enqueueAll(2, 5)
    // call method
    queue.dequeueAll(10)
    queue.toList shouldBe Nil
  }

}
