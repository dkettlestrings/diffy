import core.{Branch, ChildNode, SeedNode}
import org.scalatest.{Matchers, FlatSpec}

class TestBranch extends FlatSpec with Matchers {

  "A Branch" should "take commits" in {

    val person_v1 = new Person("dave")
    val person1Node = new SeedNode[Person](person_v1, "uuid-1")
    val branch_v1 = new Branch[Person](Seq(person1Node), "some-branch")

    val diff = PersonOperations.changeName("dave2")
    val person_v2 = diff(person_v1)
    val person2Node = new ChildNode[Person](person1Node, Seq(diff), "uuid-2")
    val branch_v2 = branch_v1.commit(person2Node)

    branch_v1.nodes.size should be (1)
    branch_v1.name should be ("some-branch")

    branch_v2.nodes.size should be (2)
    branch_v2.name should be (branch_v1.name)

    val latestPerson: Person = branch_v2.latest.resolve()
    latestPerson.name should be (person_v2.name)
  }

  "A Branch" should "not require a SeedNode as its first element" in {

    val person_v1 = new Person("dave")
    val person1Node = new SeedNode[Person](person_v1, "uuid-1")

    val diff_12 = PersonOperations.changeName("dave2")
    val person_v2 = diff_12(person_v1)
    val person2Node = new ChildNode[Person](person1Node, Seq(diff_12), "uuid-2")

    val diff_23 = PersonOperations.changeName("dave3")
    val person_v3 = diff_23(person_v2)
    val person3Node = new ChildNode[Person](person2Node, Seq(diff_23), "uuid-3")

    val branch = new Branch[Person](Seq(person2Node, person3Node), "some-branch")

    branch.nodes.size should be (2)
    branch.latest.resolve().name should be (person_v3.name)
  }

  "A Branch" should "not allow commits that don't point to the current latest node" in {
    val person = new Person("dave")
    val person1Node = new SeedNode[Person](person, "uuid-1")

    val branch_v1 = new Branch[Person](Seq(person1Node), "branch")

    val person2Node = new ChildNode[Person](person1Node, Seq(PersonOperations.changeName("bob")), "uuid-2")
    val branch_v2 = branch_v1.commit(person2Node)

    val person3Node = new ChildNode[Person](person1Node, Seq(PersonOperations.changeName("chuck")), "uuid-3")

    intercept[RuntimeException] {branch_v2.commit(person3Node)}

  }

}
