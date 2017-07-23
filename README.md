# xml_parser
---
Приложение является примером реализации xml-парсера, сохраняющего данные в БД и реализующего CRUD операции с ними

В качестве сущностей БД (DAO уровень) выступают:
1. Product - главная сущность приложения, данные которой импортируются из загружаемых xml файлов;
2. Category - статическая таблица (не подлежащая изменению), которая соотносится с записями Product по их полю rating;
3. ViewCounter - сущность, представляющая собой счетчик посещения страниц просмотра записей Product. Данные группируются по времени просмотра (в минутах) и id записи Product. Таблица используется для отображения статистических данных.

Структура cтатической таблицы Category:

  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `grade` tinyint(4) NOT NULL,
  `name` varchar(255) NOT NULL
    
, где grade - целочисленное значение оценки категории (0- минимальный уровень, 2 - максимальный),
      name  - наименование категории ("плохая"/"хорошая"/"отличная").
      
Структура таблицы учета просмотров товаров:
 
 `id` bigint(20) NOT NULL AUTO_INCREMENT,
 `version` bigint(20) NOT NULL,
 `product_id` bigint(20) NOT NULL,
 `timestamp` bigint(20) NOT NULL,
 `count` smallint(6) NOT NULL
 
 , где  product_id  - ссылка на запись таблицы Product (товар), с которой свяана текущая запись,
        timestamp   - минута, когда были осуществлены просмотры данного товара,
        count       - количество просмотров данного товара в данную минуту.
        
Структура главной таблицы Product:
  
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `price` decimal(19,2) NOT NULL,
  `product_id` int(11) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `rating` float NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `category_id` bigint(20) NOT NULL,
  `description` longtext
  
, где price       - цена товара,
      product_id  - ID товара в сторонней базе (источник - импортируемый файлы),
      title       - наименование товара,
      rating      - рейтинг товара, использующийся для причисления записи к той или иной категории,
      image       - ссылка на фотографию товара,
      category_id - связанная запись таблицы Category,
      description - описание товара.
