package xml_parser

import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class ViewCounterSpec extends Specification implements GrailsUnitTest {

    ViewCounter viewCounter = new ViewCounter(count: Short.MAX_VALUE, timestamp: Long.MAX_VALUE)

    def setup() {
    }

    def cleanup() {
    }

    void getValues() {
        expect:
        viewCounter.count == Short.MAX_VALUE | viewCounter.timestamp == Long.MAX_VALUE
    }
}