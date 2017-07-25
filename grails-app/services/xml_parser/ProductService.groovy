package xml_parser

import grails.gorm.transactions.Transactional
import grails.validation.ValidationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.xml.sax.SAXParseException

import java.util.concurrent.*

/**
 * Сервисный уровень главной таблицы
 */
@Transactional
class ProductService {

    def categoryService

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    //кол-во потоков
    private static final byte COUNT_THREAD = 10
    //длина очереди
    private static final byte QUEUE_LENGTH = 50
    //пул для многопоточной обработки xml файлов
    private static final ExecutorService consumers = Executors.newFixedThreadPool(COUNT_THREAD);
    //Ссылка на результат выполнения потока, читающего xml файл и заполняющего очередь queue
    private static FutureTask<String> futureProducer


    /**
     * Основная функция парсинга xml файла
     *
     * @param inputStream поток, из которого читаем xml контент
     * @param logWriter выходной поток для записи логов в файл (используется при фоновом парсинге для последующего
     * отображения на сайте)
     * @return String
     */
    def parsingInputStream(InputStream inputStream, BufferedWriter logWriter) {
        String result

        try {
            def startTime = System.currentTimeMillis()

            manualLogging(logWriter, "==============");
            manualLogging(logWriter, "${new Date(startTime)}: Запуск автоматической процедуры импорта данных.")

            def xmlContent = getXmlContent(inputStream)
            result = importXmlToDB(xmlContent)

            def time = System.currentTimeMillis() - startTime

            logger.info("Время обработки файла: ${time} ms")
            result = "${result}. Время обработки файла: ${time} ms."

            manualLogging(logWriter, "${new Date(System.currentTimeMillis())}: Завершение автоматической процедуры импорта данных.")

        } catch (SAXParseException e) {
            String text = "Ошибка парсинга файла: нарушена структура xml-файла. ${e.getMessage()}"

            logger.error(text)
            manualLogging(logWriter, text)

            result = text
        } catch (IOException e) {
            String text = "Ошибка открытия импортируемого файла. ${e.getMessage()}."

            logger.error(text)
            manualLogging(logWriter, text)

            result = text
        } finally {
            inputStream?.close();
        }

        return result ?: ""
    }

    /**
     * Парсинг xml файла. Используется XmlSlurper
     *
     * @param InputStream Поток для чтения
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
     * с реализацией многопоточности по алгоритму: читает файл и создает объекты 1 поток, сохраняют - COUNT_THREAD потоков
     * (операция сохранения самая долгая, время парсинга и преобразования считанных данных - несоизмеримо меньше)
     *
     * @param xmlContent Объект, распарсенный XmlSlurper
     * @return String
     */
    @Transactional
    private def importXmlToDB(def xmlContent) {
        logger.debug("importXmlToDB()")

        //очередь со считанными записями Product из файла, которые подлежат сохранению
        BlockingQueue<Product> queue = new ArrayBlockingQueue<Product>(QUEUE_LENGTH)

        //запускаем поток на парсинг файла
        futureProducer = new FutureTask<>(new Producer(queue, xmlContent))
        Thread producer = new Thread(futureProducer)

        producer.start()

        //создаем потоки на сохранение объектов из очереди queue в БД
        List futureListConsumers = new ArrayList();
        for (i in 1..COUNT_THREAD) {
            FutureTask futureTask = consumers.submit(new Consumer(queue))
            futureListConsumers.add(futureTask)
        }

        String result
        try {
            //не выходим из метода до полного завершения обработки для корректного замера длительности всего парсинга
            //TODO: Результат работы потока должен подтверждать успешность сохранения объекта в БД (для статистики)
            for (consumerTask in futureListConsumers) {
                if (!consumerTask.isDone())
                    Thread.sleep(1)
            }

            result = futureProducer.get()
        } catch (InterruptedException e) {
            result = "Ошибка получения результата парсинга"
        }
        logger.debug(result)

        return result
    }

    /**
     * класс, реализующий логику потока парсера и заполняющего очередь
     */
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
            logger.debug("${threadName}: запущен поток для парсинга файла и генерации объектов Product")

            def productId = 0
            def countSuccessful = 0, countError = 0

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

    /**
     * Класс, реализующий логику потока для сохранения записей из очереди в БД
     */
    class Consumer implements Runnable {
        BlockingQueue<Product> queue

        Consumer(BlockingQueue<Product> queue) {
            this.queue = queue
        }

        @Override
        void run() {
            String threadName = "${Thread.currentThread().getName()}"

            logger.debug("${threadName}: запущен поток для сохранения данных в БД.")
            Product product

            //Работаем до тех пор, пока работает поток парсинга или в очереди что-то есть
            while (!futureProducer.isDone() || queue.size() > 0) {
                try {
                    product = queue.poll()

                    if (product != null) {
                        saveEntity(product)
                        logger.debug("${threadName}: Объект сохранен в БД")
                    } else {
                        Thread.sleep(1)
                    }

                } catch (InterruptedException e) {
                    logger.debug("${threadName}: принудительная остановка потока.")
                    break
                }
            }

            logger.debug("${threadName}: остановлен поток для сохранения данных в БД.")
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
            product.rating = Float.valueOf(params.rating?.replace(',', '.'))
            //переопределяем категорию на основании текущего рейтинга
            recheckCategory(product)
        } catch (NumberFormatException | NullPointerException e) {
            logger.info("Ошибка преобразования рейтинга: ${e.getMessage()}")
        }
        try {
            String price = params.price?.replace(',', '.') ?: 'null'
            product.price = new BigDecimal(price)
        } catch (NumberFormatException e) {
            logger.info("Ошибка преобразования цены: ${e.getMessage()}")
        }

        product.clearErrors()
        product.validate()
    }

    /**
     * Пересчет категории продукта по его текущему рейтингу
     *
     * @param product текущая сущность
     */
    void recheckCategory(Product product) {
        logger.debug("recheckCategory()")
        product.category = categoryService.getByRating(product.rating)
    }

    /**
     * Служебная функция записи строки в выходной поток
     *
     * @param logWriter поток для записи
     * @param text записываемый текст
     */
    private void manualLogging(BufferedWriter logWriter, String text) {
        if (logWriter != null) logWriter.writeLine(text)
    }

    /**
     * Сохранение записи вынесено в отдельную функцию, т.к. автомаппинг спринга ломает сигнатуры наследуемых методов потоков...
     *
     * @param product Сохраняемая сущность
     * @return void
     */
    @Transactional
    private void saveEntity(Product product) {
        product.save(flush: true, failOnError: true);
    }
}
