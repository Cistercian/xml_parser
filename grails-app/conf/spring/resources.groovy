import xml_parser.BackgroundTaskImpl

// Place your Spring DSL code here
beans = {
    backgroundTaskImpl(BackgroundTaskImpl){
        productService = ref('productService')
    }


    xmlns backgroundTask: "http://www.springframework.org/schema/task"

    backgroundTask.'scheduled-tasks'{
        backgroundTask.scheduled(ref:'backgroundTaskImpl', method: 'importXML', cron: '*/5 * * * * *')
    }
}
