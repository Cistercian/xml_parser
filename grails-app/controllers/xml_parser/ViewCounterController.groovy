package xml_parser

import grails.converters.JSON
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Контроллер для вывода данных по таблице view_counter (счетчик просмотров)
 */
class ViewCounterController {

    private static final Logger logger = LoggerFactory.getLogger(ViewCounterController.class);

    def index(){
        render(view: 'statistics')
    }

    /**
     * Функция отправляет статистику по просмотрам в формате "временной штамп в мс", "кол-во просмотров" для
     * прорисовки графика средствами flot.js
     *
     * @return JSON
     */
    def getCountersData() {
        logger.debug("getCountersData()")

        def counters = ViewCounter.executeQuery("select v.timestamp * 60000, sum(v.count) from ViewCounter v group by v.timestamp")

        render counters as JSON
    }
}
