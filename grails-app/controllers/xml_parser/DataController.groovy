package xml_parser

import org.xml.sax.SAXParseException

class DataController {

    def productService

    def index() {
        render(view: 'data')
    }

    /**
     * Функция импорта xml-файла
     * @return view
     */
    def importXml() {
        def sourceXml = request.getFile('sourceXml')

        if (sourceXml.empty) {
            flash.message = message(code: 'data.import.filenotfound')
            render(view: 'data')
            return
        }

        //TODO: перехват ошибок!
        try {
            def xmlContent = productService.getXmlContent(sourceXml)
            flash.message = productService.importProductsXml(xmlContent)
        } catch (SAXParseException e) {
            flash.message = message(code: 'data.import.exceptionparsing')
        } finally{
            if (flash.message != null) {
                render(view: 'data')
                return
            }
        }

        respond flash.message, view: 'data'
    }
}
