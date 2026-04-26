package by.slava_borisov.library.util;

public class Messages {

    private Messages() {
    }

    public static final String CATEGORY_ALREADY_EXISTS = "Категория с таким названием уже существует";
    public static final String CATEGORY_NOT_FOUND_BY_ID = "Категория с id %d не найдена";
    public static final String CATEGORY_CANNOT_BE_PARENT_OF_ITSELF = "Категория не может быть родителем самой себя";

    public static final String AUTHOR_ALREADY_EXISTS = "Автор с таким именем и фамилией уже существует";
    public static final String AUTHOR_NOT_FOUND_BY_ID = "Автор с id %d не найден";

    public static final String BOOK_ALREADY_EXISTS_BY_ISBN = "Книга с таким ISBN уже существует";
    public static final String BOOK_NOT_FOUND_BY_ID = "Книга с id %d не найдена";
    public static final String BOOK_NOT_FOUND_BY_ISBN = "Книга с ISBN %s не найдена";

    public static final String BOOK_COPY_ALREADY_EXISTS_BY_INVENTORY_NUMBER =
            "Экземпляр книги с таким инвентарным номером уже существует";
    public static final String BOOK_COPY_NOT_FOUND_BY_ID = "Экземпляр книги с id %d не найден";
    public static final String BOOK_COPY_NOT_FOUND_BY_INVENTORY_NUMBER =
            "Экземпляр книги с инвентарным номером %s не найден";

    public static final String BORROW_RECORD_NOT_FOUND_BY_ID = "Запись аренды с id %d не найдена";
    public static final String BOOK_COPY_IS_NOT_AVAILABLE = "Экземпляр книги недоступен для аренды";
    public static final String BORROW_RECORD_ALREADY_RETURNED = "Книга по этой записи аренды уже возвращена";
    public static final String RETURN_DATE_BEFORE_BORROW_DATE = "Дата возврата не может быть раньше даты выдачи";
    public static final String NEW_DUE_DATE_BEFORE_CURRENT_DUE_DATE =
            "Новая дата возврата не может быть раньше текущего срока возврата";
    public static final String USER_NOT_FOUND_BY_ID = "Пользователь с id %d не найден";

    public static final String USER_EMAIL_ALREADY_EXISTS = "Пользователь с таким email уже существует";

    public static final String USERNAME_ALREADY_EXISTS = "Пользователь с таким именем уже существует";
    public static final String EMAIL_ALREADY_EXISTS = "Пользователь с таким email уже существует";
    public static final String INVALID_USERNAME_OR_PASSWORD = "Неверное имя пользователя или пароль";
    public static final String ROLE_NOT_FOUND_BY_NAME = "Роль с названием %s не найдена";
}
