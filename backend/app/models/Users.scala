package models

import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class  User(id: Int
                 ,lastName: String
                 ,firstName: String
                 ,email: String
                 ,password: String
                 ,tel: String
                 ,address: String
                )


// Definition of the SUPPLIERS table
class Users(tag: Tag) extends Table[User](tag, "User") {
  def id = column[Int]("idUser", O.PrimaryKey, O.AutoInc) // This is the primary key column
  def lastName = column[String]("LastName")
  def firstName = column[String]("FirstName")
  def email = column[String]("E-mail")
  def password  = column[String]("Password")
  def tel = column[String]("Tel")
  def address = column[String]("Address")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, lastName, firstName, email, password,tel, address) <> (User.tupled, User.unapply)
}

