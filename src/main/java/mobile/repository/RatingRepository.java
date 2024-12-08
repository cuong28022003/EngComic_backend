package mobile.repository;

import mobile.model.Entity.Rating;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends MongoRepository<Rating, ObjectId> {
    // Tìm tất cả các đánh giá của một truyện
    List<Rating> findByComic_Id(ObjectId comicId);

    // Tìm tất cả các đánh giá của một người dùng
    List<Rating> findByUser_Id(ObjectId userId);

    // Tìm đánh giá của một người dùng trên một truyện cụ thể
    Rating findByUser_IdAndComic_Id(ObjectId userId, ObjectId comicId);
}
