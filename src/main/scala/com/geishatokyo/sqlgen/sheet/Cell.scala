package com.geishatokyo.sqlgen.sheet

import java.util.Date
import java.text.SimpleDateFormat
import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.util.{DateFormat, TimeUtil}
import org.apache.poi.ss.usermodel.DateUtil
import com.geishatokyo.sqlgen.logger.Logger

import scala.concurrent.duration.FiniteDuration

/**
 *
 * User: takeshita
 * Create: 12/07/11 21:05
 */

class Cell(parent : Sheet,private var _value : Any) {

  def value = _value
  def value_=(a: Any) = {
    a match{
      case c: Cell => {
        _value = c.value
      }
      case _ => {
        _value = a
      }
    }
  }

  var rowIndex = 0
  var columnIndex = 0

  def row = parent.row(rowIndex)
  def column = parent.column(columnIndex)

  var tag : Any = null

  def copy(newParent : Sheet) = {
    new Cell(newParent,value)
  }

  def :=( v : Any) = {
    v match{
      case c : Cell => this.value = c.value
      case a => this.value = a
    }
  }

  def isEmpty = value match {
    case null => true
    case NullValue => true
    case "" => true
    case _ => false
  }


  def asString = if(value == NullValue) {
    null
  }else if(value != null) {
    value.toString
  }else{
    ""
  }
  def asBool = value match{
    case true => true
    case false => false
    case "true" | "t" | "yes" | "1" => true
    case "false" | "f" | "no" | "0" | "" => false
    case _ => false
  }


  def asIntOp = try{
    value match {
      case i : Int => Some(i)
      case l : Long => Some(l.toInt)
      case s : String => {
        if(s.length == 0) Some(0)
        else Some(s.toInt)
      }
      case d : Double => Some(d.toInt)
      case f : Float => Some(f.toInt)
      case null => Some(0)
      case _ => {
        None
      }
    }
  }catch{
    case e : Throwable => None
  }
  def asInt = {
    niceGet(asIntOp)("Int")
  }


  def asLongOp = try{
    value match {
      case i : Int => Some(i.toLong)
      case l : Long => Some(l)
      case s : String => {
        if(s.length == 0) Some(0L)
        else Some(s.toLong)
      }
      case d : Double => Some(d.toLong)
      case f : Float => Some(f.toLong)
      case null => Some(0L)
      case _ => {
        None
      }
    }
  }catch{
    case e : Throwable => None
  }
  def asLong = {
    niceGet(asLongOp)("Long")
  }


  def asDoubleOp: Option[Double] = try{
    value match {
      case i : Int => Some(i.toDouble)
      case l : Long => Some(l.toDouble)
      case s : String => {
        if(s.length == 0) Some(0.0)
        else Some(s.toDouble)
      }
      case d : Double => Some(d)
      case f : Float => Some(f.toDouble)
      case null => Some(0.0)
      case _ => {
        None
      }
    }
  }catch{
    case e : Throwable => None
  }
  def asDouble = {
    niceGet(asDoubleOp)("Double")
  }

  def asDateOp: Option[Date] = asDateOpOfExcelTime
  def asDate = niceGet(asDateOp)("Date")

  def asDateOpOfUnixTime = try{
    value match {
      case i : Int => Some(new Date(i))
      case l : Long => Some(new Date(l))
      case s : String => DateFormat.parse(s)
      case d : Double => Some(new Date((d * 1000).toLong))
      case f : Float => Some(new Date( (f * 1000).toLong))
      case d : Date => Some(d)
      case _ => {
        None
      }
    }
  }catch{
    case e : Throwable => None
  }
  def asDateOpOfExcelTime : Option[Date] = try{
    asDoubleOp.map(d => TimeUtil.excelTimeToJavaDate(d)) orElse{
      DateFormat.parse(value.toString)
    }
  }catch{
    case e : Throwable => {
      Logger.log("Wring date format:" + value)
      None
    }
  }

  def niceGet[T]( op : Option[T])(typeName : String) = {
    op.getOrElse{
      throw new SQLGenException(s"Cell at ${parent.indexOf(this)} in sheet '${parent.name}' is not ${typeName}.value = ${value}")
    }
  }

  override def equals(obj: scala.Any): Boolean = {
    obj match{
      case c : Cell => c.value == value
      case v => v == value
    }
  }

  /**
   * アバウトなEqual
   * Valueの型によらず判定できる
    *
    * @param obj
   * @return
   */
  def ~==(obj : scala.Any): Boolean = {
    if(obj == null || value == null){
      obj == value
    }else {
      obj match {
        case c: Cell => this ~== c.value
        case v => {
          v == value ||
          v.toString == value.toString
        }
      }
    }
  }
  def !~==(obj : scala.Any): Boolean = !(this ~== obj)

  def +=( duration: FiniteDuration) = {
    asDateOp match{
      case Some(d) => {
        _value = new Date(d.getTime + duration.toMillis)
      }
      case _ => throw new Exception(s"Can't add duration to non date cell.(${rowIndex}:${columnIndex})@${parent.name}")
    }
  }
  def -=( duration: FiniteDuration) = {
    asDateOp match{
      case Some(d) => {
        _value = new Date(d.getTime - duration.toMillis)
      }
      case _ => throw new Exception(s"Can't add duration to non date cell.(${rowIndex}:${columnIndex})@${parent.name}")
    }
  }


  override def toString(): String = {
    if(value != null) value.toString
    else "''"
  }
}
