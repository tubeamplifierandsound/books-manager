package service;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import books.*;
import exceptions.*;
import storage.*;
import java.util.function.Function;
//import interfaces.GetterDelegate;
import interfaces.TaskDelegate;
//import exceptions.DuplicateBooksException;
//import exceptions.NonExistentBookException;
import concurrent.TaskQueue;

public class BookListService {
    private List<Book> booksList = new ArrayList<Book>();
    private BookListStorage bookStorage;

    private TaskQueue threadPool;
    public BookListService(String storageInfo){
        bookStorage = new BinFileBookListStorage(storageInfo);
        int coreNum = Runtime.getRuntime().availableProcessors();
        threadPool = new TaskQueue(coreNum);
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
//        Function<Book, Comparable<?>> getter = getterByTag(tag);
//        if(getter == null){
//            return null;
//        }
//        return (Comparator<Book>) Comparator.comparing(getter);

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

    public List<Book> FindBookByTag(BookTag tag, Object value){
        // реализовать теги
        //


        List<Book> foundBooks = new ArrayList<>();

        int threadsBookNum = 100;
        int maxThreadNum = threadPool.getThreadNum();
        int countedThrNum = booksList.size() / threadsBookNum;

        int threadsNum;
        if(maxThreadNum >= countedThrNum){
            threadsNum = countedThrNum;
        }else{
            threadsNum = maxThreadNum;
            threadsBookNum = booksList.size() / threadsNum;
        }

        Function<Book, ?> getter = getterByTag(tag);
        TaskDelegate taskPartly;

        if(getter != null){
            //int startInd = 0;
            for(int i = 0; i < threadsNum; i++){
                final int startInd = i * threadsBookNum;
                final int endInd = i < threadsNum - 1 ? startInd + threadsBookNum : booksList.size();
                taskPartly = ()->{
                    findPartly(value, getter, foundBooks, startInd, endInd);
                };
                threadPool.EnqueueTask(taskPartly);
            }
            // дождаться завершения
            threadPool.waitAllTasks();
        }
        return foundBooks;
    }

    private void findPartly(Object val, Function<Book, ?> getMethod, List<Book> found, int startInd, int searchBorder){
        Book checkBook;
        for(int i = startInd; i < searchBorder; i++){
            checkBook = booksList.get(i);
            if(val.equals(getMethod.apply(checkBook)) ){
                synchronized (found){
                    found.add(checkBook);
                }
            }
        }
    }


    private Function<Book, ?> getterByTag(BookTag tag){
        if(tag == null){
            return null;
        }
        switch(tag){
            case ISBN:
                return Book::getISBN;
            case AUTHOR:
                return Book::getAuthor;
            case NAME:
                return Book::getName;
            case PUBLISHER:
                return Book::getPublisher;
            case PUBL_YEAR:
                return Book::getPublYear;
            case PAGES_NUMBER:
                return Book::getPagesNumber;
            case PRICE:
                return Book::getPrice;
        }
        return null;
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
