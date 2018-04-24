package models

import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class  Partner(id: Int
                    ,Name: String
                    ,Location: String
                    ,City: String
                    ,tel: String
                   )


// Definition of the SUPPLIERS table
class Partners(tag: Tag) extends Table[Partner](tag, "Partner") {
  def id = column[Int]("id", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def Name = column[String]("Name")
  def Location = column[String]("Location")
  def City = column[String]("City")
  def tel = column[String]("Tel")


  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, Name, Location,City,tel) <> (Partner.tupled, Partner.unapply)
}
