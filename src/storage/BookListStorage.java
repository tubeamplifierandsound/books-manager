package storage;

import books.Book;
import java.util.List;
import java.io.IOException;

public interface BookListStorage {
    void saveList(List<Book> booksL) throws IOException;
    List<Book> takeList() throws IOException;
}
