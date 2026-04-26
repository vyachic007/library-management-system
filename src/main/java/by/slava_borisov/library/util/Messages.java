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
}
