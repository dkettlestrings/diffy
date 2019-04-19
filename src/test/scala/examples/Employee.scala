package examples

import core.Diff

class Employee(val name: String, val email: String)

object EmployeeOperations {
  def changeName(newName: String): Diff[Employee] = { employee: Employee => new Employee(newName, employee.email)}

  def changeEmail(newEmail: String): Diff[Employee] = { employee: Employee => new Employee(employee.name, newEmail)}
}
