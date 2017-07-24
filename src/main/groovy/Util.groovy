import xml_parser.BackgroundTaskImpl

import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by d.v.hozyashev on 24.07.2017.
 */

class Util {

    /**
     * Функция вывода содержимого лог файла фонового импорта (для отображения в gsp)
     * Путь до файла задан статически
     *
     * @return List<String>
     */
    public static def getSchedulerLog(){
        return Files.readAllLines(Paths.get("${BackgroundTaskImpl.LOG_PATH}/${BackgroundTaskImpl.LOG_FILENAME}")).reverse()
    }
}
