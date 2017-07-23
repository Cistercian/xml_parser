package xml_parser

import grails.gorm.transactions.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Transactional
class ViewCounterService {

    private static final Logger logger = LoggerFactory.getLogger(ViewCounterService.class);

    /**
     * Функция увеличения счетчика просмотренных товаров.
     * На всякий случай синхронизируем для работы нескольких клиентов
     * @param product текущая сущность Product ()
     * @return void
     */
    def synchronized incrementCounter(Product product) {
        logger.debug("incrementCounter()")

        //рассчитываем номер текущей минуты
        Date currentTime = new Date();
        Long currentMinute = Math.floor(currentTime.getTime() / 60_000)

        logger.debug("Текущая минута: ${currentTime.getDateString()}: ${currentMinute}")

        //ищем в базе запись для данного Product и данной минуты, если ее нет - создаем.
        ViewCounter counter = ViewCounter.findByTimestampAndProduct(currentMinute, product) ?:
                new ViewCounter(count: 0, timestamp: currentMinute, product: product)

        counter.count++

        counter.save(flush: true, failOnError: true)
    }
}
