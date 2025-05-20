package services.interfaces;

import domains.items.BookingItem;

import java.util.List;
import java.util.Optional;

public interface BookingItemService {

    List<BookingItem> getAllBookingItems();
    Optional<BookingItem> getBookingItemById(Integer id);
    BookingItem createBookingItem(BookingItem bookingItem);
    BookingItem updateBookingItem(Integer id, BookingItem bookingItem);
    boolean deleteBookingItem(Integer id);
}
