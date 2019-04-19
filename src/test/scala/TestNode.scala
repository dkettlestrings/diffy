import core.SeedNode
import org.scalatest.{FlatSpec, Matchers}

class TestNode extends FlatSpec with Matchers {

  "Nodes" should "apply diffs from left to right" in {
    val dummyNode = SeedNode[Person](new Person("bob"), "dummy-id")
    def f(p: Person): Person = {new Person("bob smith")}
    def g(p: Person): Person = {new Person(p.name + " 1")}

    dummyNode.composeDiffs(Seq(f,g))(dummyNode.instance).name should be ("bob smith 1")

  }

}
