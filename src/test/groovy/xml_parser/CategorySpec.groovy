package xml_parser

import grails.testing.gorm.DomainUnitTest
import org.grails.testing.GrailsUnitTest
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

class CategorySpec extends Specification  implements DomainUnitTest<Product> {

    def setup() {

    }

    def cleanup() {
    }

    void "simple creating entity"() {
        setup:
        new Category(grade: (byte) 0 , name: "name").save()
        new Category(grade: (byte) 1 , name: "another").save()

        expect:
        Category.count() == 2
    }
}