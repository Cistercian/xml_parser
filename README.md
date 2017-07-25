# xml_parser
---
Приложение является примером реализации xml-парсера с веб-интерфейсом, сохраняющего данные в БД и реализующего CRUD операции с ними, реализованного на фреймворке Grails. 

**Краткое описание:**

Парсинг файлов осуществляется библиотекой XMLSlurper. 
Веб интерфейс представлен набором динамических gsp страниц с использованием фреймворка bootstrap и jquery.flot.js (для отображения графика по JSON данным, полученным с сервера). 
Парсинг реализован с использованием многопоточности. 
Помимо ручного импорта файла (посредством POST запроса со страницы сайта), предусмотрена возможность автоматического фонового импорта по расписанию с отображением протоколов работы процесса на сайте.

**Развернутое приложение:** <http://5.189.96.147:82>
***

### В качестве сущностей БД (DAO уровень) выступают:
1. **Product** - главная сущность приложения, данные которой импортируются из загружаемых xml файлов;
2. **Category** - статическая таблица (не подлежащая изменению), которая соотносится с записями Product по их полю rating;
3. **ViewCounter** - сущность, представляющая собой счетчик посещения страниц просмотра записей Product. Данные группируются по времени просмотра (в минутах) и id записи Product. Таблица используется для отображения статистических данных.

### Структура cтатической таблицы Category:

  * `id` bigint(20) NOT NULL AUTO_INCREMENT,
  * `version` bigint(20) NOT NULL,
  * `grade` tinyint(4) NOT NULL,
  * `name` varchar(255) NOT NULL
    
, где grade - целочисленное значение оценки категории (0- минимальный уровень, 2 - максимальный),
      name  - наименование категории ("плохая"/"хорошая"/"отличная").
      
### Структура таблицы учета просмотров товаров:
 
 * `id` bigint(20) NOT NULL AUTO_INCREMENT,
 * `version` bigint(20) NOT NULL,
 * `product_id` bigint(20) NOT NULL,
 * `timestamp` bigint(20) NOT NULL,
 * `count` smallint(6) NOT NULL
 
 , где  product_id  - ссылка на запись таблицы Product (товар), с которой свяана текущая запись,
        timestamp   - минута, когда были осуществлены просмотры данного товара,
        count       - количество просмотров данного товара в данную минуту.
        
### Структура главной таблицы Product:
  
  * `id` bigint(20) NOT NULL AUTO_INCREMENT,
  * `version` bigint(20) NOT NULL,
  * `price` decimal(19,2) NOT NULL,
  * `product_id` int(11) NOT NULL,
  * `title` varchar(255) DEFAULT NULL,
  * `rating` float NOT NULL,
  * `image` varchar(255) DEFAULT NULL,
  * `category_id` bigint(20) NOT NULL,
  * `description` longtext
  
, где price       - цена товара,
      product_id  - ID товара в сторонней базе (источник - импортируемые файлы),
      title       - наименование товара,
      rating      - рейтинг товара, использующийся для причисления записи к той или иной категории,
      image       - ссылка на фотографию товара,
      category_id - связанная запись таблицы Category,
      description - описание товара.

### Схема БД:
 ![Схема](http://5.189.96.147/db_structure.png)

### Уровень контроллеров
1. **DataController** - реализует функции импорта xml-файла через страницу сайта (используется view /data/data);
2. **ViewCounterController** - реализует ответ с данными таблицы view_counter (счетчики просмотров) в формате JSON на ajax запрос из view /viewCounter/statistics;
3. **ProductController** - контроллер, манипулирующий данными таблицы product. Реализует CRUD операции над записями данной таблицы. Связан со следующими view: /product/index, /product/show, /product/edit, /product/create.

### Уровень сервисов:
1. **CategoryService** - работа с данными таблицы category. Реализуемые функции:
* `void initMockData()` - заполняет таблицу данными с категориями с наименованиями "Плохой/Хороший/Отличный" при их отсутствии. Далее в приложении таблица больше не редактируется.
* `Category getByRating(Float rating)` - функция возвращает запись категории, соответсвующую переданному рейтингу, по заданному условию:
* Рейтинг <= 3 - "плохой";
* 3 < рейтинг <= 4 - "хороший";
* 4 < рейтинг <= 5 - "отличный";
* в иных случаях - "плохой".

2. **ViewCounterService** - работа с таблицей учета счетчиков просмотра записей товаров (view_counter). Функции:
* `def synchronized incrementCounter(Product product)` - синхронизированная функция, увеличивающая счетчик для записи таблицы, соответсвующей переданному товару.

3. **ProductService** - сервис для работы с главной таблицей product. Функции:
* `def parsingInputStream(InputStream inputStream, BufferedWriter logWriter)` - реализация функции импорта xml-контента из переданного потока с возможностью записи лога в отдельный поток записи (используется при автоматическом фоновом импорте для последующего отображение его результата на сайте)
* `private def getXmlContent(InputStream inputStreamXML)` - вспомогательная функция (вызывается в `arsingInputStream`), реализующая парсинг xml-контента из переданного потока с помощью бибилиотеки XmlSlurper. Возвращает объект, несущий в себе содержимое структуры.
* `private def importXmlToDB(def xmlContent)` - вспомогательная функция (вызывается в `parsingInputStream`), реализующая инициализацию распарсенных объектов product с последующим их сохранением в базу данных. Возвращает результат испорта в виде строки "Импорт завершен! Импортировано {0} объектов. Ошибок при импорте: {1}. Время обработки файла: {2} ms." Функция реализует работу в многопоточном режиме по шаблону producer-consumer.

Логика парсинга полей объекта класса product предусматривает выполнение условий:
* Если не задано поле product_id, то берется значение предыдущего объекта, где оно было (при инициализации метода значение принимается равным 0);
* Свойство price объекта Product парсится только в том случае, если в xml есть поле inet_price (т.е. если inet_price не пусто, то берем price).
* После вычисления price расчитывается соответствующая ему категория.

### Текущие характеристики логики многопоточности (задаются статическими переменными класса `ProductService`):
* `Кол-во producer` - 1 (реализация внутреннего класса Producer, осуществляющего создание и заполнение объекта product). Поток работает до завершения чтения файла;
* `Кол-во consumer` - 10 (внутренний класс Consumer, осуществляет сохранение в БД переданных через BlockingQueue объектов Product). Потоки запускаются в статическом пуле Executors.newFixedThreadPool(10). Потоки работают до тех пор, пока не завершен поток producer либо читаемая очередь с Product не пуста;
* `Размер используемой блокируемой очереди BlockingQueue` - 50.

### Функция автоматического фонового парсинга файлов

Фоновый парсинг xmlфайлов реализован классом BackgroundTaskImpl, подключенного к Spring через конфигурационный файл /conf/spring/resources.groovy с указанием в нем вызова метода importXML по расписанию ('scheduled-tasks').

В классе BackgroundTaskImpl статическими переменными обозначены параметры импорта: путь до импортируемого файла, путь до архивной папки (куда перемещается файл после обработки), путь до файла с логами процесса (для последующего отображения его на сайте).
Функции класса:
* `def importXML()` - функция инициализации процесса с последующим вызовом функции парсинга (`parsingInputStream(InputStream inputStream, BufferedWriter logWriter)` сервиса ProductService);
* `def arcFile(Path pathOriginal)` - функция перемещает файл pathOriginal в архивный каталог;
* `static def checkDir(Path path)` - служебная функция создания при необходимости используемых каталогов.

### Фронт-енд

Фронт-енд представлен сайтом, состоящим из 3 разделов:
* CRUD операции объектов Product и просмотр их в таблице;
* Страница импорта xml файла для парсинга и просмотр логов работы процедуры фонового импорта;
* Отображение графика зависимости общего количества просмотров Product от времени.

**Раздел CRUD операций**
Главная страница (маппинг на "/", "/index"), на которой представлена  общая таблица с полной информацией по таблице product. При нажатии на названия столбцов происходит сортировка содержимого, при нажатии на значение столбов "Наименование" или "Ссылка на картинку" (поле image сохраняется в БД как строка и далее отображается как img) происходит переход на страницу просмотра отдельной записи Product. 

На странице просмотра помимо основных полей таблицы Product отображаются свойства связанных записей (Category - в зависимости от grade меняется кол-во "звездочек". ViewCounter - кол-во предыдущих просмотров данной записи). На странице есть кнопки удаления записи (с запросом подтверждения выбора), создания новой и возврата на главную страницу.

При сохранении записи производится валидация поля Наименование (реализовано и на стороне фронта на js, и на стороне бек-енда в функциях ручного сохранения (эта валидация игнорируется при импорте xml файла).

Страница импорта позволяет указать файл, который будет отправлен на сервер с последующим отображением результата в виде "Импорт завершен! Импортировано 1 объектов. Ошибок при импорте: 0. Время обработки файла: 7 ms.". Здесь же отображается содержимое лога работы процедуры автоматиечского импорта.

Страница отображения графика позволяет изучить общую статистику просмотров товаров в зависимости от времени с возможностью приближения/отдаления временных интервалов.
