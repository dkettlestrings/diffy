package examples

import core.{BasicIDGenerator, Project}
import org.scalatest.{FlatSpec, Matchers}
import strategies.SuperSafeStrategy

class BasicUsage extends FlatSpec with Matchers {

  "You" should "learn how to create a Project and make commits" in {

    // We begin by creating an Employee which we will use to seed the history.
    val originalEmployee = new Employee("David Kettlestrings", "david.kettlestrings@company.com")
    val project = new Project[Employee](originalEmployee, "employee-history", BasicIDGenerator)

    // So what is in this project object?  This is your project (in the
    // repository sense).  It contains your branches, lets you commit
    // to them, create them, merge them, etc.  Let's take a look at what's
    // inside.  There isn't much now, but we have created the master branch.

    project.getBranches().size should be (1)
    project.getBranches().toList.head.name should be ("master")

    // As you see, by default all names are inferred to be relative to the
    // project name.  You can use the fully qualified name if you want,
    // though.
    project.getFullyQualifiedName(project.getBranch("master")) should be ("employee-history/master")

    // Let's make a change to our employee.  Let's give him a middle initial.
    // Instead of creating a new branch, let's work off of master
    val addMiddleInitial = EmployeeOperations.changeName("David E. Kettlestrings")
    val master_v2 = project.commit(Seq(addMiddleInitial), project.getBranch("master"))

    // I'd like to take the time to go into a technical note.  Note that when
    // we commit, we get a new Branch object back.  In general, this is how
    // this code is designed: we use immutable objects.  This may seem strange
    // since a version control system's main purpose is to mutate history.  The
    // place where mutation occurs is deep within the Project object.  When you
    // commit, branch, merge, etc., we swap out the old Branch object(s) for
    // the new ones.  So what is the takeaway?  To stay up to date, I suggest
    // referencing branches not by persistent object references, but rather by
    // invoking project.getBranch(branchName).  In fact, capturing the result
    // of commits, branches, merges, etc. is probably not useful very often.
    // Another good reason to use getBranch() is for concurrency reasons.
    // This Project object is a synchronization mechanism.

    // OK, back to the example; let's see what we've got.  The resolve()
    // method recursively applies diffs to get you an instance.  The latest
    // version should be different than the original.
    val latest = project.getBranch("master").latest.resolve()
    latest.name should be ("David E. Kettlestrings")
    latest.email should be ("david.kettlestrings@company.com")

    //The earlier version should still be intact.
    val earlier = project.getBranch("master").beforeLatest(1).resolve()
    earlier.name should be ("David Kettlestrings")
    earlier.email should be ("david.kettlestrings@company.com")
  }

  "You" should "learn how to branch and merge" in {

    // We start the same as before
    val originalEmployee = new Employee("David Kettlestrings", "david.kettlestrings@company.com")
    val project = new Project[Employee](originalEmployee, "employee-history", BasicIDGenerator)

    // Now let's branch from here.  Note that per my comments above, I'm not
    // capturing the resulting Branch object.
    project.branchFrom(project.getBranch("master").latest, "newBranch", "master")

    // The branch now exists and has a single node in it.  What is that node?
    // It is a "copy" of the node you branched from.  Actually, it's not a
    // copy, but a new node whose parent is the node you branched from.  The
    // diff is just a NoOp (no operation).
    project.getBranch("master/newBranch").nodes.size should be (1)


    // We now have two branches: see?
    project.getBranches().size should be (2)
    project.getBranch("master") should be
    project.getBranch("master/newBranch") should be

    // Let's make edits on each of the branches
    val addMiddleInitial = EmployeeOperations.changeName("David E. Kettlestrings")
    project.commit(Seq(addMiddleInitial), project.getBranch("master"))

    val changeEmailDomain = EmployeeOperations.changeEmail("david.kettlestrings@business.com")
    project.commit(Seq(changeEmailDomain), project.getBranch("master/newBranch"))

    // Now for the scary part: merge.  Here we give a simple example using the
    // NaiveMergeStrategy.  We will merge newBranch back into (onto) master.
    val merged = project.merge(project.getBranch("master/newBranch"), project.getBranch("master"), SuperSafeStrategy.of[Employee]())

    // Since merging is inherently error-prone, the result of a merge is a Try.
    merged.isSuccess should be (true)

    // What is in this merged branch?  We are merging newBranch into (onto)
    // master, so we expect the following:

    // The name is "master"
    merged.get.name should be ("master")

    // The state of latest in master should have both the name and email changes
    project.getBranch("master").latest.resolve().name should be("David E. Kettlestrings")
    project.getBranch("master").latest.resolve().email should be ("david.kettlestrings@business.com")

    // The previous commit in master should have the name change but original email
    project.getBranch("master").beforeLatest(1).resolve().name should be ("David E. Kettlestrings")
    project.getBranch("master").beforeLatest(1).resolve().email should be("david.kettlestrings@company.com")

  }

}
