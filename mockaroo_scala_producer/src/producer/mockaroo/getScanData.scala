package producer.mockaroo

object getScanData extends App {
  var region = "02"
  var url = "https://api.mockaroo.com/api/91b59790?count=10&key=76b93870&region=" + region

  @throws(classOf[java.io.IOException])
  def get(url: String) = io.Source.fromURL(url).mkString
  
  var content = get(url)
  println(content)
		
}