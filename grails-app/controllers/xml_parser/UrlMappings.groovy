package xml_parser

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        //"/"(view:"/index")
        "/" (controller: "Product")
        "/statistics" (controller: "ViewCounter")
        "/data" (controller: "Data")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
