//> using scala 3.4.0
//> using dep org.jsoup:jsoup:1.17.2
//> using dep com.softwaremill.ox::core:0.0.21
//> using dep com.lihaoyi::os-lib:0.9.3

import org.jsoup.Jsoup
import ox.retry.RetryPolicy
import ox.retry.retry
import ox.syntax.foreachPar

import java.util.Random
import scala.concurrent.duration.given
import scala.jdk.CollectionConverters.*

@main
def crawl =
  val doc = Jsoup.connect("https://www.biquge635.com/book/40438/").get()
  val allChapters = doc.select("#section-list > li").asScala
  val rand = new Random()
  def parse_content(title: String, url: String): Unit =
    val path = os.pwd / "chapters" / s"$title.md"
    if !(os.exists(path)) then
      Thread.sleep(1000 + rand.nextInt(3000))
      println(s"Processing $title")
      val doc = Jsoup.connect(url).get()
      os.write.over(
        os.pwd / "chapters" / s"$title.md",
        doc
          .selectFirst("#content")
          .html()
          .split("<br>")
          .map(l =>
            l.strip()
              .replace("&nbsp;&nbsp;&nbsp;&nbsp;", "")
              .replace(
                """<script type="text/javascript" src="/sj.js"></script>""",
                ""
              )
          )
          .filter(_.length() > 0)
          .mkString("\n\n")
      )

  allChapters
    .foreachPar(5)(chap =>
      try
        retry(
          parse_content(chap.text(), chap.selectFirst("a").absUrl("href"))
        )(RetryPolicy.backoff(3, 100.milliseconds))
      catch
        case _ =>
          println(
            s"Error: ${chap.text()} with URL: ${chap.selectFirst("a").absUrl("href")}"
          )
    )
