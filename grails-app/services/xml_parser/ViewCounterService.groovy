package xml_parser

import grails.gorm.transactions.Transactional

@Transactional
class ViewCounterService {

    def synchronized incrementCounter(Product product) {
        println("incrementCounter()")

        Date currentTime = new Date();
        Long currentMinute = Math.floor(currentTime.getTime() / 60_000)

        println("Текущая минута: ${currentTime.getDateString()}: ${currentMinute}")

        ViewCounter counter = ViewCounter.findByTimestampAndProduct(currentMinute, product) ?:
                new ViewCounter(count: 0, timestamp: currentMinute, product: product)

        counter.count ++

        counter.save(flush: true, failOnError: true)
    }
}
