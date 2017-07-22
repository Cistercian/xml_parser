package xml_parser

import grails.gorm.transactions.Transactional
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest

@Transactional
class ProductService {

    def categoryService

    /**
     * Парсинг xml файла
     * @param file Переданный файл из запроса
     * @return отформатированный результат
     */
    def getXmlContent(StandardMultipartHttpServletRequest.StandardMultipartFile sourceXmlFile){
        println("getXmlContent()")

        def xmlContent = sourceXmlFile.getInputStream()?.getText()
        def parser = new XmlSlurper();

        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        return parser.parseText(xmlContent)
    }

    @Transactional
    def importProductsXml(def xmlContent){
        println("importProductsXml()")

        def productId;
        println("парсинг XML")
        xmlContent.products.product.each { product ->
            println("import entity: ${product.title[0].text()}")

            productId = product.product_id[0].text() ?: productId

            def price = product.inet_price[0].text() != null ?
                    new BigDecimal(product.price[0].text()) :
                    new BigDecimal("0")

            def rating = Float.valueOf(product.rating[0].text()) ?: Float.valueOf(0)

            def entity = new Product(
                    productId: productId,
                    title: product.title[0].text(),
                    description: product.description[0].text(),
                    rating: rating,
                    price: price,
                    image: product.image[0].text() //image: new java.net.URL(product.image[0].text()).bytes
            )

            //расчитываем категорию товара
            def category = categoryService.getByRating(rating);
            println("Category: ${category}")

            entity.category = category
            println("Parsed object: ${entity}, category: ${entity.category}")
            entity.save(flush: true, failOnError: true);

            category.products << entity
            category.save(flush: true)
        }

        return "import complited"
    }

    @Deprecated
    Product fixNumberParsing(Product product){
        println("fixNumberParsing()")

        println("product.rating=${product.rating}, product.price=${product.price}")

        product.rating = Float.valueOf(product.rating.toString())
        product.price = new BigDecimal(product.price.toString())

        println("product.rating=${product.rating}, product.price=${product.price}")

        return product
    }

}
