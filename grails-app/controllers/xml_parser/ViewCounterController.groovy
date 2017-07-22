package xml_parser

import grails.converters.JSON

class ViewCounterController {

    def index(){
        render(view: 'statistics')
    }

    /**
     * Функция отправляет статистику по просмотрам в формате "временной штамп в мс", "кол-во просмотров"
     * @return JSON
     */
    def getCountersData() {
        println("getCountersData()")

        def counters = ViewCounter.executeQuery("select v.timestamp * 60000, sum(v.count) from ViewCounter v group by v.timestamp")

        render counters as JSON
    }
}
