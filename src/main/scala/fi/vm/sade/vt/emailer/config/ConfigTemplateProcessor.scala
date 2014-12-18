package fi.vm.sade.vt.emailer.config

import java.io.{File, FileInputStream, StringReader}
import java.util.{HashMap, Properties}

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.`type`.MapType
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.typesafe.config.ConfigFactory
import fi.vm.sade.vt.emailer.CommandLineArgs
import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalate.support.FileTemplateSource

import scala.collection.JavaConverters._

object ConfigTemplateProcessor {
  def createSettings(attributesFile: String, commandLineArgs: CommandLineArgs): ApplicationSettings = {
    val templateFile: String = "src/main/resources/oph-configuration/valinta-tulos-emailer.properties.template"
    val templatedData = ConfigTemplateProcessor.processTemplate(templateFile, attributesFile)
    val properties = new Properties()
    properties.load(new StringReader(templatedData))
    new ApplicationSettings(ConfigFactory.load(ConfigFactory.parseProperties(properties)), commandLineArgs)
  }

  def processTemplate(from: String, attributesFile: String): String = {
    val mapper: ObjectMapper = new ObjectMapper(new YAMLFactory())
    val mapType: MapType = mapper.getTypeFactory.constructMapType(classOf[HashMap[String, String]], classOf[String], classOf[String])
    val rawValue = mapper.readValue(new FileInputStream(attributesFile), mapType).asInstanceOf[HashMap[String, String]]
    val attributes: Map[String, Any] = rawValue.asScala.toMap.asInstanceOf[Map[String, Any]]
    val engine = new TemplateEngine

    val templateSource: FileTemplateSource = new FileTemplateSource(new File(from), "template.mustache")

    engine.layout(templateSource, attributes) + "\nmongodb.ensureIndex=false" // <- to make work with embedded mongo
  }
}
