package xml_parser

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.xml.sax.SAXParseException

/**
 * Контроллер для работы с импортом данных
 */
class DataController {

    def productService

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    def index() {
        render(view: 'data')
    }

    /**
     * Функция импорта xml-файла
     * @return view
     */
    def importXml() {
        logger.debug("importXML()")

        def sourceXml = request.getFile('sourceXml')

        if (sourceXml.empty) {
            logger.info("Импортируемый файл не найден")

            flash.message = message(code: 'data.import.filenotfound')
            render(view: 'data')
            return
        }

        flash.message = productService.parsingInputStream(sourceXml.getInputStream(), null)

        render(view: 'data')
    }
}
