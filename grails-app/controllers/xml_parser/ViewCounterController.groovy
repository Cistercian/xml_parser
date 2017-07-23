package xml_parser

import grails.converters.JSON
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ViewCounterController {

    private static final Logger logger = LoggerFactory.getLogger(ViewCounterController.class);

    def index(){
        render(view: 'statistics')
    }

    /**
     * Функция отправляет статистику по просмотрам в формате "временной штамп в мс", "кол-во просмотров"
     * @return JSON
     */
    def getCountersData() {
        logger.debug("getCountersData()")

        def counters = ViewCounter.executeQuery("select v.timestamp * 60000, sum(v.count) from ViewCounter v group by v.timestamp")

        render counters as JSON
    }
}
