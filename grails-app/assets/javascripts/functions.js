/**
 * Created by Olaf on 23.07.2017.
 */
$(document).ready(function () {
    /**
     * Функция валидации поля с id inputTitle. Недопустимые символы удаляются.
     */
    $('#inputTitle').keyup(function () {
        $(this).val($(this).val().replace(/[^а-яА-Я0-9 ]/g, function () {
            return '';
        }));
    });
})