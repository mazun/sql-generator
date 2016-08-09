package com.geishatokyo.sqlgen.project.output

import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.project.flow.Output
import com.geishatokyo.sqlgen.sheet.Workbook

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:45
 */
class ConsoleOutput extends Output{

  def output(context: Context, w: Workbook) = {
    println(w)
  }
}