


# Grammer

Base

    ignore sheet  { sheetName }
           sheets { sheetName*}
           sheet by pf { PratialFunction[String,Boolean] }
           column  { columnName }
           columns { columnNames }
           column by pf { PratialFunction[String,Boolean] }

    map    sheetName  { (sheetName -> sheetName) }
           sheetNames { (sheetName -> sheetName)*}
           columnName { (columnName -> columnName) }
           columnNames { (columnName -> columnName)*}
           columnName by pf { PratialFunction[String,String] }

    ensure column {columnName} exists
           column {columnName} set {value} whenEmpty
                                           when { String => Boolean }
                                           always
           column {columnName} refer {columnName} whenEmpty
                                                  when { String => Boolean}
                                                  always
           column {columnName} convert { String => String }
           column {columnName} throws error whenNotExists
                                            whenEmpty
                                            when { String => Boolean }

Scoping sheet

    onSheet({sheetName}) { ... }

ColumnAddress

    column({columnName}) at {SheetName}
                         @@ {SheetName}
    at({sheetName}) { ... }

Merge and split

    # can declare outside of SheetScope
    merge sheet {sheetName} from {columnAddress*}
    split sheet {sheetName} into {columnAddress*}
    modify sheet {sheetName} rename {newName}
                             delete
    modify column {columnAddress} ... # => same as below
    # can declare only inside of SheetScope
    modify column {columnName} rename {newName}
                               convert { String => String }
                               delete
                               ignore
                               copyFrom {columnAddress}
                               copyTo   {columnAddress*}