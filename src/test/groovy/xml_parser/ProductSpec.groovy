package xml_parser

import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

@TestFor
class ProductSpec extends Specification  implements GrailsUnitTest {

    def product

    def setup() {
        //product = new Product()
    }

    def cleanup() {
    }

    void validateProductId1() {
        given:
        def product2 = new Product(
                productId: 0,
                title: "1",
                description: "1",
                rating: 1f,
                price: new BigDecimal("100"),
                image: "1",
                category: null
        )
        when:
            product2.productId  = -1
        then:
            !product2.validate()
    }
}