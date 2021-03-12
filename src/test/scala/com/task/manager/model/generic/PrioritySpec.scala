package com.task.manager.model.generic

import org.scalatest.{FlatSpec, Matchers}

class PrioritySpec extends FlatSpec with Matchers {

  "higher compare for priority of types Low and high" should "be false" in {
    Low > High shouldBe false
  }

  "higher compare for priority of types medium and high" should "be false" in {
    Medium > High shouldBe false
  }

  "higher compare for priority of types high and medium" should "be true" in {
    High > Medium shouldBe true
  }

  "higher compare for priority of types high and low" should "be false" in {
    High > Low shouldBe true
  }

  "higher compare for priority of types with themselves" should "be false" in {
    High > High shouldBe false
  }

  "priority ordering for list high, low, medium, high" should "be low, medium, high, high" in {
    val list = List[Priority](High, Low, Medium, Low)
    list.sorted shouldBe List[Priority](Low, Low, Medium, High)
  }

  "priority ordering for list low, low, medium, high" should "never change it" in {
    val list = List[Priority](Low, Low, Medium, High)
    list.sorted shouldBe List[Priority](Low, Low, Medium, High)
  }

}
