package xml_parser

import grails.gorm.transactions.Transactional
import grails.validation.ValidationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.xml.sax.SAXParseException

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

/**
 * Сервисный уровень главной таблицы
 */
@Transactional
class ProductService {

    def categoryService

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private static final ExecutorService consumers = Executors.newFixedThreadPool(2);

    /**
     * Парсинг xml файла
     * @param file Переданный файл из запроса
     * @return отформатированный результат
     */
    private def getXmlContent(InputStream inputStreamXML) {
        logger.debug("getXmlContent()")

        def xmlContent = inputStreamXML?.getText()
        def parser = new XmlSlurper();

        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        return parser.parseText(xmlContent)
    }

    /**
     * Функция парсинга xml данных и импорта в БД сущностей product
     * @param xmlContent
     * @return
     */
    @Transactional
    private def importXmlToDB(def xmlContent) {
        logger.debug("importXmlToDB()")

        BlockingQueue<Product> queue = new ArrayBlockingQueue<Product>(50)

        FutureTask<String> futureProducer = new FutureTask<>(new Producer(queue, xmlContent))
        Thread producer = new Thread(futureProducer)
        //Future consumer = consumers.submit(new Consumer(queue))
        //Thread consumer = new Thread(new Consumer(queue))!!!! TODO: static

        producer.start()
        //consumer.start()

        String message
        try {
            message = futureProducer.get()
        } catch (InterruptedException e) {
            message = "Ошибка получения результата парсинга"
        }
        logger.debug(message)

        return message
    }

    class Producer implements Callable<String> {
        BlockingQueue<Product> queue
        def xmlContent

        Producer(BlockingQueue<Product> queue, def xmlContent) {
            this.queue = queue
            this.xmlContent = xmlContent
        }

        @Override
        String call() {
            String threadName = "${Thread.currentThread().getName()}"
            logger.debug("${threadName}: запущен поток для пасинга файла и генерации объектов Product")

            def productId
            def countSuccessful = 0, countError = 0
            logger.debug("${threadName}: Запуск парсинга XML")
            xmlContent.products.product.each { product ->
                try {
                    logger.debug("${threadName}: Импортируется сущность с наименованием: ${product.title[0].text()}")

                    //Если текущее поле product_id пустое или не существует - берем предыдущее известное значение
                    productId = product.product_id[0].text() ?: productId

                    //Если поле inet_price отсутствует или пустое - обнуляем поле price сущности, иначе - берем значение поля price
                    def price = product.inet_price[0]?.text()?.length() > 0 ? product.price[0]?.text() ?: 0 : 0

                    def rating = Float.valueOf(product.rating[0]?.text()) ?: Float.valueOf("0")

                    //расчитываем категорию товара
                    def category = categoryService.getByRating(rating);

                    def entity = new Product(
                            productId: productId,
                            title: product.title[0]?.text(),
                            description: product.description[0]?.text(),
                            rating: rating,
                            price: price,
                            image: product.image[0]?.text(),
                            category: category
                    )

                    logger.debug("${threadName}: Результирующая сущность product: ${entity}, category: ${entity.category}")

                    queue.put(entity)

                    countSuccessful++
                } catch (NumberFormatException | ValidationException e) {
                    logger.error("${threadName}: Ошибка парсинга записи product: ${e.getMessage()}")
                    countError++
                }
            }

            logger.debug("${threadName}: поток парсинга завершил свою работу.")

            String text = "Импорт завершен! Импортировано ${countSuccessful} объектов. Ошибок при импорте: ${countError}"

            return text
        }
    }

    class Consumer implements Runnable {
        BlockingQueue<Product> queue

        Consumer(BlockingQueue<Product> queue) {
            this.queue = queue
        }

        @Override
        void run() {
            String threadName = "${Thread.currentThread().getName()}"

            logger.debug("${threadName}: запущен поток для сохранения данных в БД.")
            while (true) {
                try {
                    Thread.sleep(1)

                    Product product = queue.poll()

                    if (product != null) {
                        saveEntity(product)

                        logger.debug("${threadName}: Объект сохранен в БД")
                    }

                } catch (InterruptedException e) {
                    logger.debug("${threadName}: принудительная остановка потока.")
                    break
                }
            }
        }
    }

    /**
     * Функция ручного парсинга полей rating и price (grails автоматически некорректно принимает данные).
     * Явно проблема в разных локалях на стороне сервера и клиента....
     * После парсинга рейтинга функция запускает пересчет категории, к которой относится текущая сущность
     *
     * @param product текущая сущность
     * @param params переданные данные от клиента
     * @return void
     */
    def fixNumberParsing(Product product, def params) {
        logger.debug("fixNumberParsing()")
        try {
            product.rating = Float.valueOf(params.rating.replace(',', '.'))
            //переопределяем категорию на основании текущего рейтинга
            recheckCategory(product)
        } catch (NumberFormatException e) {
            logger.info("Ошибка преобразования рейтинга: ${e.getMessage()}")
        }
        try {
            product.price = new BigDecimal(params.price.replace(',', '.'))
        } catch (NumberFormatException e) {
            logger.info("Ошибка преобразования цены: ${e.getMessage()}")
        }

        product.clearErrors()
        product.validate()
    }

    /**
     * Пересчет калегории продукта по его текущему рейтингу
     * @param product текущая сущность
     */
    void recheckCategory(Product product) {
        logger.debug("recheckCategory()")

        product.category = categoryService.getByRating(product.rating)
    }

    def parsingInputStream(InputStream inputStream, BufferedWriter logWriter) {
        String message

        try {
            def startTime = System.currentTimeMillis()

            manualLogging(logWriter, "==============");
            manualLogging(logWriter, "${new Date(startTime)}: Запуск автоматической процедуры импорта данных.")

            def xmlContent = getXmlContent(inputStream)
            message = importXmlToDB(xmlContent)

            def time = System.currentTimeMillis() - startTime

            logger.info("Время обработки файла: ${time} ms")
            message = "${message}. Время обработки файла: ${time} ms."

            manualLogging(logWriter, "${new Date(System.currentTimeMillis())}: Завершение автоматической процедуры импорта данных.")

        } catch (SAXParseException e) {
            String text = "Ошибка парсинга файла: нарушена структура xml-файла. ${e.getMessage()}"

            logger.error(text)
            manualLogging(logWriter, text)

            message = message(code: 'data.import.exceptionparsing')
        } catch (IOException e) {
            String text = "Ошибка открытия импортируемого файла. ${e.getMessage()}."

            logger.error(text)
            manualLogging(logWriter, text)

            message = message(code: 'data.import.ioexception')
        } finally {
            inputStream?.close();
        }

        return message
    }

    private void manualLogging(BufferedWriter logWriter, String message) {
        if (logWriter != null) logWriter.writeLine(message)
    }

    /**
     * Сохранение записи вынесено в отдельную функцию, т.к. автомаппинг спринга ломает сигнатуры наследуемых методов потоков...
     * @param product Сохраняемая сущность
     * @return void
     */
    @Transactional
    def saveEntity(Product product) {
        product.save(flush: true, failOnError: true);
    }
}
