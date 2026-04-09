package by.slava_borisov.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_copies")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"book", "borrowRecords"})
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "inventory_number", nullable = false, unique = true, length = 100)
    private String inventoryNumber;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "condition_description")
    private String conditionDescription;

    @OneToMany(mappedBy = "bookCopy")
    private Set<BorrowRecord> borrowRecords = new HashSet<>();
}