package xml_parser

import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class CategorySpec extends Specification implements GrailsUnitTest {

    Category category

    def setup() {
        category = new Category(grade: (byte) 0, name: "Плохой")
    }

    def cleanup() {
    }

    void creatingValue() {
        expect:"fix me"
        category != null
    }

    void getGrade(){
        expect:
        category.grade == (byte) 0
    }

    void getName(){
        expect:
        "Плохой".equals(category.name)
    }
}