import books.BookTag;
import exceptions.DuplicateBooksException;
import exceptions.NonExistentBookException;
import service.BookListService;
import books.Book;

import java.math.BigDecimal;


// Debug
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args) {

        BookListService bList = new BookListService("src/data/books.bin");
        Book b1 = new Book("978-0-452-28423-4", "George Orwell", "1984", "Secker & Warburg",
                1948, 328, new BigDecimal(15.99));
        Book b2 = new Book("978-0-452-28423-5", "J.K. Rowling", "Harry Potter and the Sorcerer's Stone", "Scholastic Inc.",
                1997, 309, new BigDecimal(19.99));
        Book b3 = new Book("978-0-06-112008-4", "Harper Lee", "To Kill a Mockingbird", "J.B. Lippincott & Co.",
                1960, 281, new BigDecimal(10.99));
        Book b4 = new Book("978-1-56619-909-4", "F. Scott Fitzgerald", "The Great Gatsby", "Charles Scribner's Sons",
                1925, 180, new BigDecimal(14.99));

        Book[] bArr = {b1, b2, b3, b4};
        int bRealNum = 4;

        // Adding all books, with the first book being added twice
        for(int i = 0; i < bArr.length + 1; i++){
            try{
                bList.AddBook(bArr[i % bRealNum]);
            }catch (DuplicateBooksException e){
                System.out.println(e.getMessage());
            }
        }

        // Deleting the first two books twice
        for(int i = 0; i < bArr.length; i++){
            try{
                bList.RemoveBook(bArr[i % 2]);
            }catch(NonExistentBookException e){
                System.out.println(e.getMessage());
            }
        }

        // Adding two deleted books back
        for(int i = 0; i < 2; i++){
            try{
                bList.AddBook(bArr[i]);
            }catch (DuplicateBooksException e){
                System.out.println(e.getMessage());
            }
        }

        System.out.println("\n\nList output before sorting: \n" + bList.toString() + "\n");
        bList.SortBooksByTag(BookTag.PUBL_YEAR);
        System.out.println("\n\nList output after sorting by year of publication: \n" + bList.toString() + "\n");
        bList.SortBooksByTag(null);
        System.out.println("\n\nList output after sorting by ISBN (natural - compareTo): \n" + bList.toString() + "\n");

        bList.saveToStorage();
        // Deleting all books from list
        for(int i = 0; i < bArr.length; i++){
            try{
                bList.RemoveBook(bArr[i]);
            }catch(NonExistentBookException e){
                System.out.println(e.getMessage());
            }
        }
        bList.takeFromStorage();
        System.out.println("\n\nList output after storage (should be the same as after the last sorting): \n" + bList.toString() + "\n");


        System.out.println("\n\nFound books: \n");
        List<Book> foundBook = bList.FindBookByTag(BookTag.PAGES_NUMBER, 328);
        for(Book book : foundBook ){
            System.out.println(book.toString());
        }
    }
}

