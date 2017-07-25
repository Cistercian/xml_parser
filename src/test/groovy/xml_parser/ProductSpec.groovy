package xml_parser

import grails.testing.gorm.DomainUnitTest
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

class ProductSpec extends Specification implements DomainUnitTest<Product> {

    @Shared
    int id

    def setup() {
        //product = new Product()
    }

    def cleanup() {
    }

    void "test simple saving"() {
        setup:
        new Product(
                productId: 0,
                title: "title",
                description: "description",
                rating: 1f,
                price: new BigDecimal("100"),
                image: "image",
                category: new Category(grade: (byte) 1, name: "1")
        ).save()

        expect:
        Product.count() == 1
    }

    void "validation not positive productId"() {
        setup:
        domain.productId = -1
        domain.validate()

        expect:
        domain.errors.getFieldError('productId') != null
    }

    void "validation too big productId"() {
        setup:
        domain.productId = Long.MAX_VALUE
        domain.validate()

        expect:
        domain.errors.getFieldError('productId') != null
    }

    void "validation nullable of title"() {
        setup:
        domain.title = null
        domain.validate()

        expect:
        domain.errors.getFieldError("title") == null
    }

    void "validation length of title"() {
        setup:
        domain.title = String.format("%-256s", " ")
        domain.validate()

        expect:
        domain.errors.getFieldError("title") != null
    }
}