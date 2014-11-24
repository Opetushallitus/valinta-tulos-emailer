package json

import org.json4s._
import org.json4s.ext.JodaTimeSerializers


object JsonFormats {
  val genericFormats =  new DefaultFormats {
    override def dateFormatter = {
      val format = super.dateFormatter
      format.setTimeZone(DefaultFormats.UTC)
      format
    }
  } ++ JodaTimeSerializers.all
  val jsonFormats: Formats = JsonFormats.genericFormats
}

trait JsonFormats {
  implicit val jsonFormats: Formats = JsonFormats.jsonFormats
}