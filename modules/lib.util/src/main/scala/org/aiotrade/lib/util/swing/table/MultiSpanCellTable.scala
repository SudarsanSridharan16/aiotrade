/*
 * (swing1.1beta3)
 * 
 */
package org.aiotrade.lib.util.swing.table

import java.awt.Point
import java.awt.Rectangle
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionEvent
import javax.swing.table.TableColumnModel
import javax.swing.table.TableModel

/**
 * @version 1.0 11/26/98
 */
class MultiSpanCellTable(model: TableModel) extends JTable(model) {

  setUI(new MultiSpanCellTableUI)
  getTableHeader().setReorderingAllowed(false)
  setCellSelectionEnabled(true)
  setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION)

  override def getCellRect(arow: Int, acolumn: Int, includeSpacing: Boolean): Rectangle = {
    if (arow < 0 || acolumn < 0 || getRowCount <= arow || getColumnCount <= acolumn) {
      return super.getCellRect(arow, acolumn, includeSpacing)
    }

    var row = arow
    var column = acolumn
    val cellAtt = getModel.asInstanceOf[AttributiveCellTableModel].getCellAttribute.asInstanceOf[CellSpan]
    if (!cellAtt.isVisible(row, column)) {
      val temp_row = row
      val temp_column = column
      row += cellAtt.getSpan(temp_row, temp_column)(CellSpan.ROW)
      column += cellAtt.getSpan(temp_row, temp_column)(CellSpan.COLUMN)
    }
    val spans = cellAtt.getSpan(row, column)

    val cmodel = getColumnModel
    val cm = cmodel.getColumnMargin
    val r = new Rectangle
    val aCellHeight = rowHeight + rowMargin
    r.y = row * aCellHeight
    r.height = spans(CellSpan.ROW) * aCellHeight

    if (getComponentOrientation.isLeftToRight) {
      for (i <- 0 until column) {
        r.x += cmodel.getColumn(i).getWidth()
      }
    } else {
      for (i <- cmodel.getColumnCount - 1 until column) {
        r.x += cmodel.getColumn(i).getWidth
      }
    }
    r.width = cmodel.getColumn(column).getWidth
        
    for (i <- 0 until spans(CellSpan.COLUMN) - 1) {
      r.width += cmodel.getColumn(column + i).getWidth + cm
    }

    if (!includeSpacing) {
      val rm = getRowMargin
      r.setBounds(r.x + cm / 2, r.y + rm / 2, r.width - cm, r.height - rm)
    }
    return r;
  }

  private def rowColumnAtPoint(point: Point): Array[Int] = {
    val retValue = Array(-1, -1)
    val row = point.y / (rowHeight + rowMargin)
    if ((row < 0) || (getRowCount <= row)) {
      return retValue
    }
    val column = getColumnModel.getColumnIndexAtX(point.x)

    val cellAtt = getModel.asInstanceOf[AttributiveCellTableModel].getCellAttribute.asInstanceOf[CellSpan]

    if (cellAtt.isVisible(row, column)) {
      retValue(CellSpan.COLUMN) = column
      retValue(CellSpan.ROW) = row
      return retValue
    }
    
    retValue(CellSpan.COLUMN) = column + cellAtt.getSpan(row, column)(CellSpan.COLUMN)
    retValue(CellSpan.ROW) = row + cellAtt.getSpan(row, column)(CellSpan.ROW)
    retValue
  }

  override def rowAtPoint(point: Point): Int = {
    rowColumnAtPoint(point)(CellSpan.ROW)
  }

  override def columnAtPoint(point: Point): Int = {
    rowColumnAtPoint(point)(CellSpan.COLUMN)
  }

  override def columnSelectionChanged(e: ListSelectionEvent): Unit = {
    repaint()
  }

  override def valueChanged(e: ListSelectionEvent): Unit = {
    val firstIndex = e.getFirstIndex
    val lastIndex = e.getLastIndex
    if (firstIndex == -1 && lastIndex == -1) { // Selection cleared.
      repaint()
    }
    val dirtyRegion = getCellRect(firstIndex, 0, false);
    val numCoumns = getColumnCount
    var index = firstIndex;
    for (i <- 0 until numCoumns) {
      dirtyRegion.add(getCellRect(index, i, false))
    }
    index = lastIndex;
    for (i <- 0 until numCoumns) {
      dirtyRegion.add(getCellRect(index, i, false))
    }
    repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height)
  }
}
