import scala.io.Codec
def splitFile(): Unit =
    // Download content from: https://m.dfxsw.com/down/94098/
    val content = os.read.lines.stream(os.pwd / "content.txt", Codec.apply("GB18030"))
    var currentChapter = ""
    content.foreach: line =>
        if line.startsWith(" 第") then
            currentChapter = line.strip()
            println(currentChapter)
            os.write.over(os.pwd / "dfxsw" / s"$currentChapter.txt", currentChapter + "\n")
        else
            os.write.append(os.pwd / "dfxsw" / s"$currentChapter.txt", line.strip() + "\n")
