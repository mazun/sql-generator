package com.geishatokyo.sqlgen.project3.flow

import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.sheet.Workbook

/**
  * Created by takezoux2 on 2016/08/05.
  */
trait Output{


  def output(context: Context,workbook: Workbook) : Unit



}