@main
def run(command: String) =
    command match
        case "split" => splitFile()
        case "crawl" => crawl()