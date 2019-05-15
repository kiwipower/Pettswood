package org.pettswood

import org.pettswood.Result._

trait MultiRow extends Concept {
  
  def columns: PartialFunction[String, (String) => Probe]
  def initialiseRow() {}

  private var rowPointer = 0
  var currentProbes, probeTemplate = List.empty[(String) => Probe]

  override def row() {
    rowPointer += 1
    currentProbes = probeTemplate.reverse
    initialiseRow()
  }

  private def probeFor(text: String): (String) => Probe = try {
    columns(text)
  } catch {
    case e: MatchError => throw new RuntimeException("Unrecognised column probe: " + text)
    case e: NullPointerException => throw new RuntimeException("No column probes defined")
  }

  def cell(cellText: String) = rowPointer match {
    case 1 => Uninteresting()
    case 2 => probeTemplate = probeFor(cellText) :: probeTemplate; Uninteresting()
    case x => determineResult(cellText)
  }

  private def determineResult(cellText: String): Result = probeForCell(cellText) match {
      case descriptor: Descriptor => Description()
      case doer: Doer => Setup()
      case digger: Digger => given(cellText, digger.actual)
    }

  private def probeForCell(cellText: String): Probe = try { currentProbes.head(cellText) } finally { currentProbes = currentProbes.tail }
}