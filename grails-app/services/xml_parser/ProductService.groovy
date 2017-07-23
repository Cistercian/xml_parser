package xml_parser

import grails.gorm.transactions.Transactional
import grails.validation.ValidationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest

@Transactional
class ProductService {

    def categoryService

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    /**
     * Парсинг xml файла
     * @param file Переданный файл из запроса
     * @return отформатированный результат
     */
    def getXmlContent(StandardMultipartHttpServletRequest.StandardMultipartFile sourceXmlFile) {
        logger.debug("getXmlContent()")

        def xmlContent = sourceXmlFile.getInputStream()?.getText()
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
    def importProductsXml(def xmlContent) {
        logger.debug("importProductsXml()")

        def productId
        def countSuccessful = 0, countError = 0
        logger.debug("Запуск парсинга XML")
        xmlContent.products.product.each { product ->
            try {
                logger.debug("Импортируется сущность с наименованием: ${product.title[0].text()}")

                //Если текущее поле product_id пустое или не существует - берем предыдущее известное значение
                productId = product.product_id[0].text() ?: productId

                //Если поле inet_price отсутствует или пустое - обнуляем поле price сущности, иначе - берем значение поля price
                def price = product.inet_price[0]?.text()?.length() > 0 ? product.price[0]?.text() ?: 0 : 0

                def rating = Float.valueOf(product.rating[0]?.text()) ?: Float.valueOf("0")

                def entity = new Product(
                        productId: productId,
                        title: product.title[0]?.text(),
                        description: product.description[0]?.text(),
                        rating: rating,
                        price: price,
                        image: product.image[0]?.text()
                )

                //расчитываем категорию товара
                def category = categoryService.getByRating(rating);

                entity.category = category
                logger.debug("Результирующая сущность product: ${entity}, category: ${entity.category}")
                entity.save(flush: true, failOnError: true);

                countSuccessful++
            } catch (NumberFormatException | ValidationException e) {
                logger.error("Ошибка парсинга записи product: ${e.getMessage()}")
                countError++
            }
        }
        logger.debug("Импорт завершен! Импортировано ${countSuccessful} объектов. Ошибок при импорте: ${countError}")

        return "Импорт завершен! Импортировано ${countSuccessful} объектов. Ошибок при импорте: ${countError}"
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
}
