package producer.mockaroo

object simpleGetScanData extends App {
  var region = "02"
  var url = "https://api.mockaroo.com/api/91b59790?count=1000&key=76b93870&region=" + region

  @throws(classOf[java.io.IOException])
  def get(url: String) = scala.io.Source.fromURL(url).mkString
  
  var content = get(url)
  println(content)
}
