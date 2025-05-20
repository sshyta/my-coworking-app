package services.impls;

import domains.items.BookingItem;
import domains.repositories.BookingItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.interfaces.BookingItemService;

import java.util.List;
import java.util.Optional;

@Service
public class BookingItemServiceImpl implements BookingItemService {

    private final BookingItemRepository bookingItemRepository;

    @Autowired
    public BookingItemServiceImpl(BookingItemRepository bookingItemRepository) {
        this.bookingItemRepository = bookingItemRepository;
    }

    public List<BookingItem> getAllBookingItems() {
        return bookingItemRepository.findAll();
    }

    public Optional<BookingItem> getBookingItemById(Integer id) {
        return bookingItemRepository.findById(id);
    }

    public BookingItem createBookingItem(BookingItem bookingItem) {
        return bookingItemRepository.save(bookingItem);
    }

    public BookingItem updateBookingItem(Integer id, BookingItem bookingItem) {
        if (bookingItemRepository.existsById(id)) {
            bookingItem.setItemId(id);
            return bookingItemRepository.save(bookingItem);
        }
        return null;
    }

    public boolean deleteBookingItem(Integer id) {
        if (bookingItemRepository.existsById(id)) {
            bookingItemRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
