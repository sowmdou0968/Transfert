package models

import java.util.Date
import java.time.LocalDate

import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global

case class  Transfert(id: Int
                      ,date_transfert: LocalDate
                      ,amount: Double
                     )


// Definition of the SUPPLIERS table
class Transferts(tag: Tag) extends Table[Transfert](tag, "Transfert") {
  def id = column[Int]("id", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def date_transfert = column[LocalDate]("date_transfert")
  def amount = column[Double]("amount")



  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id,date_transfert,amount) <> (Transfert.tupled, Transfert.unapply)
}
