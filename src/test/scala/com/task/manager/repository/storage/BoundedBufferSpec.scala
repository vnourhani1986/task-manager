package com.task.manager.repository.storage

import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class BoundedBufferSpec extends FlatSpec with Matchers {

  "add method with parameter 2 for BoundedBuffer with size 2 with state empty" should "change state to 2" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    list.add(2)
    list.toList shouldBe List(2)
  }

  "add method with parameter 6 for BoundedBuffer with size 2 with state (2, 5)" should "never change state" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // preparation
    list.add(2)
    list.add(5)
    // call method
    list.add(6)
    list.toList shouldBe List(2, 5)
  }

  "remove method with parameter 5 for BoundedBuffer with size 2 with state (2, 5)" should "change state to (2)" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // preparation
    list.add(2)
    list.add(5)
    // call method
    list.remove(5)
    list.toList shouldBe List(2)
  }

  "remove method with parameter 2, 5 for BoundedBuffer with size 2 with state (2, 5)" should "change state to ()" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // preparation
    list.add(2)
    list.add(5)
    // call method
    list.remove(2, 5)
    list.toList shouldBe Nil
  }

  "remove method with parameter 2, 5 for BoundedBuffer with size 2 with state (2, 6)" should "change state to (5)" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // preparation
    list.add(2)
    list.add(5)
    // call method
    list.remove(2, 6)
    list.toList shouldBe List(5)
  }

  "remove method with parameter 5 for BoundedBuffer with size 2 with state ()" should "never change state" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // call method
    list.remove(5)
    list.toList shouldBe Nil
  }

  "removeAll method for BoundedBuffer with size 2 with state (2, 5)" should "change state ()" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // preparation
    list.remove(5)
    list.add(5)
    // call method
    list.removeAll()
    list.toList shouldBe Nil
  }

  "removeAll method for BoundedBuffer with size 2 with state ()" should "change state ()" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // call method
    list.removeAll()
    list.toList shouldBe Nil
  }

  "find method for value 2 for BoundedBuffer with size 2 with state (2, 5)" should "return option 2" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // preparation
    list.add(2)
    list.add(5)
    // call method
    list.find(_ == 2) shouldBe Some(2)
  }

  "find method for parameter 2 for BoundedBuffer with size 2 with state (2, 5)" should "return none" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // preparation
    list.add(2)
    list.add(5)
    // call method
    list.find(_ == 6) shouldBe None
  }

  "filter method for value 2 for BoundedBuffer with size 2 with state (2, 5)" should "return list 2" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // preparation
    list.add(2)
    list.add(5)
    // call method
    list.filter(_ == 2) shouldBe List(2)
  }

  "filter method for parameter 6 for BoundedBuffer with size 2 with state (2, 5)" should "return empty list" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // preparation
    list.add(2)
    list.add(5)
    // call method
    list.filter(_ == 6) shouldBe Nil
  }

  "isFull method for BoundedBuffer with size 2 with state (2, 5)" should "return true" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // preparation
    list.add(2)
    list.add(5)
    // call method
    list.isFull shouldBe true
  }

  "isFull method for BoundedBuffer with size 2 with state (2)" should "return false" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // preparation
    list.add(2)
    // call method
    list.isFull shouldBe false
  }

  "isEmpty method for BoundedBuffer with size 2 with state (2, 5)" should "return false" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // preparation
    list.add(2)
    list.add(5)
    // call method
    list.isEmpty shouldBe false
  }

  "isEmpty method for BoundedBuffer with size 2 with state ()" should "return true" in {
    val fakeBuffer: ListBuffer[Int] = new mutable.ListBuffer[Int]
    val list = new BoundedBuffer[Int](2, fakeBuffer)
    // call method
    list.isEmpty shouldBe true
  }

}
