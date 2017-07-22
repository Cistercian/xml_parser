package xml_parser

class BootStrap {
    def categoryService

    def init = { servletContext ->
        println("Init app")
        categoryService.initMockData();

        //assert "а А" ==~ '^[а-яА-Я0-9 ]+$'

    }
    def destroy = {
    }
}
