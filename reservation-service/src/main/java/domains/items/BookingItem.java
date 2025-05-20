package domains.items;

import domains.reserve.Reservation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "details", nullable = false)
    private String details;

    @OneToMany(mappedBy = "bookingItem")
    private List<Reservation> reservations;

}
