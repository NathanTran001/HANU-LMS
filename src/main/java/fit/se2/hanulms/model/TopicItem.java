//package fit.se2.hanulms.model;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotEmpty;
//import org.hibernate.validator.constraints.Length;
//
//import java.util.List;
//
//@Entity
//public class TopicItem {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Length(max = 40, message = "Max title length is 40 characters!")
//    private String title;
//
//    @OneToOne(mappedBy = "topicItem", cascade = CascadeType.REMOVE)
//    private File file;
//
//    private String url;
//
//    @ManyToOne
//    private Topic topic;
//
//    @Enumerated(EnumType.STRING)
//    private TopicItemType type;
//}
