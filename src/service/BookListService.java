package service;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import books.*;
import exceptions.*;
import storage.*;
//import exceptions.DuplicateBooksException;
//import exceptions.NonExistentBookException;

public class BookListService {
    private List<Book> booksList = new ArrayList<Book>();
    private BookListStorage bookStorage;

    public BookListService(String storageInfo){
        bookStorage = new BinFileBookListStorage(storageInfo);
    }

    public void AddBook(Book addBook) throws DuplicateBooksException {
        if(isBookOccur(addBook)){
            throw new DuplicateBooksException("The repetition of book objects in the collection is forbidden. Book already exists: " +
                    addBook.getISBN() + "; " + addBook.getName());
        }
        booksList.add(addBook);
    }

    public void RemoveBook(Book delBook) throws NonExistentBookException{
        if(isBookOccur(delBook)){
            booksList.remove(delBook);
        }else{
            throw new NonExistentBookException("It's not possible to delete a book because it isn't in the collection (book: " +
                    delBook.getISBN() + "; " + delBook.getName() + ")");
        }
    }

    public boolean isBookOccur(Book extBook){
        for(Book intBook: booksList){
            if(intBook.equals(extBook)){
                return true;
            }
        }
        return false;
    }


    public void SortBooksByTag(BookTag tag){
        Comparator<Book> comparator = comparatorByTag(tag);
        // if null value is occurred, there will be natural sorting
        booksList.sort(comparator);
    }

    private Comparator<Book> comparatorByTag(BookTag tag){
        if(tag == null){
            return null;
        }
        switch(tag){
            case AUTHOR:
                return Comparator.comparing(Book::getAuthor);
            case NAME:
                return Comparator.comparing(Book::getName);
            case PUBLISHER:
                return Comparator.comparing(Book::getPublisher);
            case PUBL_YEAR:
                return Comparator.comparing(Book::getPublYear);
            case PAGES_NUMBER:
                return Comparator.comparing(Book::getPagesNumber);
            case PRICE:
                return Comparator.comparing(Book::getPrice);
        }
        return null;
    }


    public void saveToStorage(){
        try{
            bookStorage.saveList(booksList);
        }catch(IOException e){
            System.out.print("Error saving books to storage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void takeFromStorage(){
        try{
            booksList = bookStorage.takeList();
        } catch(IOException e){
            System.out.print("Error taking books from storage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void FindBookByTag(){

    }

    @Override
    public String toString(){
        String allBooks = "";
        for(Book book : booksList){
            allBooks += book.toString() + "\n";
        }
        return allBooks;
    }
}
