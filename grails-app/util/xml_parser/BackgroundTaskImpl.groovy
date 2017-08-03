package xml_parser

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets
import java.nio.file.*

/**
 * Класс, реализующий фоновой импорт данных из xml-файла по расписанию
 */
@Component
class BackgroundTaskImpl implements BackgroundTask {

    def productService

    private static final Logger logger = LoggerFactory.getLogger(BackgroundTaskImpl.class)

    private static final String IMPORT_FILENAME = "C:/JAVA/IMPORT/import.xml"   //путь до импортируемого файла
    private static final String ARC_PATH = "C:/JAVA/ARC"                        //папка с хранимым архивом
    public static final String LOG_PATH = "C:/JAVA/LOG"                         //папка с логами
    public static final String LOG_FILENAME = "log.txt"                         //имя лог файла

    private static final BufferedWriter logWriter

    static {
        //TODO: почему игнорируется?
        checkDir(Paths.get(ARC_PATH))
        checkDir(Paths.get(LOG_PATH))

        logWriter = Files.newBufferedWriter(Paths.get("${LOG_PATH}/${LOG_FILENAME}"),
                StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /**
     * Функция запуска импорта xml файла в фоновом режиме. Вызывается через спринг (/conf/spring/resources.groovy)
     * по заданному в указанном файле шедулеру
     *
     * @return void
     */
    @Override
    def importXML() {
        logger.debug("importXML()")

        Path xmlFile = Paths.get(IMPORT_FILENAME)

        if (!Files.isRegularFile(xmlFile)) {
            logger.debug("Отсутствует файл для импорта.")
            return
        }

        String message = productService.parsingInputStream(Files.newInputStream(xmlFile), logWriter)

        String text = "Результат автоматического импорта xml-файла: ${message}"

        logger.info(text)
        logWriter.writeLine(text)

        logWriter.flush();
        arcFile(xmlFile)
    }

    /**
     * Функция переноса обработанного файла в архивную директорию
     *
     * @param pathOriginal  Текущий файл
     * @return  void
     */
    def arcFile(Path pathOriginal) {
        logger.debug("arcFile()")

        logWriter.write("Перемещение обработанного файла в архивную директорию.")

        Path pathDestination = Paths.get(ARC_PATH);

        checkDir(pathDestination)

        Files.move(pathOriginal, Paths.get("${pathDestination.toString()}/${pathOriginal.getFileName()}"),
                StandardCopyOption.REPLACE_EXISTING)

        String text = "Файл перемещен в архивную директорию ${pathDestination.toString()}"

        logger.debug(text)
        logWriter.writeLine(text)

        logWriter.flush()
    }

    /**
     * Функция валидации используемых директорий
     *
     * @param path Проверяемая папка
     * @return void
     */
    static def checkDir(Path path) {
        if (!Files.isDirectory(path)) {
            logger.error("Не найдено директории ${path.toString()}")

            if (Files.isRegularFile(path)) {
                logger.debug("Найден файл с именем директории. Удаление файла.")

                Files.delete(path)
            }

            Files.createDirectories(path);

            logger.debug("Директория создана.")
        }
    }
}
