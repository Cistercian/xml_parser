package xml_parser

import grails.testing.gorm.DomainUnitTest
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class ProductSpec extends Specification  implements DomainUnitTest<Product> {

    @Shared int id

    def setup() {
        //product = new Product()
    }

    def cleanup() {
    }

    void "test simple saving"(){
        setup:
            new Product(
                    productId: 0,
                    title: "title",
                    description: "description",
                    rating: 1f,
                    price: new BigDecimal("100"),
                    image: "image",
                    category: null
            ).save

        expect:
            Product.count() == 1
    }

    void validateProductId1() {

    }
}